Kütüphane projenin README dosyasını daha profesyonel ve kurumsal bir görünüme kavuşturalım. Markdown formatını kullanarak başlıkları belirginleştirdim ve teknik detayları listeler halinde organize ettim.

Aşağıdaki metni kopyalayıp GitHub'daki README dosyanla değiştirebilirsin:

Kütüphane Yönetim Sistemi (Library Management System)
Java ve MySQL kullanılarak geliştirilmiş, tam kapsamlı bir kütüphane yönetim sistemidir. Proje, veritabanı düzeyinde iş kuralları ve veri bütünlüğü üzerine odaklanmaktadır.
+4

Proje Hakkında
Bu sistem; üyelerin kimlik bilgilerinin, kitap stoklarının ve ödünç verme süreçlerinin yönetilmesini sağlar. Ödünç verme sürecinde stok kontrolü, gecikme takibi ve ceza hesaplamasını içeren tam bir iş akışına sahiptir.
+1

Temel Özellikler

Gelişmiş Veritabanı Mimarisi: Tasarım 3NF (Third Normal Form) düzeyinde normalize edilmiştir.


Otomatik İş Akışları: Saklı yordamlar (Stored Procedures) ve tetikleyiciler (Triggers) ile yönetilen süreçler mevcuttur.


Rol Tabanlı Erişim: Admin ve Görevli rolleri ile sistem güvenliği sağlanmaktadır.


Dinamik Raporlama: Tarih aralığına, kategoriye ve stok durumuna göre detaylı sorgulama ekranları bulunur.

Teknik Detaylar
Veritabanı Tasarımı
Sistem aşağıdaki tablolar üzerine inşa edilmiştir:

KULLANICI, UYE, KITAP, KATEGORI, ODUNC, CEZA ve LOG_ISLEM.

Veritabanı Programlama (Backend Logic)
İş mantığı doğrudan SQL düzeyinde işlenerek hata riski minimuma indirilmiştir:


Saklı Yordamlar (Stored Procedures): Ödünç verme süreci (sp_YeniOduncVer), teslim alma ve ceza yönetimi (sp_KitapTeslimAl) ile üye özeti raporlama (sp_UyeOzetRapor).
+1


Tetikleyiciler (Triggers): Stok miktarının otomatik güncellenmesi, ceza tahakkuk ettiğinde üye borcunun güncellenmesi ve kritik işlemler için log kayıtlarının oluşturulması.

Proje Sunumu
Sistemin detaylı çalışma mantığını ve ekran arayüzlerini incelemek için tanıtım videosuna göz atabilirsiniz:


Youtube: Kütüphane Sistemi Tanıtımı
