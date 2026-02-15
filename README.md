Kütüphane Yönetim Sistemi (Library Management System)
Java ve MySQL kullanılarak geliştirilmiş, tam kapsamlı bir kütüphane yönetim sistemidir. Proje, veritabanı düzeyinde iş kuralları ve veri bütünlüğü üzerine odaklanmaktadır. 


 Öne Çıkan Özellikler

Gelişmiş Veritabanı Mimarisi: 3NF düzeyinde normalize edilmiş yapı. 

Otomatik İş Akışları: Saklı yordamlar (Stored Procedures) ve tetikleyiciler (Triggers) ile yönetilen süreçler. 

Rol Tabanlı Erişim: Admin ve Görevli rolleri ile sistem güvenliği. 

Dinamik Raporlama: Tarih aralığına, kategoriye ve stok durumuna göre detaylı sorgulama ekranları. 

 Teknik Detaylar
Veritabanı Tasarımı
Sistem şu tablolar üzerine inşa edilmiştir: 

KULLANICI, UYE, KITAP, KATEGORI, ODUNC, CEZA ve LOG_ISLEM. 

Veritabanı Programlama (Backend Logic)
Bu projede iş mantığı doğrudan SQL düzeyinde işlenmiştir:


Stored Procedures: Ödünç verme (sp_YeniOduncVer), teslim alma (sp_KitapTeslimAl) ve üye özeti raporlama. 


Triggers: Stok miktarının otomatik güncellenmesi, ceza tahakkuk ettiğinde borcun güncellenmesi ve log kayıtlarının oluşturulması.  

Youtube Linki: https://youtu.be/wJQrmm9_RWM 
