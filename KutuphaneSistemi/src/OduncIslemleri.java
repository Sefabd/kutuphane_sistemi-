import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OduncIslemleri extends JFrame {
    private JTable tableUye, tableKitap;
    private DefaultTableModel modelUye, modelKitap;
    private int aktifPersonelID; // İşlemi yapan görevlinin ID'si

    public OduncIslemleri(int personelID) {
        this.aktifPersonelID = personelID;
        setTitle("Ödünç Verme İşlemleri");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //  ORTA ALAN (İKİ TABLO YAN YANA) 
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // 1. Üye Tablosu
        JPanel pnlSol = new JPanel(new BorderLayout());
        pnlSol.setBorder(BorderFactory.createTitledBorder("1. Adım: Üye Seçiniz"));
        modelUye = new DefaultTableModel();
        modelUye.setColumnIdentifiers(new Object[]{"ID", "Ad", "Soyad", "Borç"});
        tableUye = new JTable(modelUye);
        pnlSol.add(new JScrollPane(tableUye), BorderLayout.CENTER);

        // 2. Kitap Tablosu
        JPanel pnlSag = new JPanel(new BorderLayout());
        pnlSag.setBorder(BorderFactory.createTitledBorder("2. Adım: Kitap Seçiniz (Sadece Stoktakiler)"));
        modelKitap = new DefaultTableModel();
        modelKitap.setColumnIdentifiers(new Object[]{"ID", "Kitap Adı", "Yazar", "Stok"});
        tableKitap = new JTable(modelKitap);
        pnlSag.add(new JScrollPane(tableKitap), BorderLayout.CENTER);

        splitPanel.add(pnlSol);
        splitPanel.add(pnlSag);
        add(splitPanel, BorderLayout.CENTER);

        //  ALT ALAN (BUTON) 
        JButton btnVer = new JButton("SEÇİLİ KİTABI ÖDÜNÇ VER");
        btnVer.setFont(new Font("Arial", Font.BOLD, 14));
        btnVer.setBackground(new Color(0, 150, 0)); // Koyu Yeşil
        btnVer.setForeground(Color.WHITE);
        btnVer.setPreferredSize(new Dimension(200, 50));
        
        add(btnVer, BorderLayout.SOUTH);

        // Verileri Yükle
        verileriGetir();

        // BUTON İŞLEVİ 
        btnVer.addActionListener(e -> {
            int rowUye = tableUye.getSelectedRow();
            int rowKitap = tableKitap.getSelectedRow();

            if (rowUye == -1 || rowKitap == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen hem bir ÜYE hem de bir KİTAP seçiniz!");
                return;
            }

            int uyeID = Integer.parseInt(tableUye.getValueAt(rowUye, 0).toString());
            int kitapID = Integer.parseInt(tableKitap.getValueAt(rowKitap, 0).toString());

            try (Connection conn = Database.baglantiGetir()) {
                // SAKLI YORDAM ÇAĞRISI (Stored Procedure)
                CallableStatement cstmt = conn.prepareCall("{call sp_YeniOduncVer(?, ?, ?)}");
                cstmt.setInt(1, uyeID);
                cstmt.setInt(2, kitapID);
                cstmt.setInt(3, aktifPersonelID);
                
                cstmt.execute(); // İşlemi yap
                
                JOptionPane.showMessageDialog(this, "İşlem Başarılı! Kitap ödünç verildi.");
                verileriGetir(); // Listeleri yenile (Stok düşür)
                
            } catch (SQLException ex) {
                // SQL'den gelen hata mesajını (Limit doldu, stok yok vb.) göster
                JOptionPane.showMessageDialog(this, "HATA: " + ex.getMessage(), "İşlem Başarısız", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void verileriGetir() {
        modelUye.setRowCount(0);
        modelKitap.setRowCount(0);
        try (Connection conn = Database.baglantiGetir()) {
            // Üyeleri çek
            ResultSet rsUye = conn.createStatement().executeQuery("SELECT * FROM UYE");
            while (rsUye.next()) {
                modelUye.addRow(new Object[]{rsUye.getInt("UyeID"), rsUye.getString("Ad"), rsUye.getString("Soyad"), rsUye.getDouble("ToplamBorc")});
            }
            // Stokta olan kitapları çek
            ResultSet rsKitap = conn.createStatement().executeQuery("SELECT * FROM KITAP WHERE MevcutAdet > 0");
            while (rsKitap.next()) {
                modelKitap.addRow(new Object[]{rsKitap.getInt("KitapID"), rsKitap.getString("KitapAdi"), rsKitap.getString("Yazar"), rsKitap.getInt("MevcutAdet")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}