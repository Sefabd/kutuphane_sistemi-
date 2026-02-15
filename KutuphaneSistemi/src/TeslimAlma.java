import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;

public class TeslimAlma extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtAra; // Arama kutusu

    public TeslimAlma() {
        setTitle("Kitap Teslim Alma (İade İşlemleri)");
        setSize(1000, 600); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //  ÜST PANEL: FİLTRELEME 
        JPanel pnlUst = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlUst.setBorder(BorderFactory.createTitledBorder("İade İşlemi Ara"));
        
        txtAra = new JTextField(20);
        JButton btnAra = new JButton("Ara");
        JButton btnTumunuGoster = new JButton("Tümünü Göster");
        
        pnlUst.add(new JLabel("Üye, Kitap veya Tarih:"));
        pnlUst.add(txtAra);
        pnlUst.add(btnAra);
        pnlUst.add(btnTumunuGoster);
        
        add(pnlUst, BorderLayout.NORTH);

        //  ORTA PANEL: TABLO 
        model = new DefaultTableModel();
        //  Personel ID SÜTUNU
        model.setColumnIdentifiers(new Object[]{"OduncID", "Üye Adı", "Kitap Adı", "Veriliş Tarihi", "Son Teslim Tarihi", "Veren Personel ID"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        //  ALT PANEL: BUTON 
        JButton btnTeslim = new JButton("SEÇİLİ KİTABI İADE AL");
        btnTeslim.setBackground(Color.ORANGE);
        btnTeslim.setFont(new Font("Arial", Font.BOLD, 14));
        btnTeslim.setPreferredSize(new Dimension(200, 50));
        add(btnTeslim, BorderLayout.SOUTH);

        listele(""); // İlk açılışta hepsini getir

        //  AKSİYONLAR 
        
        // Arama Butonu
        btnAra.addActionListener(e -> listele(txtAra.getText()));
        
        // Tümünü Göster
        btnTumunuGoster.addActionListener(e -> {
            txtAra.setText("");
            listele("");
        });

        // Teslim Al Butonu
        btnTeslim.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen listeden teslim alınacak işlemi seçiniz.");
                return;
            }

            int oduncID = Integer.parseInt(table.getValueAt(row, 0).toString());

            try (Connection conn = Database.baglantiGetir()) {
                CallableStatement cstmt = conn.prepareCall("{call sp_KitapTeslimAl(?, ?)}");
                cstmt.setInt(1, oduncID);
                cstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); 
                cstmt.execute();
                
                JOptionPane.showMessageDialog(this, "Kitap başarıyla teslim alındı. (Gecikme varsa ceza eklendi)");
                listele(""); 
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });
    }

    private void listele(String aramaKelimesi) {
        model.setRowCount(0);
        try (Connection conn = Database.baglantiGetir()) {
            //  'o.KullaniciID' EKLENDİ
            String sql = "SELECT o.OduncID, CONCAT(u.Ad, ' ', u.Soyad) as UyeAd, k.KitapAdi, o.OduncTarihi, o.SonTeslimTarihi, o.KullaniciID " +
                         "FROM ODUNC o " +
                         "JOIN UYE u ON o.UyeID = u.UyeID " +
                         "JOIN KITAP k ON o.KitapID = k.KitapID " +
                         "WHERE o.TeslimTarihi IS NULL";

            // Filtre varsa SQL'e ekle
            if (!aramaKelimesi.isEmpty()) {
                sql += " AND (u.Ad LIKE '%" + aramaKelimesi + "%' OR u.Soyad LIKE '%" + aramaKelimesi + "%' " +
                       "OR k.KitapAdi LIKE '%" + aramaKelimesi + "%' OR o.OduncTarihi LIKE '%" + aramaKelimesi + "%')";
            }
            
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("OduncID"),
                    rs.getString("UyeAd"),
                    rs.getString("KitapAdi"),
                    rs.getTimestamp("OduncTarihi"),
                    rs.getTimestamp("SonTeslimTarihi"),
                    rs.getInt("KullaniciID") 
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}