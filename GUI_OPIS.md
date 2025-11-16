# 🖥️ SimpleFishing - Opis GUI

## 📋 Spis Treści
1. [Główne GUI](#główne-gui)
2. [Menu Kolekcji Ryb](#menu-kolekcji-ryb)
3. [Sklep Ryb](#sklep-ryb)
4. [Menu Konkursów](#menu-konkursów)
5. [Menu Wędek](#menu-wędek)
6. [Integracja z Citizens NPC](#integracja-z-citizens-npc)

---

## 🏠 Główne GUI

**Otwieranie:**
- Komenda `/sf` lub `/simplefishing`
- Kliknięcie prawym w NPC Citizens z metadata `simplefishing`

**Wygląd:** (27 slotów - 3 rzędy)

```
┌─────────────────────────────────────────┐
│ ░ ░ ░ ░ ░ ░ ░ ░ ░ │  Rząd 1: Wypełnienie (szare szkło)
│                     │
│ ░ 🐟 ░ 💎 ░ 🏆 ░ 🎣 ░ │  Rząd 2: Opcje menu
│                     │
│ ░ ░ ░ ░ 📖 ░ ░ ░ ❌ │  Rząd 3: Info + Zamknij
└─────────────────────────────────────────┘
```

**Elementy:**

### Slot 10: 🐟 **Kolekcja Ryb**
- **Item:** `TROPICAL_FISH`
- **Nazwa:** `&6&lKolekcja Ryb`
- **Lore:**
  ```
  &7Przeglądaj wszystkie złowione ryby
  &7i ich statystyki

  &eKliknij aby otworzyć!
  ```
- **Akcja:** Otwiera menu kolekcji ryb

### Slot 12: 💎 **Sklep Ryb**
- **Item:** `EMERALD`
- **Nazwa:** `&a&lSklep Ryb`
- **Lore:**
  ```
  &7Sprzedaj swoje ryby
  &7za monety!

  &eKliknij aby otworzyć!
  ```
- **Akcja:** Otwiera sklep do sprzedaży
- **Wymagane uprawnienie:** `simplefishing.shop`

### Slot 14: 🏆 **Konkursy**
- **Item:** `DIAMOND`
- **Nazwa:** `&b&lKonkursy`
- **Lore:**
  ```
  &7Zobacz aktywne konkursy
  &7i swoje miejsce w rankingu

  &eKliknij aby otworzyć!
  ```
- **Akcja:** Otwiera menu konkursów
- **Wymagane uprawnienie:** `simplefishing.contest`

### Slot 16: 🎣 **Wędki**
- **Item:** `FISHING_ROD`
- **Nazwa:** `&d&lWędki`
- **Lore:**
  ```
  &7Zarządzaj swoimi wędkami
  &7i nakładaj przynęty

  &eKliknij aby otworzyć!
  ```
- **Akcja:** Otwiera menu wędek
- **Wymagane uprawnienie:** `simplefishing.rod.upgrade`

### Slot 22: 📖 **Informacje**
- **Item:** `BOOK`
- **Nazwa:** `&e&lInformacje`
- **Lore:**
  ```
  &7SimpleFishing v1.0.0
  &7Autor: &atremeq

  &7Zarejestrowanych ryb: &a[LICZBA]
  &7Zarejestrowanych przynęt: &a[LICZBA]
  ```
- **Akcja:** Tylko informacyjny (bez akcji)

### Slot 26: ❌ **Zamknij**
- **Item:** `BARRIER`
- **Nazwa:** `&c&lZamknij`
- **Akcja:** Zamyka GUI

---

## 🐟 Menu Kolekcji Ryb

**PLANOWANE** (będzie zaimplementowane w przyszłości)

**Wygląd:** (54 sloty - 6 rzędów)

```
┌─────────────────────────────────────────┐
│ ░ ░ ░ Kolekcja Ryb ░ ░ ░ │  Rząd 1: Nagłówek
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │  Rząd 2-5: Ryby
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │  (pogrupowane po rzadkości)
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │
│ ◀ ░ ░ ░ 🏠 ░ ░ ░ ▶ │  Rząd 6: Nawigacja
└─────────────────────────────────────────┘
```

**Funkcje:**
- Wyświetla wszystkie złowione ryby gracza
- Filtrowanie po rzadkości
- Statystyki: ile razy złowiono, największa długość
- Nawigacja między stronami

---

## 💰 Sklep Ryb

**PLANOWANE** (będzie zaimplementowane w przyszłości)

**Wygląd:** (54 sloty)

```
┌─────────────────────────────────────────┐
│ ░ ░ ░ Sklep Ryb ░ ░ ░ │
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │  Ryby z ekwipunku gracza
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │  + cena każdej
│ 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 🐟 │
│ ░ ░ ░ ░ ░ ░ ░ ░ ░ │
│ 🏠 ░ ░ 💰 ░ ░ ✅ ░ ░ │  Sprzedaj wszystkie
└─────────────────────────────────────────┘
```

**Funkcje:**
- Wyświetla ryby z ekwipunku
- Pokazuje cenę każdej ryby (bazowa × długość × rzadkość)
- Kliknięcie = sprzedanie pojedynczej ryby
- Przycisk "Sprzedaj wszystkie" - sprzedaje wszystkie ryby z ekwipunku
- Wymaga Vault + ekonomię

**Bezpieczeństwo:**
- Cooldown 500ms między transakcjami
- Walidacja NBT przed sprzedażą
- Zabezpieczenie przed duplikacją

---

## 🏆 Menu Konkursów

**PLANOWANE** (będzie zaimplementowane w przyszłości)

**Wygląd - Brak Aktywnego Konkursu:**

```
┌─────────────────────────────────────────┐
│ ░ ░ ░ Konkursy ░ ░ ░ │
│ ░ ░ ░ ░ ░ ░ ░ ░ ░ │
│ ░ ░ ░ ░ ░ ░ ░ ░ ░ │
│ ░ ░ ⏰ &cBrak konkursu! ⏰ ░ ░ │
│ ░ ░ ░ ░ ░ ░ ░ ░ ░ │
│ 🏠 ░ ░ ░ ░ ░ ░ ░ ░ │
└─────────────────────────────────────────┘
```

**Wygląd - Aktywny Konkurs:**

```
┌─────────────────────────────────────────┐
│ ░ 📊 Konkurs: [NAZWA] 📊 ░ │
│                     │
│ 🥇 1. Gracz1 - 125.5cm  │  Top 3
│ 🥈 2. Gracz2 - 98.2cm   │
│ 🥉 3. Gracz3 - 87.1cm   │
│                     │
│ ⏰ Pozostały czas: 15:32 │
│ 📍 Twoje miejsce: #5    │
│ 📏 Twój wynik: 75.3cm   │
│                     │
│ 🏠 ░ ░ 🏆 Nagrody 🏆 ░ ░ │
└─────────────────────────────────────────┘
```

**Funkcje:**
- Wyświetla informacje o aktywnym konkursie
- Ranking TOP graczy (aktualizowany dynamicznie)
- Pozostały czas (odliczanie)
- Miejsce i wynik gracza
- Przycisk "Nagrody" - pokazuje nagrody za miejsca

**Typy Konkursów:**
- **Największa Ryba** - kto złowi najdłuższą rybę
- **Najwięcej Ryb** - kto złowi najwięcej ryb
- **Suma Długości** - suma długości wszystkich złowionych ryb
- **Najdłuższa Suma** - największa suma złowiona

---

## 🎣 Menu Wędek

**PLANOWANE** (będzie zaimplementowane w przyszłości)

**Wygląd:**

```
┌─────────────────────────────────────────┐
│ ░ ░ ░ Twoje Wędki ░ ░ ░ │
│                     │
│ 🎣 Diamentowa Wędka 🎣  │
│ &7Szczęście: &a1.7      │
│ &7Sloty na przynęty: &a2/3 │
│ &7Wytrzymałość: &a85/200 │
│                     │
│ 🪱 Przynęty:        │
│ &a✓ Robak (8 użyć)  │
│ &a✓ Kukurydza (12 użyć) │
│ &7○ [Pusty slot]    │
│                     │
│ 🏠 ░ ➕ Dodaj ░ ➖ Usuń ░ │
└─────────────────────────────────────────┘
```

**Funkcje:**
- Wyświetla statystyki wędki
- Lista nałożonych przynęt
- Przycisk "Dodaj przynętę" - wybór z ekwipunku
- Przycisk "Usuń przynętę" - usuwa wybraną
- Pasek wytrzymałości
- Informacja o bonusach

**Dodatki:**
- Przewidywany bonus szczęścia
- Obliczone szanse na rzadkości
- Lista dostępnych ulepszeń

---

## 🤝 Integracja z Citizens NPC

### Konfiguracja NPC:

```bash
# Stwórz NPC
/npc create Rybak

# Dodaj metadata SimpleFishing
/npc data set simplefishing true

# Opcjonalnie: dostosuj skórkę
/npc skin [nick_gracza]
```

### Działanie:

1. Gracz **kliknie prawym** w NPC
2. Plugin sprawdza metadata `simplefishing`
3. Jeśli `true` → otwiera **Główne GUI**
4. Jeśli gracz nie ma uprawnień → komunikat błędu

### Przykład NPC Setup:

```
Nazwa NPC: &6&lRybak Marek
Lokalizacja: Spawn / Port / Plaża

Metadata:
- simplefishing: true

Zachowanie:
- Kliknięcie → Główne GUI
- Można dodać więcej NPC w różnych lokacjach
```

---

## 🎨 Customizacja GUI

### W `config.yml`:

```yaml
gui:
  tytuly:
    glowne: "&6&lSimpleFishing - Menu Główne"
    kolekcja: "&6&lKolekcja Ryb"
    sklep: "&a&lSklep Ryb"
    konkursy: "&b&lKonkursy"
    wedki: "&d&lWędki"
```

### Możliwości:
- Zmiana tytułów
- Zmiana kolorów
- Dodanie własnych opisów w lore

---

## 📊 Podsumowanie

### Główne GUI - **ZAIMPLEMENTOWANE** ✅
- Kod: `MainGui.java`
- Funkcjonalny system kliknięć
- Dynamiczne uprawnienia
- Integracja z Citizens

### Pozostałe GUI - **DO IMPLEMENTACJI** 🔨
- Kolekcja Ryb - planowane
- Sklep Ryb - planowane (logika ShopManager gotowa)
- Menu Konkursów - planowane
- Menu Wędek - planowane

### Architektura GUI:

```
SimpleFishingGui (klasa bazowa)
├── MainGui (zaimplementowane)
├── FishCollectionGui (TODO)
├── ShopGui (TODO)
├── ContestGui (TODO)
└── RodManagementGui (TODO)
```

Wszystkie GUI dziedziczą po `SimpleFishingGui` która zapewnia:
- Automatyczną obsługę kliknięć
- System odświeżania
- Jednolity wygląd
- Zarządzanie przez `GuiManager`

---

**SimpleFishing GUI** - Intuicyjne, czytelne i funkcjonalne! 🎣
