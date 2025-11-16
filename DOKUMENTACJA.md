# SimpleFishing - Dokumentacja Pluginu

**Wersja:** 1.0.0
**Autor:** tremeq
**Platforma:** Paper/Spigot 1.21
**Java:** 21

---

## 📋 Spis Treści

1. [Wprowadzenie](#wprowadzenie)
2. [Funkcje Pluginu](#funkcje-pluginu)
3. [Instalacja](#instalacja)
4. [Konfiguracja](#konfiguracja)
5. [Komendy](#komendy)
6. [Uprawnienia](#uprawnienia)
7. [API dla Developerów](#api-dla-developerów)
8. [PlaceholderAPI](#placeholderapi)
9. [Integracje](#integracje)
10. [Przykłady Użycia](#przykłady-użycia)
11. [FAQ](#faq)

---

## 🎣 Wprowadzenie

**SimpleFishing** to zaawansowany plugin do Minecraft (Paper/Spigot 1.21) autorstwa **tremeq**, który całkowicie rozbudowuje system łowienia ryb. Plugin wprowadza:

- **Customowe ryby** z unikalnymi właściwościami
- **System konkursów** łowienia z nagrodami
- **Sklep ryb** z integracją Vault
- **Customowe wędki** z systemem ulepszania
- **Przynęty (baity)** zwiększające szanse na rzadkie ryby
- **Pełne GUI** z integracją Citizens NPC
- **PlaceholderAPI** dla scoreboardów i tabulatorów

---

## ⭐ Funkcje Pluginu

### 1. System Customowych Ryb

Plugin oferuje kompletny system customowych ryb z następującymi cechami:

- **6 poziomów rzadkości:**
  - Pospolita (50% szans)
  - Niepospolita (30% szans)
  - Rzadka (15% szans)
  - Epicka (4% szans)
  - Legendarna (0.9% szans)
  - Mityczna (0.1% szans)

- **Właściwości ryb:**
  - Unikalna nazwa i wygląd (Custom Model Data)
  - Zmienna długość (min-max w cm)
  - Własne lore
  - Efekty potion po złowieniu
  - Dynamiczna cena zależna od rzadkości i długości
  - Zapisywanie w NBT (Persistent Data Container)

### 2. System Konkursów

- **Tryby konkursów:**
  - Największa ryba - wygrywa gracz z najdłuższą rybą
  - Najwięcej ryb - wygrywa gracz z największą liczbą złowionych ryb
  - Suma długości - wygrywa gracz z największą sumą długości wszystkich ryb
  - Najdłuższa ryba - alternatywny tryb

- **Funkcje:**
  - Automatyczne śledzenie wyników
  - System nagród (pieniądze, komendy, itemy)
  - Ranking graczy w czasie rzeczywistym
  - Powiadomienia o rozpoczęciu i zakończeniu

### 3. Sklep Ryb

- **Zabezpieczenia:**
  - Ochrona przed duplikacją itemów
  - Cooldown między transakcjami (500ms)
  - Walidacja NBT przed sprzedażą

- **Funkcje:**
  - Sprzedaż pojedynczych ryb
  - Sprzedaż wszystkich ryb z ekwipunku
  - Dynamiczne ceny zależne od rzadkości i długości
  - Integracja z Vault Economy

### 4. System Przynęt (Baitów)

- **Typy bonusów:**
  - Ogólny bonus do szansy złowienia
  - Bonus dla konkretnych rzadkości
  - Preferencje dla określonych gatunków ryb

- **Właściwości:**
  - Maksymalna liczba użyć
  - Możliwość kupienia w sklepie
  - Nakładanie na wędki

### 5. System Wędek

- **Właściwości wędek:**
  - Podstawowy poziom szczęścia
  - Maksymalna liczba slotów na przynęty
  - System wytrzymałości
  - Ulepszenia (enchantmenty wędek)

- **Typy ulepszeń:**
  - Luck Boost - zwiększa szczęście
  - Durability Boost - zwiększa wytrzymałość
  - Bait Capacity - dodatkowe sloty na baity
  - Rare Fish Chance - większa szansa na rzadkie ryby
  - Size Bonus - większe ryby
  - Speed Bonus - szybsze łowienie

### 6. System GUI

- **Główne GUI** (otwierane przez NPC lub komendę):
  - Kolekcja ryb
  - Sklep
  - Konkursy
  - Wędki i ulepszenia
  - Informacje

- **Interaktywne menu:**
  - Obsługa kliknięć
  - Dynamiczne odświeżanie
  - Customizowalne tytuły

### 7. Integracje

#### **Vault**
- Obsługa ekonomii serwerowej
- Wypłaty nagród w konkursach
- Sprzedaż ryb za pieniądze

#### **PlaceholderAPI**
- Ponad 15 placeholderów
- Statystyki konkursów
- Informacje o rybch

#### **Citizens**
- Kliknięcie w NPC otwiera GUI
- Metadata `simplefishing` dla NPC
- Pełna integracja z trait system

---

## 📥 Instalacja

### Wymagania:
- **Minecraft:** 1.21
- **Silnik:** Paper lub Spigot
- **Java:** 21
- **Vault** (opcjonalnie, dla ekonomii)
- **PlaceholderAPI** (opcjonalnie)
- **Citizens** (opcjonalnie, dla NPC)

### Kroki instalacji:

1. **Pobierz plugin** SimpleFishing-1.0.0.jar
2. **Umieść w folderze** `plugins/` twojego serwera
3. **Zainstaluj zależności:** Vault, PlaceholderAPI, Citizens (opcjonalnie)
4. **Uruchom serwer** - plugin utworzy folder konfiguracyjny
5. **Skonfiguruj** pliki w `plugins/SimpleFishing/`
6. **Zrestartuj serwer** lub użyj `/sf reload`

---

## ⚙️ Konfiguracja

**📖 Szczegółowy przewodnik konfiguracji szans:** Zobacz [KONFIGURACJA_SZANS.md](KONFIGURACJA_SZANS.md)

Plugin używa 5 głównych plików konfiguracyjnych:

### 1. `config.yml` - Główna konfiguracja

```yaml
ustawienia:
  prefix: "&6[SimpleFishing]&r "
  debug: false
  jezyk: "pl"

# NOWE! Konfigurowalne szanse rzadkości
szanse_rzadkosci:
  wlaczone: true
  POSPOLITA: 50.0       # Możesz modyfikować!
  NIEPOSPOLITA: 30.0
  RZADKA: 15.0
  EPICKI: 4.0
  LEGENDARNA: 0.9
  MITYCZNA: 0.1

  mnozniki_cen:
    POSPOLITA: 1.0
    MITYCZNA: 25.0

# Algorytm bonusów od szczęścia
algorytm_szczescia:
  typ: "linear"  # linear, exponential, logarithmic
  multiplier: 1.0
  wiekszy_bonus_dla_rzadkich: true

lowienie:
  zastap_domyslne: true
  mnoznik_czasu: 1.0
  wiadomosc_po_zlowieniu: true

sklep:
  wlaczony: true
  mnoznik_cen: 1.0
  cooldown: 500
  wymaga_vault: true

konkursy:
  wlaczone: true
  max_jednoczesnych: 1
  auto_nagrody: true
  powiadomienia: true

wedki:
  wlaczone: true
  zuzywaj_przynety: true
  wytrzymalosc: true
```

### 2. `fish.yml` - Konfiguracja ryb

Przykład ryby:

```yaml
ryby:
  dorsz:
    nazwa: "Dorsz"
    display_name: "&fDorsz"
    lore:
      - "&7Pospolita ryba morska"
    rzadkosc: "POSPOLITA"
    min_dlugosc: 20.0
    max_dlugosc: 60.0
    material: "COD"
    cena: 5.0
    custom_model_data: 0
    efekty:
      - "STRENGTH:10:1"  # Format: TYP:SEKUNDY:POZIOM
    # custom_szansa: 10.0  # OPCJONALNE - nadpisz szansę tej ryby!
```

**Dostępne rzadkości:**
- `POSPOLITA` (domyślnie 50%)
- `NIEPOSPOLITA` (30%)
- `RZADKA` (15%)
- `EPICKI` (4%)
- `LEGENDARNA` (0.9%)
- `MITYCZNA` (0.1%)

**NOWOŚĆ: Custom szansa dla konkretnej ryby!**
Możesz nadpisać szansę dla pojedynczej ryby używając `custom_szansa`:

```yaml
zloty_karp:
  rzadkosc: "LEGENDARNA"  # Normalna legenda = 0.9%
  custom_szansa: 5.0      # TA ryba = 5% (5x łatwiej!)
```

### 3. `baits.yml` - Konfiguracja przynęt

Przykład przynęty z pełnymi bonusami:

```yaml
baity:
  zlota_przyneta:
    nazwa: "Złota Przynęta"
    display_name: "&6Złota Przynęta"
    lore:
      - "&7Legendarna przynęta"
    material: "GOLD_NUGGET"
    szansa_bonus: 3.0              # x3 ogólny bonus
    max_uzycia: 1
    cena: 1000.0

    # Preferowane ryby
    preferencje_ryb:
      - "lewiatian"
      - "krol_oceanow"
    bonus_dla_preferencji: 5.0     # x5 dla preferowanych!

    # Bonusy dla rzadkości
    bonus_rzadkosci:
      LEGENDARNA: 5.0              # x5 dla legendarnych
      MITYCZNA: 10.0               # x10 dla mitycznych!
```

**Wszystkie bonusy się MNOŻĄ!** Przykład:
- Mityczna ryba (0.1%) + Złota Przynęta + Wędka Neptuna = **41% szansy!**

### 4. `rods.yml` - Konfiguracja wędek

Przykład wędki:

```yaml
wedki:
  drewniana:
    nazwa: "Drewniana Wędka"
    display_name: "&7Drewniana Wędka"
    lore:
      - "&7Podstawowa wędka"
    szczescie: 1.0
    max_baity: 1
    wytrzymalosc: 50.0
    cena: 50.0
```

### 5. `messages.yml` - Wiadomości

Wszystkie wiadomości pluginu są konfigurowalne:

```yaml
prefix: "&6[SimpleFishing]&r "

lowienie:
  zlowiles: "&aZłowiłeś %fish% &a(%length% cm)!"

sklep:
  sprzedano: "&aSprzedano %fish% za &e%cena% monet!"
```

**Dostępne placeholdery w wiadomościach:**
- `%fish%` - nazwa ryby
- `%length%` - długość ryby
- `%cena%` - cena
- `%liczba%` - liczba ryb
- `%nazwa%` - nazwa konkursu
- `%typ%` - typ konkursu
- `%czas%` - pozostały czas
- `%miejsce%` - miejsce w rankingu
- `%wynik%` - wynik gracza
- `%gracz%` - nazwa gracza

---

## 💻 Komendy

### Główne komendy:

| Komenda | Aliasy | Opis | Uprawnienie |
|---------|--------|------|-------------|
| `/simplefishing` | `/sf`, `/fishing` | Otwiera główne GUI | `simplefishing.use` |
| `/sf help` | - | Pokazuje pomoc | `simplefishing.use` |
| `/sf info` | - | Informacje o pluginie | `simplefishing.use` |
| `/sf shop` | - | Otwiera sklep ryb | `simplefishing.shop` |
| `/sf contest` | - | Informacje o konkursie | `simplefishing.contest` |
| `/sf reload` | - | Przeładowuje konfigurację | `simplefishing.reload` |

### Przykłady użycia:

```
/sf                    # Otwiera główne GUI
/sf help               # Pokazuje pomoc
/sf shop               # Otwiera sklep
/sf contest            # Sprawdza aktywny konkurs
/sf reload             # Przeładowuje config (admin)
```

---

## 🔐 Uprawnienia

### Podstawowe uprawnienia:

| Uprawnienie | Domyślnie | Opis |
|-------------|-----------|------|
| `simplefishing.*` | OP | Dostęp do wszystkich funkcji |
| `simplefishing.use` | TRUE | Podstawowe użycie pluginu |
| `simplefishing.admin` | OP | Komendy administracyjne |
| `simplefishing.shop` | TRUE | Dostęp do sklepu ryb |
| `simplefishing.contest` | TRUE | Udział w konkursach |
| `simplefishing.contest.manage` | OP | Zarządzanie konkursami |
| `simplefishing.gui` | TRUE | Dostęp do GUI |
| `simplefishing.rod.upgrade` | TRUE | Ulepszanie wędek |
| `simplefishing.bait.use` | TRUE | Używanie przynęt |
| `simplefishing.reload` | OP | Przeładowanie konfiguracji |

### Przykład konfiguracji w LuckPerms:

```
/lp group default permission set simplefishing.use true
/lp group default permission set simplefishing.shop true
/lp group vip permission set simplefishing.rod.upgrade true
/lp group admin permission set simplefishing.admin true
```

---

## 🔧 API dla Developerów

SimpleFishing oferuje kompletne API dla innych pluginów.

### Maven Dependency:

```xml
<dependency>
    <groupId>pl.tremeq</groupId>
    <artifactId>SimpleFishing-Core</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Podstawowe użycie API:

```java
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.SimpleFishingAPI;
import pl.tremeq.simplefishing.api.fish.Fish;
import pl.tremeq.simplefishing.api.fish.FishRegistry;

public class MojPlugin extends JavaPlugin {

    private SimpleFishingAPI api;

    @Override
    public void onEnable() {
        // Pobierz API
        SimpleFishingPlugin plugin = (SimpleFishingPlugin) Bukkit.getPluginManager()
            .getPlugin("SimpleFishing");
        api = plugin;

        // Użyj API
        FishRegistry registry = api.getFishRegistry();
        Fish fish = registry.wylosujRybe();

        getLogger().info("Wylosowana ryba: " + fish.getNazwa());
    }
}
```

### Rejestracja własnej ryby:

```java
Fish mojaRyba = new Fish.FishBuilder("moja_ryba")
    .nazwa("Moja Customowa Ryba")
    .displayName("§6Moja Ryba")
    .rzadkosc(FishRarity.LEGENDARNA)
    .minDlugosc(50.0)
    .maxDlugosc(150.0)
    .itemStack(new ItemStack(Material.TROPICAL_FISH))
    .bazowaCena(500.0)
    .build();

api.getFishRegistry().zarejestrujRybe(mojaRyba);
```

### Zarządzanie konkursami:

```java
// Rozpocznij konkurs
api.getContestManager().rozpocznijKonkurs("moj_konkurs");

// Dodaj wynik gracza
UUID playerId = player.getUniqueId();
api.getContestManager().dodajWynik(playerId, 150.5);

// Pobierz ranking
List<Map.Entry<UUID, Double>> ranking = api.getContestManager()
    .getRankingAktywnego();
```

### Eventy API:

Plugin wysyła customowe eventy które możesz obsługiwać:

```java
// TODO: Eventy będą dodane w kolejnych wersjach
// - FishCaughtEvent
// - ContestStartEvent
// - ContestEndEvent
// - FishSoldEvent
```

---

## 📊 PlaceholderAPI

Plugin oferuje integrację z PlaceholderAPI:

### Dostępne placeholdery:

#### Konkursy:
- `%simplefishing_contest_active%` - Czy jest aktywny konkurs (Tak/Nie)
- `%simplefishing_contest_name%` - Nazwa aktywnego konkursu
- `%simplefishing_contest_type%` - Typ konkursu
- `%simplefishing_contest_time%` - Pozostały czas (HH:MM:SS)
- `%simplefishing_contest_place%` - Miejsce gracza w rankingu
- `%simplefishing_contest_score%` - Wynik gracza
- `%simplefishing_contest_leader_1%` - Nick lidera (1 miejsce)
- `%simplefishing_contest_leader_2%` - Nick 2 miejsca
- `%simplefishing_contest_leader_3%` - Nick 3 miejsca
- `%simplefishing_contest_leader_score_1%` - Wynik lidera
- `%simplefishing_contest_leader_score_2%` - Wynik 2 miejsca
- `%simplefishing_contest_leader_score_3%` - Wynik 3 miejsca

#### Statystyki:
- `%simplefishing_fish_count%` - Liczba zarejestrowanych ryb
- `%simplefishing_bait_count%` - Liczba zarejestrowanych przynęt

### Przykład użycia w scoreboardzie:

```yaml
# DeluxeScoreboard config
lines:
  - "&6&lKonkurs Łowienia"
  - "&7Typ: &a%simplefishing_contest_type%"
  - "&7Czas: &a%simplefishing_contest_time%"
  - ""
  - "&eTwoje miejsce: &f#%simplefishing_contest_place%"
  - "&eTwój wynik: &f%simplefishing_contest_score%"
  - ""
  - "&6Top 3:"
  - "&71. %simplefishing_contest_leader_1% - %simplefishing_contest_leader_score_1%"
  - "&72. %simplefishing_contest_leader_2% - %simplefishing_contest_leader_score_2%"
  - "&73. %simplefishing_contest_leader_3% - %simplefishing_contest_leader_score_3%"
```

---

## 🔗 Integracje

### Vault
- **Wymagane:** Tak (dla ekonomii)
- **Funkcje:** Sprzedaż ryb, nagrody pieniężne, zakup wędek
- **Konfiguracja:** `config.yml` → `sklep.wymaga_vault`

### PlaceholderAPI
- **Wymagane:** Nie
- **Funkcje:** Placeholdery do scoreboardów
- **Konfiguracja:** Automatyczna po zainstalowaniu PAPI

### Citizens
- **Wymagane:** Nie
- **Funkcje:** NPC otwierające GUI pluginu
- **Konfiguracja:**
  1. Stwórz NPC: `/npc create SimpleFishing`
  2. Dodaj metadata: `/npc data set simplefishing true`
  3. Kliknięcie w NPC otworzy GUI

---

## 🎮 Przykłady Użycia

### Dla Graczy:

1. **Łowienie ryb:**
   - Użyj wędki i zacznij łowić
   - Automatycznie otrzymasz customowe ryby
   - Sprawdź statystyki ryby w lore

2. **Sprzedaż ryb:**
   - `/sf shop` - otwiera sklep
   - Kliknij na ryby które chcesz sprzedać
   - Otrzymasz pieniądze za każdą rybę

3. **Udział w konkursie:**
   - `/sf contest` - sprawdź aktywny konkurs
   - Łów ryby - wyniki będą automatycznie zapisywane
   - Najlepsi gracze otrzymają nagrody

4. **Ulepszanie wędek:**
   - Otwórz GUI wędek
   - Wybierz wędkę do ulepszenia
   - Nałóż przynęty aby zwiększyć szanse

### Dla Adminów:

1. **Dodawanie nowej ryby:**
   - Edytuj `fish.yml`
   - Dodaj nową sekcję z parametrami ryby
   - `/sf reload`

2. **Tworzenie konkursu:**
   - Skonfiguruj konkurs w kodzie lub przez API
   - Ustaw nagrody i czas trwania
   - Uruchom konkurs

3. **Konfiguracja NPC:**
   ```
   /npc create Rybak
   /npc data set simplefishing true
   ```

---

## ❓ FAQ

### Q: Czy plugin działa na Spigot?
**A:** Tak, plugin działa zarówno na Paper jak i Spigot 1.21.

### Q: Czy mogę dodać własne ryby?
**A:** Tak! Wystarczy edytować `fish.yml` i dodać nową sekcję.

### Q: Czy plugin wymaga Vault?
**A:** Nie jest wymagany, ale bez Vault nie będzie działać sklep ryb i nagrody pieniężne.

### Q: Jak zmienić szanse na złowienie rzadkich ryb?
**A:** Szanse są zdefiniowane w `FishRarity.java`. Możesz je zmienić poprzez modyfikację kodu lub użycie systemów wędek/przynęt które zwiększają szanse.

### Q: Czy ryby zapisują się w NBT?
**A:** Tak, plugin używa Persistent Data Container (PDC) do zapisywania ID ryby i jej długości.

### Q: Jak zintegrować z Citizens?
**A:** Stwórz NPC i ustaw metadata `simplefishing` na `true` używając `/npc data set simplefishing true`.

### Q: Czy mogę wyłączyć określone funkcje?
**A:** Tak, większość funkcji można włączyć/wyłączyć w `config.yml`.

### Q: Jak sprawdzić wersję pluginu?
**A:** Użyj komendy `/sf info`.

---

## 📞 Wsparcie

- **Autor:** tremeq
- **Wersja:** 1.0.0
- **Licencja:** Wszystkie prawa zastrzeżone

---

## 🔄 Changelog

### Wersja 1.0.0
- Pierwsza wersja pluginu
- System customowych ryb (6 rzadkości)
- System konkursów
- Sklep ryb z integracją Vault
- System przynęt i wędek
- GUI z integracją Citizens
- PlaceholderAPI
- Pełna konfigurowalność

---

**SimpleFishing** - Najlepszy plugin do łowienia ryb dla Minecraft!
