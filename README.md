# 🎣 SimpleFishing

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Custom-blue.svg)](LICENSE)

**SimpleFishing** to zaawansowany plugin do Minecraft (Paper/Spigot 1.21) rozbudowujący system łowienia ryb.

## ✨ Główne Funkcje

- 🐟 **Customowe Ryby** - 6 poziomów rzadkości, unikalne właściwości, efekty
- 🎲 **Pełna Kontrola Szans** - Konfigurowalne szanse przez YAML (rzadkości, pojedyncze ryby, bonusy)
- 🏆 **Konkursy Łowienia** - Różne tryby, nagrody, ranking w czasie rzeczywistym
- 💰 **Sklep Ryb** - Sprzedaż złowionych ryb, integracja z Vault
- 🎣 **Customowe Wędki** - System ulepszeń, sloty na przynęty, modyfikatory szczęścia
- 🪱 **Przynęty** - Zaawansowane bonusy (ogólne, dla rzadkości, dla konkretnych ryb)
- 🖥️ **GUI** - Intuicyjne menu, integracja z Citizens NPC
- 📊 **PlaceholderAPI** - Placeholdery do scoreboardów
- ⚙️ **API** - Pełne API dla developerów

## 📥 Instalacja

1. Pobierz `SimpleFishing-1.0.0.jar`
2. Umieść w folderze `plugins/` serwera
3. Zainstaluj zależności: **Vault**, **PlaceholderAPI**, **Citizens** (opcjonalnie)
4. Uruchom serwer
5. Skonfiguruj pliki w `plugins/SimpleFishing/`
6. Gotowe!

## 🔧 Wymagania

- **Minecraft:** 1.21
- **Silnik:** Paper lub Spigot
- **Java:** 21
- **Vault** (opcjonalnie, dla ekonomii)
- **PlaceholderAPI** (opcjonalnie)
- **Citizens** (opcjonalnie, dla NPC)

## 💻 Komendy

| Komenda | Opis |
|---------|------|
| `/sf` | Otwiera główne GUI |
| `/sf help` | Pokazuje pomoc |
| `/sf shop` | Otwiera sklep ryb |
| `/sf contest` | Informacje o konkursie |
| `/sf reload` | Przeładowuje konfigurację |

## 🔐 Uprawnienia

| Uprawnienie | Opis |
|-------------|------|
| `simplefishing.use` | Podstawowe użycie |
| `simplefishing.shop` | Dostęp do sklepu |
| `simplefishing.contest` | Udział w konkursach |
| `simplefishing.admin` | Komendy administracyjne |

## 📖 Dokumentacja

- **Pełna dokumentacja:** [DOKUMENTACJA.md](DOKUMENTACJA.md)
- **Przewodnik konfiguracji szans:** [KONFIGURACJA_SZANS.md](KONFIGURACJA_SZANS.md) ⭐ NOWOŚĆ!

## 🏗️ Struktura Projektu

```
SimpleFishing/
├── core/                          # Moduł core z API
│   └── src/main/java/pl/tremeq/simplefishing/api/
│       ├── SimpleFishingAPI.java
│       ├── fish/                  # System ryb
│       ├── bait/                  # System przynęt
│       ├── rod/                   # System wędek
│       ├── contest/               # System konkursów
│       ├── shop/                  # System sklepu
│       ├── gui/                   # System GUI
│       └── integration/           # Integracje
│
├── plugin-1-21/                   # Implementacja dla 1.21
│   └── src/main/
│       ├── java/pl/tremeq/simplefishing/
│       │   ├── SimpleFishingPlugin.java
│       │   ├── commands/          # Komendy
│       │   ├── listeners/         # Listenery
│       │   ├── gui/               # GUI
│       │   └── config/            # ConfigManager
│       └── resources/
│           ├── plugin.yml
│           ├── config.yml
│           ├── fish.yml           # Konfiguracja ryb
│           ├── baits.yml          # Konfiguracja przynęt
│           ├── rods.yml           # Konfiguracja wędek
│           └── messages.yml       # Wiadomości
│
├── DOKUMENTACJA.md                # Pełna dokumentacja
├── README.md                      # Ten plik
└── pom.xml                        # Maven config
```

## 🎯 Przykłady Konfiguracji

### Modyfikacja szans (`config.yml`):

```yaml
szanse_rzadkosci:
  wlaczone: true
  POSPOLITA: 40.0      # Zmniejsz pospolite
  LEGENDARNA: 5.0      # Zwiększ legendarne z 0.9!
  MITYCZNA: 2.0        # Zwiększ mityczne z 0.1!
```

### Dodawanie własnej ryby z custom szansą (`fish.yml`):

```yaml
ryby:
  moja_ryba:
    nazwa: "Moja Ryba"
    display_name: "&6Moja Customowa Ryba"
    lore:
      - "&7Wyjątkowa ryba!"
    rzadkosc: "LEGENDARNA"
    custom_szansa: 10.0   # NOWOŚĆ - nadpisz szansę!
    min_dlugosc: 50.0
    max_dlugosc: 150.0
    material: "TROPICAL_FISH"
    cena: 500.0
    custom_model_data: 100
    efekty:
      - "LUCK:60:2"
```

### Dodawanie zaawansowanej przynęty (`baits.yml`):

```yaml
baity:
  super_przyneta:
    nazwa: "Super Przynęta"
    display_name: "&aSuper Przynęta"
    lore:
      - "&7Świetna przynęta!"
    material: "WHEAT"
    szansa_bonus: 2.0              # x2 ogólnie
    max_uzycia: 5
    cena: 100.0
    preferencje_ryb:
      - "moja_ryba"
    bonus_dla_preferencji: 3.0     # x3 dla "moja_ryba"!
    bonus_rzadkosci:
      LEGENDARNA: 2.0              # x2 dla legendarnych
      MITYCZNA: 5.0                # x5 dla mitycznych!
```

## 🔌 API dla Developerów

```java
// Pobierz API
SimpleFishingAPI api = SimpleFishingPlugin.getInstance();

// Wylosuj rybę
Fish fish = api.getFishRegistry().wylosujRybe();

// Dodaj własną rybę
Fish customFish = new Fish.FishBuilder("custom_id")
    .nazwa("Moja Ryba")
    .rzadkosc(FishRarity.LEGENDARNA)
    .build();

api.getFishRegistry().zarejestrujRybe(customFish);

// Zarządzaj konkursami
api.getContestManager().rozpocznijKonkurs("konkurs_id");
```

## 📊 PlaceholderAPI

Dostępne placeholdery:

- `%simplefishing_contest_active%` - Czy jest aktywny konkurs
- `%simplefishing_contest_name%` - Nazwa konkursu
- `%simplefishing_contest_time%` - Pozostały czas
- `%simplefishing_contest_place%` - Miejsce gracza
- `%simplefishing_contest_leader_1%` - Lider rankingu
- I wiele więcej...

## 🛠️ Kompilacja

```bash
# Sklonuj repozytorium
git clone https://github.com/twojerepo/SimpleFishing.git

# Przejdź do folderu
cd SimpleFishing

# Zbuduj projekt
mvn clean package

# Plik JAR będzie w plugin-1-21/target/
```

## 📝 Licencja

Wszystkie prawa zastrzeżone © 2024 tremeq

## 👨‍💻 Autor

**tremeq**

## 🤝 Wsparcie

W razie pytań lub problemów:
- Otwórz Issue na GitHub
- Skontaktuj się z autorem

## 🎉 Podziękowania

Dziękujemy za korzystanie z SimpleFishing!

---

**SimpleFishing** - Najlepszy plugin do łowienia ryb dla Minecraft! 🎣
