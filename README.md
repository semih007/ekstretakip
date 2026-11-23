#yükleyerek olabilecek bütün riskleri kabul etmiş sayılırsınız. sorumluluk kabul etmiyorum.

# Ekstre Takip

Bugün yapacağınız bir harcama hangi kartın ekstresine daha geç yansır? Ekstre Takip, kaydettiğiniz kredi kartlarının kesim tarihlerini karşılaştırır ve bir sonraki kesimine en çok gün kalan kartı ana ekranda gösterir. Aynı ekrandaki takvimden yaklaşan ekstre ve son ödeme günlerinizi takip edebilirsiniz.

**Sürüm 1.0.0** · Android 8.0 ve üzeri · Türkçe · Çevrimdışı

## İlk kartınızı ekleyin

1. Sağ üstteki **⋮ → Kartlarım** menüsünü açıp **+** düğmesine dokunun.
2. Karta tanıyacağınız bir ad verin ve ekstrenin her ay kaçıncı gün kesildiğini girin. **Son ödeme kaç gün sonra?** alanına kesim ile son ödeme arasındaki takvim günü sayısını yazın.
3. Bu kart için istediğiniz öteleme seçeneklerini belirleyin ve **Kaydet** düğmesine dokunun.

Ana ekrana döndüğünüzde bugünün kart önerisini görürsünüz. Takvimde **Ekstre kesimi** ve **Son ödeme** ayrı renklerle işaretlenir. Bir güne dokunduğunuzda o tarihteki kart adları listelenir. Aylar arasında oklarla geçiş yapabilirsiniz. **Kartlarım** ekranında her kartın yaklaşan tarihlerini görebilir, kartı düzenleyebilir veya silebilirsiniz.

## Hangi kart önerilir?

Örneğin A kartının ekstresi her ayın **16’sında**, B kartının ekstresi **29’unda** kesilsin. İki kartta da öteleme kapalıyken öneri bir günde değişebilir.

| Bugün | A kartının sıradaki kesimi | B kartının sıradaki kesimi | Öneri |
| --- | --- | --- | --- |
| 16 Kasım | 16 Kasım · bugün | 29 Kasım · 13 gün sonra | **B kartı** |
| 17 Kasım | 16 Aralık · 29 gün sonra | 29 Kasım · 12 gün sonra | **A kartı** |

Kesim günü bugünse kalan süre sıfırdır. İki kartın kesimine eşit süre kaldığında ilk eklediğiniz kart seçilir. Seçtiğiniz kesim günü o ayda yoksa uygulama ayın son gününü kullanır. Örneğin ayın 31’inde kesilen bir ekstre için kasımda 30’u esas alınır.

### Öteleme ve son ödeme

Her kart için üç bağımsız öteleme seçeneği vardır. **Hafta sonu** cumartesi ve pazarı, **resmî tatil** 1 Ocak, 23 Nisan, 1 Mayıs, 19 Mayıs, 15 Temmuz, 30 Ağustos ve 29 Ekim’i, **yarım gün tatil** ise 28 Ekim’i kapsar. Dinî bayramlar ve idari izin köprüleri kapsam dışıdır.

Uygulama her günü kartın açık seçeneklerine göre kontrol eder. Gün engelliyse ertesi güne geçer ve ilk açık günde durur. Örneğin 29 Ekim 2027 Cuma günkü kesim, resmî tatil ötelemesi açıkken 30 Ekim Cumartesiye taşınır. Hafta sonu ötelemesi de açıksa kesim 1 Kasım Pazartesiye kalır.

Son ödeme için hesaplanan kesim tarihine karttaki ödeme süresi (1–60 takvim günü) eklenir. Aynı üç seçenek bu tarihe de uygulanır. Kart önerisini hesaplanan kesim tarihi belirler.

## Varsayımlar

- Yeni kartlarda hafta sonu ve resmî tatil ötelemesi açık, yarım gün ötelemesi kapalıdır.
- Eski tek anahtar hafta sonu ve resmî tatil seçeneklerine aktarılır. Yarım gün kapalı başlar. Hiç öteleme kaydı yoksa üç seçenek de kapalıdır. Yeni alanlardan biri varsa eksik olanlar kapalı kabul edilir.
- Yeni kartta ödeme süresi alanı boş açılır. Kartı kaydetmek için kesimden sonra kaç takvim günü içinde ödeme yapılacağını 1–60 arasında girin.

## Gizlilik

Kart eklemek için bir ad ve tarih bilgileri yeterlidir. Uygulama çevrimdışı çalışır ve kayıtları cihazdaki özel uygulama alanında saklar. Bulut yedeklemesi kapalıdır. Kart verileri cihaz aktarımının dışında tutulur.

## Kaynak koddan derleme

Depoyu klonlayın ve `settings.gradle.kts` dosyasının bulunduğu dizini açın. İlk derlemede Gradle Wrapper dağıtımı, eklentileri ve kütüphaneleri indirir.

1. İşletim sisteminize uygun **Temurin 21 JDK** arşivini açın. `JAVA_HOME` değerini içinde `bin/java` ve `bin/javac` bulunan JDK dizinine ayarlayın. [Temurin indirmeleri](https://adoptium.net/temurin/releases/?version=21)
2. Android SDK Command-line Tools arşivini açın. Arşivdeki `cmdline-tools` içeriğini SDK kökünün altında `cmdline-tools/latest/` dizinine koyun. `ANDROID_HOME` bu SDK kökünü göstermeli. [Android araçlarını indir](https://developer.android.com/studio#command-tools) · [SDK yöneticisi](https://developer.android.com/tools/sdkmanager)
3. Windows PowerShell örneğinde ilk iki yolu kendi dizinlerinize göre değiştirin. Değişkenler açık terminal oturumu için geçerlidir.

```powershell
$env:JAVA_HOME = 'C:\AndroidAraclari\jdk-21'
$env:ANDROID_HOME = 'C:\AndroidAraclari\sdk'
$sdkmanager = Join-Path $env:ANDROID_HOME 'cmdline-tools\latest\bin\sdkmanager.bat'
& $sdkmanager "--sdk_root=$env:ANDROID_HOME" --licenses
& $sdkmanager "--sdk_root=$env:ANDROID_HOME" 'platforms;android-37.0' 'build-tools;36.0.0' 'platform-tools'
.\gradlew.bat testDebugUnitTest assembleRelease
```

Linux ve macOS’ta `JAVA_HOME` ve `ANDROID_HOME` tanımlandıktan sonra proje kökünde çalıştırın.

```sh
sdkmanager="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"
"$sdkmanager" --sdk_root="$ANDROID_HOME" --licenses
"$sdkmanager" --sdk_root="$ANDROID_HOME" 'platforms;android-37.0' 'build-tools;36.0.0' 'platform-tools'
./gradlew testDebugUnitTest assembleRelease
```

Release APK `app/build/outputs/apk/release/app-release.apk` yoluna yazılır. Birim testlerinin raporu `app/build/reports/tests/testDebugUnitTest/index.html` içinde açılır.

GitHub Actions, dallara yapılan gönderimlerde ve pull request açıldığında aynı testleri, release derlemesini ve APK izin denetimini çalıştırır. Kendi GitHub kopyanızda başarılı **Android** çalıştırmasının **Artifacts** bölümünden `ekstre-takip-apk` indirilebilir. `v*` etiketi gönderildiğinde APK GitHub Release’e de eklenir.

## APK imzalama

GitHub Actions her çalıştırmada runner’ın debug anahtarıyla imzalanmış bir release APK üretir. Depoya imzalama secret’ı eklemek gerekmez. Geçici runner’lar farklı anahtarlar oluşturabildiği için CI çıktıları temiz kurulumla test edilebilir.

Kendi anahtarınızla yerel release APK üretmek için aşağıdaki dört ortam değişkenini tanımlayıp `assembleRelease` görevini çalıştırın. Yayımladığınız uygulamaya güncelleme sunarken aynı anahtarı kullanın.

| Değişken | Değer |
| --- | --- |
| `KEYSTORE_FILE` | Keystore dosyasının yolu |
| `KEYSTORE_PASSWORD` | Keystore parolası |
| `KEY_ALIAS` | Anahtarın takma adı |
| `KEY_PASSWORD` | Anahtar parolası |

## Kullanılan sürümler

Uygulama 1.0.0 (1), minimum SDK 26, derleme ve hedef SDK 37. Gradle 9.3.1, AGP 9.1.1, Kotlin ve Compose Compiler 2.3.21, Compose BOM 2026.06.00, Activity Compose 1.13.0, JUnit 4.13.2. Java ve Kotlin hedefi 17, CI ortamı Temurin 21.

## Proje yapısı

- `app/src/main` uygulama kodu ve kaynakları
- `app/src/test` kart seçimi, tarih ötelemesi ve takvim testleri
- `gradle/libs.versions.toml` derleme ve bağımlılık sürümleri
- `.github/workflows/build.yml` GitHub Actions derleme, test ve APK yayımlama iş akışı
