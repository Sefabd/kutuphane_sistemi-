import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class KitapYonetimi extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtAd, txtYazar, txtYayinevi, txtYil, txtAdet, txtArama;
    private JComboBox<String> cmbKategori;
    private JLabel lblID; // Seçili kitabın ID'sini tutmak için

    public KitapYonetimi() {
        setTitle("Kitap Yönetimi");
        setSize(1000, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        //  1. ÜST PANEL (ARAMA KISMI)
        JPanel pnlUst = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlUst.setBorder(BorderFactory.createTitledBorder("Kitap Ara"));
        
        txtArama = new JTextField(20);
        JButton btnAra = new JButton("Ara");
        JButton btnHepsiniGoster = new JButton("Tümünü Göster");
        
        pnlUst.add(new JLabel("Kitap Adı veya Yazar:"));
        pnlUst.add(txtArama);
        pnlUst.add(btnAra);
        pnlUst.add(btnHepsiniGoster);
        
        add(pnlUst, BorderLayout.NORTH);

        // 2. ORTA KISIM (TABLO) 
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"ID", "Kitap Adı", "Yazar", "Kategori", "Yayınevi", "Basım Yılı", "Toplam", "Stok"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. ALT PANEL (FORM VE İŞLEMLER) 
        JPanel pnlAlt = new JPanel(new BorderLayout());
        
        // Form Alanı
        JPanel pnlForm = new JPanel(new GridLayout(7, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlForm.add(new JLabel("Seçili ID:"));
        lblID = new JLabel("-");
        lblID.setForeground(Color.RED);
        pnlForm.add(lblID);

        txtAd = new JTextField();
        txtYazar = new JTextField();
        txtYayinevi = new JTextField();
        txtYil = new JTextField();
        txtAdet = new JTextField();
        cmbKategori = new JComboBox<>();

        pnlForm.add(new JLabel("Kitap Adı:")); pnlForm.add(txtAd);
        pnlForm.add(new JLabel("Yazar:")); pnlForm.add(txtYazar);
        pnlForm.add(new JLabel("Kategori:")); pnlForm.add(cmbKategori);
        pnlForm.add(new JLabel("Yayınevi:")); pnlForm.add(txtYayinevi);
        pnlForm.add(new JLabel("Basım Yılı:")); pnlForm.add(txtYil);
        pnlForm.add(new JLabel("Toplam Adet:")); pnlForm.add(txtAdet);

        // Butonlar
        JPanel pnlButonlar = new JPanel();
        JButton btnEkle = new JButton("Ekle");
        JButton btnGuncelle = new JButton("Güncelle");
        JButton btnSil = new JButton("Sil");
        JButton btnTemizle = new JButton("Temizle");

        btnEkle.setBackground(new Color(46, 204, 113)); // Yeşil
        btnGuncelle.setBackground(new Color(52, 152, 219)); // Mavi
        btnSil.setBackground(new Color(231, 76, 60)); // Kırmızı
        btnSil.setForeground(Color.WHITE);

        pnlButonlar.add(btnEkle);
        pnlButonlar.add(btnGuncelle);
        pnlButonlar.add(btnSil);
        pnlButonlar.add(btnTemizle);

        pnlAlt.add(pnlForm, BorderLayout.CENTER);
        pnlAlt.add(pnlButonlar, BorderLayout.SOUTH);

        add(pnlAlt, BorderLayout.SOUTH);

        // BAŞLANGIÇ İŞLEMLERİ 
        kategorileriYukle();
        listele(""); // Boş gönderince hepsini listeler

        // OLAYLAR 

        // 1. TABLOYA TIKLAMA (Verileri Kutuya Çek)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    lblID.setText(model.getValueAt(row, 0).toString());
                    txtAd.setText(model.getValueAt(row, 1).toString());
                    txtYazar.setText(model.getValueAt(row, 2).toString());
                    
                    // Kategori ComboBox'ını ayarla
                    String tabloKategoriAdi = model.getValueAt(row, 3).toString();
                    for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                        if (cmbKategori.getItemAt(i).contains(tabloKategoriAdi)) {
                            cmbKategori.setSelectedIndex(i);
                            break;
                        }
                    }

                    txtYayinevi.setText(model.getValueAt(row, 4).toString());
                    txtYil.setText(model.getValueAt(row, 5).toString());
                    txtAdet.setText(model.getValueAt(row, 6).toString());
                }
            }
        });

        // 2. ARAMA İŞLEMİ
        btnAra.addActionListener(e -> listele(txtArama.getText()));
        
        btnHepsiniGoster.addActionListener(e -> {
            txtArama.setText("");
            listele("");
        });

        // 3. EKLEME İŞLEMİ
        btnEkle.addActionListener(e -> {
            try (Connection conn = Database.baglantiGetir()) {
                String sql = "INSERT INTO KITAP (KitapAdi, Yazar, KategoriID, Yayinevi, BasimYili, ToplamAdet, MevcutAdet) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtAd.getText());
                ps.setString(2, txtYazar.getText());
                
                String secilen = (String) cmbKategori.getSelectedItem();
                int katID = Integer.parseInt(secilen.split("-")[0]); 
                ps.setInt(3, katID);
                
                ps.setString(4, txtYayinevi.getText());
                ps.setInt(5, Integer.parseInt(txtYil.getText()));
                int adet = Integer.parseInt(txtAdet.getText());
                ps.setInt(6, adet);
                ps.setInt(7, adet); 
                
                ps.executeUpdate();
                listele("");
                temizle();
                JOptionPane.showMessageDialog(this, "Kitap Eklendi!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // 4. GÜNCELLEME İŞLEMİ 
        btnGuncelle.addActionListener(e -> {
            if (lblID.getText().equals("-")) {
                JOptionPane.showMessageDialog(this, "Güncellenecek kitabı seçin!");
                return;
            }
            try (Connection conn = Database.baglantiGetir()) {
                // Not: MevcutAdet (Stok) alanını elle güncellemeye izin vermiyoruz, karışıklık olmasın diye.
                // Sadece ToplamAdet güncellenebilir.
                String sql = "UPDATE KITAP SET KitapAdi=?, Yazar=?, KategoriID=?, Yayinevi=?, BasimYili=?, ToplamAdet=? WHERE KitapID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                
                ps.setString(1, txtAd.getText());
                ps.setString(2, txtYazar.getText());
                
                String secilen = (String) cmbKategori.getSelectedItem();
                int katID = Integer.parseInt(secilen.split("-")[0]); 
                ps.setInt(3, katID);
                
                ps.setString(4, txtYayinevi.getText());
                ps.setInt(5, Integer.parseInt(txtYil.getText()));
                ps.setInt(6, Integer.parseInt(txtAdet.getText()));
                ps.setInt(7, Integer.parseInt(lblID.getText()));
                
                ps.executeUpdate();
                listele("");
                temizle();
                JOptionPane.showMessageDialog(this, "Kitap Bilgileri Güncellendi!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // 5. SİLME İŞLEMİ
        btnSil.addActionListener(e -> {
            if (lblID.getText().equals("-")) {
                JOptionPane.showMessageDialog(this, "Silinecek kitabı seçin!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bu kitabı silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            try (Connection conn = Database.baglantiGetir()) {
                conn.createStatement().executeUpdate("DELETE FROM KITAP WHERE KitapID=" + lblID.getText());
                listele("");
                temizle();
                JOptionPane.showMessageDialog(this, "Kitap Silindi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: Bu kitap ödünç verilmiş olabilir, silinemez!");
            }
        });

        // 6. TEMİZLE
        btnTemizle.addActionListener(e -> temizle());
    }

    private void kategorileriYukle() {
        try (Connection conn = Database.baglantiGetir()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM KATEGORI");
            while (rs.next()) {
                cmbKategori.addItem(rs.getInt("KategoriID") + "-" + rs.getString("KategoriAdi"));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Arama parametresi alan listeleme metodu
    private void listele(String aramaKelimesi) {
        model.setRowCount(0);
        try (Connection conn = Database.baglantiGetir()) {
            String sql = "SELECT k.KitapID, k.KitapAdi, k.Yazar, c.KategoriAdi, k.Yayinevi, k.BasimYili, k.ToplamAdet, k.MevcutAdet " +
                         "FROM KITAP k JOIN KATEGORI c ON k.KategoriID = c.KategoriID WHERE 1=1";
            
            // Eğer arama kutusu doluysa filtre ekle
            if (aramaKelimesi != null && !aramaKelimesi.isEmpty()) {
                sql += " AND (k.KitapAdi LIKE '%" + aramaKelimesi + "%' OR k.Yazar LIKE '%" + aramaKelimesi + "%')";
            }
            
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("KitapID"),
                    rs.getString("KitapAdi"),
                    rs.getString("Yazar"),
                    rs.getString("KategoriAdi"),
                    rs.getString("Yayinevi"),
                    rs.getInt("BasimYili"),
                    rs.getInt("ToplamAdet"),
                    rs.getInt("MevcutAdet")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    private void temizle() {
        lblID.setText("-");
        txtAd.setText(""); txtYazar.setText(""); txtYayinevi.setText(""); 
        txtYil.setText(""); txtAdet.setText("");
        table.clearSelection();
    }
}