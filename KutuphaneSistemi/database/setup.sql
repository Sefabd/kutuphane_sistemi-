-- 1. VERITABANI SIFIRLAMA
DROP DATABASE IF EXISTS kutuphane_db;
CREATE DATABASE kutuphane_db CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci;
USE kutuphane_db;

-- 2. TABLOLAR
CREATE TABLE KULLANICI (
    KullaniciID INT PRIMARY KEY AUTO_INCREMENT,
    KullaniciAdi VARCHAR(50) NOT NULL UNIQUE,
    Sifre VARCHAR(50) NOT NULL,
    Rol VARCHAR(20) DEFAULT 'Gorevli'
);

CREATE TABLE KATEGORI (
    KategoriID INT PRIMARY KEY AUTO_INCREMENT,
    KategoriAdi VARCHAR(50) NOT NULL
);

CREATE TABLE KITAP (
    KitapID INT PRIMARY KEY AUTO_INCREMENT,
    KitapAdi VARCHAR(100) NOT NULL,
    Yazar VARCHAR(100),
    KategoriID INT,
    Yayinevi VARCHAR(100),
    BasimYili INT,
    ToplamAdet INT DEFAULT 1,
    MevcutAdet INT DEFAULT 1,
    FOREIGN KEY (KategoriID) REFERENCES KATEGORI(KategoriID)
);

CREATE TABLE UYE (
    UyeID INT PRIMARY KEY AUTO_INCREMENT,
    Ad VARCHAR(50) NOT NULL,
    Soyad VARCHAR(50) NOT NULL,
    Email VARCHAR(100),
    Telefon VARCHAR(15),
    ToplamBorc DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE ODUNC (
    OduncID INT PRIMARY KEY AUTO_INCREMENT,
    UyeID INT,
    KitapID INT,
    KullaniciID INT,
    OduncTarihi DATETIME DEFAULT CURRENT_TIMESTAMP,
    SonTeslimTarihi DATETIME,
    TeslimTarihi DATETIME NULL,
    FOREIGN KEY (UyeID) REFERENCES UYE(UyeID),
    FOREIGN KEY (KitapID) REFERENCES KITAP(KitapID),
    FOREIGN KEY (KullaniciID) REFERENCES KULLANICI(KullaniciID)
);

CREATE TABLE CEZA (
    CezaID INT PRIMARY KEY AUTO_INCREMENT,
    UyeID INT,
    OduncID INT,
    Tutar DECIMAL(10,2),
    Aciklama VARCHAR(255),
    Tarih DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UyeID) REFERENCES UYE(UyeID),
    FOREIGN KEY (OduncID) REFERENCES ODUNC(OduncID)
);

CREATE TABLE LOG_ISLEM (
    LogID INT PRIMARY KEY AUTO_INCREMENT,
    IslemTuru VARCHAR(50),
    Aciklama TEXT,
    Tarih DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. VERILER
INSERT INTO KULLANICI (KullaniciAdi, Sifre, Rol) VALUES 
('admin', '1234', 'Admin'),
('personel', '1234', 'Gorevli');

INSERT INTO KATEGORI (KategoriAdi) VALUES 
('Dünya Klasikleri'), ('Türk Edebiyatı'), ('Bilim'), ('Tarih'), ('Felsefe'), ('Psikoloji'), ('Distopya');

INSERT INTO UYE (Ad, Soyad, Email, Telefon) VALUES 
('Ahmet', 'Yılmaz', 'ahmet@gmail.com', '5551112233'),
('Ayşe', 'Demir', 'ayse@gmail.com', '5552223344'),
('Mehmet', 'Kaya', 'mehmet@gmail.com', '5553334455'),
('Zeynep', 'Çelik', 'zeynep@gmail.com', '5554445566');

INSERT INTO KITAP (KitapAdi, Yazar, KategoriID, Yayinevi, BasimYili, ToplamAdet, MevcutAdet) VALUES 
('Sefiller', 'Victor Hugo', 1, 'İş Bankası Yayınları', 1862, 5, 5),
('Suç ve Ceza', 'Fyodor Dostoyevski', 1, 'Can Yayınları', 1866, 8, 8),
('Anna Karenina', 'Lev Tolstoy', 1, 'İletişim Yayınları', 1877, 4, 4),
('Kürk Mantolu Madonna', 'Sabahattin Ali', 2, 'YKY', 1943, 10, 10),
('Saatleri Ayarlama Enstitüsü', 'Ahmet Hamdi Tanpınar', 2, 'Dergah Yayınları', 1961, 6, 6),
('Tutunamayanlar', 'Oğuz Atay', 2, 'İletişim Yayınları', 1972, 5, 5),
('Kozmos', 'Carl Sagan', 3, 'Altın Kitaplar', 1980, 4, 4),
('Sapiens', 'Yuval Noah Harari', 4, 'Kolektif Kitap', 2011, 12, 12),
('Nutuk', 'Mustafa Kemal Atatürk', 4, 'Kültür Bakanlığı', 1927, 20, 20),
('Devlet', 'Platon', 5, 'İş Bankası Yayınları', 2015, 4, 4),
('1984', 'George Orwell', 7, 'Can Yayınları', 1949, 15, 15);


-- 4. SAKLI YORDAMLAR 

DELIMITER //

CREATE PROCEDURE sp_YeniOduncVer(IN p_UyeID INT, IN p_KitapID INT, IN p_KullaniciID INT)
BEGIN
    DECLARE v_AktifOduncSayisi INT;
    DECLARE v_MevcutStok INT;
    DECLARE v_AyniKitapKontrol INT; 
    
    -- 1. Limit Kontrolu (Maks 3 kitap)
    SELECT COUNT(*) INTO v_AktifOduncSayisi FROM ODUNC WHERE UyeID = p_UyeID AND TeslimTarihi IS NULL;
    
    -- 2. Stok Kontrolu
    SELECT MevcutAdet INTO v_MevcutStok FROM KITAP WHERE KitapID = p_KitapID;
    
    -- 3. AYNI KITAP KONTROLU 
    -- Uye bu kitabi almis mi ve hala teslim etmemis mi?
    SELECT COUNT(*) INTO v_AyniKitapKontrol FROM ODUNC 
    WHERE UyeID = p_UyeID AND KitapID = p_KitapID AND TeslimTarihi IS NULL;
    
    -- Hata Kontrolleri
    IF v_AktifOduncSayisi >= 3 THEN 
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hata: Uye maksimum (3) kitap limitini doldurmus!';
    ELSEIF v_MevcutStok <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hata: Kitap stokta kalmamis!';
    ELSEIF v_AyniKitapKontrol > 0 THEN 
        -- Eger ayni kitaptan elinde varsa hata ver
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hata: Uye bu kitabi zaten almis, teslim etmeden aynisindan alamaz!';
    ELSE
        -- Sorun yoksa ver
        INSERT INTO ODUNC (UyeID, KitapID, KullaniciID, SonTeslimTarihi)
        VALUES (p_UyeID, p_KitapID, p_KullaniciID, DATE_ADD(NOW(), INTERVAL 15 DAY));
    END IF;
END //

CREATE PROCEDURE sp_KitapTeslimAl(IN p_OduncID INT, IN p_TeslimTarihi DATETIME)
BEGIN
    DECLARE v_SonTeslimTarihi DATETIME;
    DECLARE v_UyeID INT;
    DECLARE v_GecikmeGun INT;
    DECLARE v_CezaTutari DECIMAL(10,2);
    
    SELECT SonTeslimTarihi, UyeID INTO v_SonTeslimTarihi, v_UyeID FROM ODUNC WHERE OduncID = p_OduncID;
    
    UPDATE ODUNC SET TeslimTarihi = p_TeslimTarihi WHERE OduncID = p_OduncID;
    
    IF p_TeslimTarihi > v_SonTeslimTarihi THEN
        SET v_GecikmeGun = DATEDIFF(p_TeslimTarihi, v_SonTeslimTarihi);
        SET v_CezaTutari = v_GecikmeGun * 5.00;
        
        INSERT INTO CEZA (UyeID, OduncID, Tutar, Aciklama)
        VALUES (v_UyeID, p_OduncID, v_CezaTutari, CONCAT(v_GecikmeGun, ' gun gecikme'));
    END IF;
END //

CREATE PROCEDURE sp_UyeOzetRapor(IN p_UyeID INT)
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM ODUNC WHERE UyeID = p_UyeID) as ToplamAlinan,
        (SELECT COUNT(*) FROM ODUNC WHERE UyeID = p_UyeID AND TeslimTarihi IS NULL) as IadeEdilmemis,
        (SELECT IFNULL(SUM(Tutar), 0) FROM CEZA WHERE UyeID = p_UyeID) as ToplamCeza
    FROM DUAL;
END //

DELIMITER ;

-- 5. TRIGGERLAR

DELIMITER //

CREATE TRIGGER TR_ODUNC_INSERT AFTER INSERT ON ODUNC FOR EACH ROW
BEGIN
    UPDATE KITAP SET MevcutAdet = MevcutAdet - 1 WHERE KitapID = NEW.KitapID;
    INSERT INTO LOG_ISLEM (IslemTuru, Aciklama) VALUES ('ODUNC_VERME', CONCAT('Kitap:', NEW.KitapID, ' Uye:', NEW.UyeID));
END //

CREATE TRIGGER TR_ODUNC_UPDATE_TESLIM AFTER UPDATE ON ODUNC FOR EACH ROW
BEGIN
    IF OLD.TeslimTarihi IS NULL AND NEW.TeslimTarihi IS NOT NULL THEN
        UPDATE KITAP SET MevcutAdet = MevcutAdet + 1 WHERE KitapID = NEW.KitapID;
        INSERT INTO LOG_ISLEM (IslemTuru, Aciklama) VALUES ('TESLIM_ALMA', CONCAT('OduncID:', NEW.OduncID));
    END IF;
END //

CREATE TRIGGER TR_CEZA_INSERT AFTER INSERT ON CEZA FOR EACH ROW
BEGIN
    UPDATE UYE SET ToplamBorc = ToplamBorc + NEW.Tutar WHERE UyeID = NEW.UyeID;
    INSERT INTO LOG_ISLEM (IslemTuru, Aciklama) VALUES ('CEZA_EKLEME', CONCAT('Uye:', NEW.UyeID, ' Tutar:', NEW.Tutar));
END //

CREATE TRIGGER TR_UYE_DELETE_BLOCK
BEFORE DELETE ON UYE
FOR EACH ROW
BEGIN
    DECLARE v_AktifOdunc INT;
    SELECT COUNT(*) INTO v_AktifOdunc FROM ODUNC WHERE UyeID = OLD.UyeID AND TeslimTarihi IS NULL;
    IF OLD.ToplamBorc > 0 OR v_AktifOdunc > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hata: Bu uyenin borcu veya iade etmedigi kitabi var! Silinemez.';
    END IF;
END //

DELIMITER ;