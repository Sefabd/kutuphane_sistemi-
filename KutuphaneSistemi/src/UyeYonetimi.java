import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class UyeYonetimi extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtAd, txtSoyad, txtTel, txtMail, txtArama;
    private JLabel lblID; 

    public UyeYonetimi() {
        setTitle("Üye Yönetimi");
        setSize(900, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        //  1. ÜST PANEL: ARAMA 
        JPanel pnlUst = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlUst.setBorder(BorderFactory.createTitledBorder("Üye Ara"));
        txtArama = new JTextField(20);
        JButton btnAra = new JButton("Ara");
        JButton btnHepsiniGoster = new JButton("Tümünü Göster");
        pnlUst.add(new JLabel("Ad, Soyad veya Email:"));
        pnlUst.add(txtArama);
        pnlUst.add(btnAra);
        pnlUst.add(btnHepsiniGoster);
        add(pnlUst, BorderLayout.NORTH);

        // 2. ORTA KISIM: TABLO 
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Tablo elle düzenlenemesin
        };
        model.setColumnIdentifiers(new Object[]{"ID", "Ad", "Soyad", "Telefon", "Email", "Borç"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        //  3. ALT PANEL: FORM VE BUTONLAR 
        JPanel pnlAlt = new JPanel(new BorderLayout());
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlForm.add(new JLabel("Seçili Üye ID:"));
        lblID = new JLabel("-");
        lblID.setFont(new Font("Arial", Font.BOLD, 14));
        lblID.setForeground(Color.RED);
        pnlForm.add(lblID);

        txtAd = new JTextField();
        txtSoyad = new JTextField();
        txtTel = new JTextField();
        txtMail = new JTextField();

        pnlForm.add(new JLabel("Ad:")); pnlForm.add(txtAd);
        pnlForm.add(new JLabel("Soyad:")); pnlForm.add(txtSoyad);
        pnlForm.add(new JLabel("Telefon:")); pnlForm.add(txtTel);
        pnlForm.add(new JLabel("Email:")); pnlForm.add(txtMail);

        JPanel pnlButonlar = new JPanel();
        JButton btnEkle = new JButton("Ekle");
        JButton btnGuncelle = new JButton("Güncelle");
        JButton btnSil = new JButton("Sil");
        JButton btnTemizle = new JButton("Temizle");

        btnEkle.setBackground(new Color(46, 204, 113));
        btnGuncelle.setBackground(new Color(52, 152, 219));
        btnSil.setBackground(new Color(231, 76, 60));
        btnSil.setForeground(Color.WHITE);

        pnlButonlar.add(btnEkle);
        pnlButonlar.add(btnGuncelle);
        pnlButonlar.add(btnSil);
        pnlButonlar.add(btnTemizle);

        pnlAlt.add(pnlForm, BorderLayout.CENTER);
        pnlAlt.add(pnlButonlar, BorderLayout.SOUTH);
        add(pnlAlt, BorderLayout.SOUTH);

        //  İŞLEMLER 
        listele("");

        // Tabloya Tıklama
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    lblID.setText(model.getValueAt(row, 0).toString());
                    txtAd.setText(model.getValueAt(row, 1).toString());
                    txtSoyad.setText(model.getValueAt(row, 2).toString());
                    txtTel.setText(model.getValueAt(row, 3).toString());
                    txtMail.setText(model.getValueAt(row, 4).toString());
                }
            }
        });

        // Arama Butonları
        btnAra.addActionListener(e -> listele(txtArama.getText()));
        btnHepsiniGoster.addActionListener(e -> { txtArama.setText(""); listele(""); });

        // Ekleme
        btnEkle.addActionListener(e -> {
            if(txtAd.getText().isEmpty() || txtSoyad.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ad ve Soyad boş olamaz!");
                return;
            }
            try (Connection conn = Database.baglantiGetir()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO UYE (Ad, Soyad, Telefon, Email) VALUES (?,?,?,?)");
                ps.setString(1, txtAd.getText());
                ps.setString(2, txtSoyad.getText());
                ps.setString(3, txtTel.getText());
                ps.setString(4, txtMail.getText());
                ps.executeUpdate();
                listele(""); temizle();
                JOptionPane.showMessageDialog(this, "Üye Eklendi!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Ekleme Hatası: " + ex.getMessage()); }
        });

        // Güncelleme
        btnGuncelle.addActionListener(e -> {
            if (lblID.getText().equals("-")) {
                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir üye seçin!");
                return;
            }
            try (Connection conn = Database.baglantiGetir()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE UYE SET Ad=?, Soyad=?, Telefon=?, Email=? WHERE UyeID=?");
                ps.setString(1, txtAd.getText());
                ps.setString(2, txtSoyad.getText());
                ps.setString(3, txtTel.getText());
                ps.setString(4, txtMail.getText());
                ps.setInt(5, Integer.parseInt(lblID.getText()));
                ps.executeUpdate();
                listele(""); temizle();
                JOptionPane.showMessageDialog(this, "Üye Güncellendi!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Güncelleme Hatası: " + ex.getMessage()); }
        });

        // Silme
        btnSil.addActionListener(e -> {
            if (lblID.getText().equals("-")) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek üyeyi seçin!");
                return;
            }
            int onay = JOptionPane.showConfirmDialog(this, "Üyeyi silmek istediğinize emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
            if (onay == JOptionPane.YES_OPTION) {
                try (Connection conn = Database.baglantiGetir()) {
                    Statement st = conn.createStatement();
                    st.executeUpdate("DELETE FROM UYE WHERE UyeID=" + lblID.getText());
                    listele(""); temizle();
                    JOptionPane.showMessageDialog(this, "Üye Silindi.");
                } catch (Exception ex) { 
                    JOptionPane.showMessageDialog(this, "Hata: Borcu veya kitabı olan üye silinemez!\nDetay: " + ex.getMessage()); 
                }
            }
        });

        btnTemizle.addActionListener(e -> temizle());
    }

    private void listele(String arama) {
        model.setRowCount(0);
        try (Connection conn = Database.baglantiGetir()) {
            String sql = "SELECT * FROM UYE WHERE Ad LIKE ? OR Soyad LIKE ? OR Email LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + arama + "%");
            ps.setString(2, "%" + arama + "%");
            ps.setString(3, "%" + arama + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("UyeID"), rs.getString("Ad"), rs.getString("Soyad"), rs.getString("Telefon"), rs.getString("Email"), rs.getDouble("ToplamBorc")});
            }
        } catch (Exception ex) { System.out.println("Listeleme Hatası: " + ex.getMessage()); }
    }

    private void temizle() {
        lblID.setText("-"); txtAd.setText(""); txtSoyad.setText(""); txtTel.setText(""); txtMail.setText(""); table.clearSelection();
    }
}