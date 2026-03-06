Kütüphane Yönetim Sistemi
Java ve MySQL kullanılarak geliştirilen bu proje, veritabanı düzeyinde iş kuralları ve veri bütünlüğüne odaklanan kapsamlı bir yönetim sistemidir.

Proje Hakkında
Sistem; üye bilgilerinin, kitap stoklarının ve ödünç verme süreçlerinin uçtan uca yönetilmesini sağlar. Süreç içerisinde stok kontrolü, gecikme takibi ve ceza hesaplamaları otomatik olarak gerçekleştirilir.

Temel Özellikler
Veritabanı Mimarisi: 3NF düzeyinde normalize edilmiş yapı.

Otomatik İş Akışları: Saklı yordamlar (Stored Procedures) ve tetikleyiciler (Triggers) ile yönetilen mantıksal süreçler.

Rol Tabanlı Erişim: Admin ve Görevli rolleri ile sağlanan sistem güvenliği.

Dinamik Raporlama: Tarih, kategori ve stok durumuna göre detaylı sorgulama imkanı.

Teknik Detaylar
Veritabanı Tasarımı
Sistem; KULLANICI, UYE, KITAP, KATEGORI, ODUNC, CEZA ve LOG_ISLEM tabloları üzerine inşa edilmiştir.

İş Mantığı (Backend Logic)
Hata riskini minimuma indirmek için iş mantığı doğrudan SQL düzeyinde işlenmiştir:

Saklı Yordamlar: Ödünç verme (sp_YeniOduncVer), teslim alma ve ceza yönetimi (sp_KitapTeslimAl), üye özeti raporlama (sp_UyeOzetRapor).

Tetikleyiciler: Otomatik stok güncelleme, ceza/borç takibi ve kritik işlem loglarının oluşturulması.

Proje Sunumu
Sistemin çalışma mantığını ve arayüzlerini incelemek için tanıtım videosunu izleyebilirsiniz:

Youtube: https://youtu.be/wJQrmm9_RWM
