import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    // Giriş yapan personelin ID'sini burada saklayacağız
    private int aktifKullaniciID; 

    public MainMenu(int kullaniciID, String kullaniciAdi) {
        this.aktifKullaniciID = kullaniciID;

        //  PENCERE AYARLARI 
        setTitle("Kütüphane Yönetim Sistemi - Ana Menü");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. ÜST KISIM 
        JLabel lblWelcome = new JLabel("Hoş geldiniz, " + kullaniciAdi + " (ID: " + kullaniciID + ")", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(lblWelcome, BorderLayout.NORTH);

        //  2. ORTA KISIM (BUTONLAR) 
        JPanel panelBtn = new JPanel();
        panelBtn.setLayout(new GridLayout(8, 1, 10, 10));
        panelBtn.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        JButton btnUye = new JButton("1. Üye Yönetimi");
        JButton btnKitap = new JButton("2. Kitap Yönetimi");
        JButton btnOdunc = new JButton("3. Ödünç Verme İşlemleri");
        JButton btnTeslim = new JButton("4. Kitap Teslim Alma (İade)");
        JButton btnCeza = new JButton("5. Ceza Görüntüleme");
        JButton btnRapor = new JButton("6. Raporlar ve Dinamik Sorgu");
        JButton btnKategori = new JButton("7.Kategori Yönetimi");
        JButton btnCikis = new JButton("Sistemden Çıkış");
        
        btnKategori.setBackground(Color.ORANGE);
        btnCikis.setBackground(Color.RED);
        btnCikis.setForeground(Color.WHITE);

        panelBtn.add(btnUye);
        panelBtn.add(btnKitap);
        panelBtn.add(btnOdunc);
        panelBtn.add(btnTeslim);
        panelBtn.add(btnCeza);
        panelBtn.add(btnRapor);
        panelBtn.add(btnKategori);
        panelBtn.add(btnCikis);

        add(panelBtn, BorderLayout.CENTER);

        // 3. BUTONLARI SAYFALARA BAĞLAMA 
        btnUye.addActionListener(e -> new UyeYonetimi().setVisible(true));
        btnKitap.addActionListener(e -> new KitapYonetimi().setVisible(true));
        
        
        btnOdunc.addActionListener(e -> new OduncIslemleri(aktifKullaniciID).setVisible(true));
        
        btnTeslim.addActionListener(e -> new TeslimAlma().setVisible(true));
        btnCeza.addActionListener(e -> new CezaEkrani().setVisible(true));
        btnRapor.addActionListener(e -> new RaporEkrani().setVisible(true));
        btnKategori.addActionListener(e -> new KategoriYonetimi().setVisible(true));

        btnCikis.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });
    }
}