import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class CezaEkrani extends JFrame {
    
    public CezaEkrani() {
        setTitle("Ceza Takip Ekranı");
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        // Tablo Modeli
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Ceza ID", "Üye Adı Soyadı", "Tutar", "Açıklama", "Tarih"});
        JTable table = new JTable(model);
        
       
        add(new JScrollPane(table));

        // Verileri Çek
        try (Connection conn = Database.baglantiGetir()) {
            String sql = "SELECT c.CezaID, CONCAT(u.Ad, ' ', u.Soyad) as UyeAd, c.Tutar, c.Aciklama, c.Tarih " +
                         "FROM CEZA c JOIN UYE u ON c.UyeID = u.UyeID ORDER BY c.Tarih DESC";
            
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("CezaID"),
                    rs.getString("UyeAd"),
                    rs.getDouble("Tutar") + " TL",
                    rs.getString("Aciklama"),
                    rs.getTimestamp("Tarih")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}