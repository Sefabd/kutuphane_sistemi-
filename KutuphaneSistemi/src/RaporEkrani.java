import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class RaporEkrani extends JFrame {
    // Dinamik Sorgu Bileşenleri
    private JTextField txtKitapAdi, txtYazar, txtYilMin, txtYilMax;
    private JComboBox<String> cmbKategori;
    private JCheckBox chkSadeceMevcut;
    
    // Tarih Aralığı Raporu Bileşenleri
    private JTextField txtBaslangicTarih, txtBitisTarih; // Format: YYYY-MM-DD

    private JTable table;
    private DefaultTableModel model;

    public RaporEkrani() {
        setTitle("Gelişmiş Raporlama ve Dinamik Sorgu");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // SOL PANEL: KONTROLLER
        JPanel pnlSol = new JPanel();
        pnlSol.setLayout(new BoxLayout(pnlSol, BoxLayout.Y_AXIS));
        pnlSol.setPreferredSize(new Dimension(300, 0));
        pnlSol.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. BÖLÜM: DİNAMİK SORGULAMA 
        JPanel pnlDinamik = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlDinamik.setBorder(BorderFactory.createTitledBorder("1. Detaylı Kitap Arama"));
        
        txtKitapAdi = new JTextField();
        txtYazar = new JTextField();
        cmbKategori = new JComboBox<>();
        cmbKategori.addItem("Tümü"); // Varsayılan
        kategorileriYukle();
        
        txtYilMin = new JTextField();
        txtYilMax = new JTextField();
        chkSadeceMevcut = new JCheckBox("Sadece Stokta Olanlar");

        pnlDinamik.add(new JLabel("Kitap Adı:")); pnlDinamik.add(txtKitapAdi);
        pnlDinamik.add(new JLabel("Yazar:")); pnlDinamik.add(txtYazar);
        pnlDinamik.add(new JLabel("Kategori:")); pnlDinamik.add(cmbKategori);
        pnlDinamik.add(new JLabel("Min. Yıl:")); pnlDinamik.add(txtYilMin);
        pnlDinamik.add(new JLabel("Max. Yıl:")); pnlDinamik.add(txtYilMax);
        pnlDinamik.add(chkSadeceMevcut);
        
        JButton btnDinamikAra = new JButton("SORGULA");
        btnDinamikAra.setBackground(new Color(52, 152, 219));
        btnDinamikAra.setForeground(Color.WHITE);
        pnlDinamik.add(btnDinamikAra);

        // 2. BÖLÜM: TARİH ARALIĞI RAPORU 
        JPanel pnlTarih = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlTarih.setBorder(BorderFactory.createTitledBorder("2. Tarih Aralığı Raporu"));
        
        txtBaslangicTarih = new JTextField("2025-01-01"); // Örnek format
        txtBitisTarih = new JTextField("2025-12-31");
        
        pnlTarih.add(new JLabel("Başlangıç (YYYY-MM-DD):")); pnlTarih.add(txtBaslangicTarih);
        pnlTarih.add(new JLabel("Bitiş (YYYY-MM-DD):")); pnlTarih.add(txtBitisTarih);
        
        JButton btnTarihRapor = new JButton("LİSTELE");
        btnTarihRapor.setBackground(Color.ORANGE);
        pnlTarih.add(btnTarihRapor);

        // 3. BÖLÜM: DİĞER RAPORLAR
        JPanel pnlDiger = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlDiger.setBorder(BorderFactory.createTitledBorder("3. Hazır Raporlar"));
        
        JButton btnGeciken = new JButton("Geciken Kitaplar");
        JButton btnPopuler = new JButton("En Çok Okunanlar");
        JButton btnUyeler = new JButton("Tüm Üyeler");

        pnlDiger.add(btnGeciken);
        pnlDiger.add(btnPopuler);
        pnlDiger.add(btnUyeler);

        // Panelleri Sol Tarafa Ekle
        pnlSol.add(pnlDinamik);
        pnlSol.add(Box.createVerticalStrut(10));
        pnlSol.add(pnlTarih);
        pnlSol.add(Box.createVerticalStrut(10));
        pnlSol.add(pnlDiger);
        pnlSol.add(Box.createVerticalStrut(20));
        
        // EXCEL BUTONU
        JButton btnExcel = new JButton("TABLOYU EXCEL'E AKTAR");
        btnExcel.setBackground(new Color(39, 174, 96));
        btnExcel.setForeground(Color.WHITE);
        btnExcel.setPreferredSize(new Dimension(0, 40));
        pnlSol.add(btnExcel);

        add(pnlSol, BorderLayout.WEST);

        //  ORTA PANEL: TABLO 
        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // - AKSİYONLAR 

        // 1. DİNAMİK SORGULAMA AKSİYONU
        btnDinamikAra.addActionListener(e -> {
            model.setColumnIdentifiers(new Object[]{"Kitap Adı", "Yazar", "Kategori", "Yayınevi", "Basım Yılı", "Stok"});
            model.setRowCount(0);
            try (Connection conn = Database.baglantiGetir()) {
                String sql = "SELECT k.*, c.KategoriAdi FROM KITAP k JOIN KATEGORI c ON k.KategoriID = c.KategoriID WHERE 1=1";
                
                // Filtreler
                if (!txtKitapAdi.getText().isEmpty()) sql += " AND k.KitapAdi LIKE '%" + txtKitapAdi.getText() + "%'";
                if (!txtYazar.getText().isEmpty()) sql += " AND k.Yazar LIKE '%" + txtYazar.getText() + "%'";
                
                // Kategori Seçimi (Tümü değilse)
                if (cmbKategori.getSelectedIndex() > 0) {
                    String secilen = (String) cmbKategori.getSelectedItem();
                    int katID = Integer.parseInt(secilen.split("-")[0]);
                    sql += " AND k.KategoriID = " + katID;
                }
                
                // Yıl Aralığı
                if (!txtYilMin.getText().isEmpty()) sql += " AND k.BasimYili >= " + txtYilMin.getText();
                if (!txtYilMax.getText().isEmpty()) sql += " AND k.BasimYili <= " + txtYilMax.getText();
                
                // Stok Checkbox
                if (chkSadeceMevcut.isSelected()) sql += " AND k.MevcutAdet > 0";

                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("KitapAdi"), rs.getString("Yazar"), rs.getString("KategoriAdi"), rs.getString("Yayinevi"), rs.getInt("BasimYili"), rs.getInt("MevcutAdet")});
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });

        // 2. TARİH ARALIĞI RAPORU AKSİYONU
        btnTarihRapor.addActionListener(e -> {
            model.setColumnIdentifiers(new Object[]{"Üye", "Kitap", "Veriliş Tarihi", "Teslim Tarihi", "Durum"});
            model.setRowCount(0);
            try (Connection conn = Database.baglantiGetir()) {
                String sql = "SELECT CONCAT(u.Ad, ' ', u.Soyad), k.KitapAdi, o.OduncTarihi, o.TeslimTarihi, " +
                             "CASE WHEN o.TeslimTarihi IS NULL THEN 'Okuyor' ELSE 'İade Etti' END " +
                             "FROM ODUNC o JOIN UYE u ON o.UyeID = u.UyeID JOIN KITAP k ON o.KitapID = k.KitapID " +
                             "WHERE o.OduncTarihi BETWEEN ? AND ?";
                
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtBaslangicTarih.getText() + " 00:00:00");
                ps.setString(2, txtBitisTarih.getText() + " 23:59:59");
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getTimestamp(3), rs.getTimestamp(4), rs.getString(5)});
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Tarih Formatı Hatalı! (YYYY-MM-DD kullanın)\nHata: " + ex.getMessage()); }
        });

        // 3. RAPORLAR
        btnGeciken.addActionListener(e -> {  
             basitSorguCalistir("GECIKEN");
        });
        btnPopuler.addActionListener(e -> { 
             basitSorguCalistir("POPULER");
        });
        btnUyeler.addActionListener(e -> { 
             basitSorguCalistir("UYELER");
        });

        // 4. EXCEL AKTAR
        btnExcel.addActionListener(e -> excelAktar());
    }

    // Kategorileri Combobox'a Doldur
    private void kategorileriYukle() {
        try (Connection conn = Database.baglantiGetir()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM KATEGORI");
            while (rs.next()) {
                cmbKategori.addItem(rs.getInt("KategoriID") + "-" + rs.getString("KategoriAdi"));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Kod tekrarını önlemek için yardımcı metot 
    private void basitSorguCalistir(String tip) {
        model.setRowCount(0);
        try (Connection conn = Database.baglantiGetir()) {
            String sql = "";
            if (tip.equals("GECIKEN")) {
                model.setColumnIdentifiers(new Object[]{"Üye", "Kitap", "Son Teslim", "Gecikme (Gün)"});
                sql = "SELECT CONCAT(u.Ad, ' ', u.Soyad), k.KitapAdi, o.SonTeslimTarihi, DATEDIFF(NOW(), o.SonTeslimTarihi) " +
                      "FROM ODUNC o JOIN UYE u ON o.UyeID = u.UyeID JOIN KITAP k ON o.KitapID = k.KitapID " +
                      "WHERE o.TeslimTarihi IS NULL AND NOW() > o.SonTeslimTarihi";
            } else if (tip.equals("POPULER")) {
                model.setColumnIdentifiers(new Object[]{"Kitap Adı", "Ödünç Sayısı"});
                sql = "SELECT k.KitapAdi, COUNT(o.OduncID) as Sayi FROM KITAP k JOIN ODUNC o ON k.KitapID = o.KitapID GROUP BY k.KitapID ORDER BY Sayi DESC LIMIT 10";
            } else if (tip.equals("UYELER")) {
                model.setColumnIdentifiers(new Object[]{"ID", "Ad", "Soyad", "Tel", "Email", "Borç"});
                sql = "SELECT * FROM UYE";
            }

            ResultSet rs = conn.createStatement().executeQuery(sql);
            int colCount = model.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 0; i < colCount; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void excelAktar() {
        if (model.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Veri yok!"); return; }
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
                bw.write("\uFEFF"); // BOM
                for (int i = 0; i < model.getColumnCount(); i++) bw.write(model.getColumnName(i) + ";");
                bw.newLine();
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) bw.write((model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString() : "") + ";");
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Kaydedildi!");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}