import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class KategoriYonetimi extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtKategoriAd;
    private JLabel lblID; 

    public KategoriYonetimi() {
        setTitle("Kategori Yönetimi");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ÜST PANEL: FORM 
        JPanel panelForm = new JPanel(new FlowLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Kategori İşlemleri"));
        
        lblID = new JLabel("-"); 
        lblID.setVisible(false); 
        
        txtKategoriAd = new JTextField(20);
        JButton btnEkle = new JButton("Ekle");
        JButton btnGuncelle = new JButton("Güncelle");
        JButton btnSil = new JButton("Sil");

        // renkli butonlar
        btnEkle.setBackground(new Color(255, 165, 0)); 
        btnGuncelle.setBackground(new Color(52, 152, 219));
        btnSil.setBackground(Color.RED);
        btnSil.setForeground(Color.WHITE);

        panelForm.add(lblID);
        panelForm.add(new JLabel("Kategori Adı:"));
        panelForm.add(txtKategoriAd);
        panelForm.add(btnEkle);
        panelForm.add(btnGuncelle);
        panelForm.add(btnSil);

        add(panelForm, BorderLayout.NORTH);

        // ORTA PANEL: TABLO 
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"ID", "Kategori Adı"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        listele();

        // OLAYLAR 

        // 1. TABLOYA TIKLAMA (Veriyi kutuya çek)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    lblID.setText(model.getValueAt(row, 0).toString());
                    txtKategoriAd.setText(model.getValueAt(row, 1).toString());
                }
            }
        });

        // 2. EKLEME
        btnEkle.addActionListener(e -> {
            if (txtKategoriAd.getText().trim().isEmpty()) return;
            try (Connection conn = Database.baglantiGetir()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO KATEGORI (KategoriAdi) VALUES (?)");
                ps.setString(1, txtKategoriAd.getText());
                ps.executeUpdate();
                listele();
                temizle();
                JOptionPane.showMessageDialog(this, "Kategori Eklendi!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // 3. GÜNCELLEME
        btnGuncelle.addActionListener(e -> {
            if (lblID.getText().equals("-")) return;
            try (Connection conn = Database.baglantiGetir()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE KATEGORI SET KategoriAdi=? WHERE KategoriID=?");
                ps.setString(1, txtKategoriAd.getText());
                ps.setInt(2, Integer.parseInt(lblID.getText()));
                ps.executeUpdate();
                listele();
                temizle();
                JOptionPane.showMessageDialog(this, "Kategori Güncellendi.");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // 4. SİLME
        btnSil.addActionListener(e -> {
            if (lblID.getText().equals("-")) {
                JOptionPane.showMessageDialog(this, "Silinecek kategoriyi seçin.");
                return;
            }
            try (Connection conn = Database.baglantiGetir()) {
                // Önce bu kategoriye ait kitap var mı kontrol et (Hata almamak için)
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM KITAP WHERE KategoriID=" + lblID.getText());
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Hata: Bu kategoriye ait kitaplar var, silinemez!");
                    return;
                }
                
                st.executeUpdate("DELETE FROM KATEGORI WHERE KategoriID=" + lblID.getText());
                listele();
                temizle();
                JOptionPane.showMessageDialog(this, "Kategori Silindi.");
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    private void listele() {
        model.setRowCount(0);
        try (Connection conn = Database.baglantiGetir()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM KATEGORI");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("KategoriID"), rs.getString("KategoriAdi")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void temizle() {
        txtKategoriAd.setText("");
        lblID.setText("-");
    }
}