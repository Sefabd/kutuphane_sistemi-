import javax.swing.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtKullaniciAdi;
    private JPasswordField txtSifre;

    public Login() {
        setTitle("Kütüphane Sistemi - Giriş");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(null);

        JLabel lblUser = new JLabel("Kullanıcı Adı:");
        lblUser.setBounds(50, 50, 100, 30);
        add(lblUser);

        txtKullaniciAdi = new JTextField();
        txtKullaniciAdi.setBounds(150, 50, 150, 30);
        add(txtKullaniciAdi);

        JLabel lblPass = new JLabel("Şifre:");
        lblPass.setBounds(50, 100, 100, 30);
        add(lblPass);

        txtSifre = new JPasswordField();
        txtSifre.setBounds(150, 100, 150, 30);
        add(txtSifre);

        JButton btnGiris = new JButton("Giriş Yap");
        btnGiris.setBounds(150, 150, 100, 30);
        add(btnGiris);

        btnGiris.addActionListener(e -> girisKontrol());
    }

    private void girisKontrol() {
        String kullaniciAdi = txtKullaniciAdi.getText();
        String sifre = new String(txtSifre.getPassword());

        try (Connection conn = Database.baglantiGetir()) {
            String sql = "SELECT * FROM KULLANICI WHERE KullaniciAdi=? AND Sifre=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kullaniciAdi);
            ps.setString(2, sifre);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Giriş Başarılı: ID ve İsmi al
                int id = rs.getInt("KullaniciID");
                String ad = rs.getString("KullaniciAdi");
                
                // Ana Menüye her ikisini de gönder
                new MainMenu(id, ad).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı Kullanıcı Adı veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bağlantı Hatası: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}