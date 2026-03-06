# Kütüphane Yönetim Sistemi

Java ve MySQL kullanılarak geliştirilen bu proje, veritabanı düzeyinde iş kuralları ve veri bütünlüğüne odaklanan kapsamlı bir yönetim sistemidir.

## Proje Hakkında
Sistem; üye bilgilerinin, kitap stoklarının ve ödünç verme süreçlerinin yönetimini sağlar. Ödünç verme sürecinde stok kontrolü, gecikme takibi ve ceza hesaplamalarını içeren tam bir iş akışına sahiptir.

## Temel Özellikler
* **Veritabanı Mimarisi:** Tasarım, 3NF (Third Normal Form) düzeyinde normalize edilmiştir.
* **Otomatik İş Akışları:** Saklı yordamlar (Stored Procedures) ve tetikleyiciler (Triggers) ile yönetilen süreçler mevcuttur.
* **Rol Tabanlı Erişim:** Admin ve Görevli rolleri ile sistem güvenliği sağlanmaktadır.
* **Dinamik Raporlama:** Tarih aralığı, kategori ve stok durumuna göre detaylı sorgulama ekranları bulunur.

## Teknik Detaylar

### Veritabanı Tasarımı
Sistem; KULLANICI, UYE, KITAP, KATEGORI, ODUNC, CEZA ve LOG_ISLEM tabloları üzerine inşa edilmiştir.

### İş Mantığı (Backend Logic)
Hata riskini minimuma indirmek amacıyla iş mantığı doğrudan SQL düzeyinde işlenmiştir:

* **Saklı Yordamlar:** Ödünç verme (sp_YeniOduncVer), teslim alma ve ceza yönetimi (sp_KitapTeslimAl) ve üye özeti raporlama (sp_UyeOzetRapor).
* **Tetikleyiciler:** Stok miktarının otomatik güncellenmesi, ceza durumunda üye borcunun güncellenmesi ve kritik işlemler için log kayıtlarının oluşturulması.

## Proje Sunumu
Sistemin çalışma mantığını ve ekran arayüzlerini incelemek için tanıtım videosuna göz atabilirsiniz:

**Video Linki:** https://youtu.be/wJQrmm9_RWM
**Geliştirenler: Sefa Bodur & Batuhan Gözüyukarı
