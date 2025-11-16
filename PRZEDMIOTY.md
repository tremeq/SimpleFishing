# 🏺 SimpleFishing - System Przedmiotów Morskich

## 📋 Spis Treści
1. [Wprowadzenie](#wprowadzenie)
2. [Jak Działa System](#jak-działa-system)
3. [Rzadkości Przedmiotów](#rzadkości-przedmiotów)
4. [Lista Przedmiotów](#lista-przedmiotów)
5. [Konfiguracja](#konfiguracja)
6. [Długoterminowa Rozgrywka](#długoterminowa-rozgrywka)

---

## 🌊 Wprowadzenie

Oprócz ryb, gracze mogą teraz wyławiać różnorodne **przedmioty morskie** - od śmieci, przez skarby, aż po **mityczne artefakty**!

### Cechy Systemu:
- ✅ **25% szans na przedmiot** zamiast ryby (konfigurowalne)
- ✅ **6 poziomów rzadkości** - od Śmieci do Mitycznych
- ✅ **26 unikalnych przedmiotów** morskich
- ✅ **Enchantmenty i efekty specjalne** na przedmiotach
- ✅ **Pełna konfigurowalność** w YAML
- ✅ **Długoterminowa rozgrywka** - trudne do zdobycia skarby

---

## ⚙️ Jak Działa System

### 1. **Losowanie Drop Type**
Podczas łowienia system losuje:
- **75% szans** → Wypadnie **ryba**
- **25% szans** → Wypadnie **przedmiot**

### 2. **Losowanie Przedmiotu**
Jeśli wypadnie przedmiot, system losuje jego rzadkość:
```
ŚMIECI         60.0   (60% szans)  ← Bardzo często
POSPOLITY      30.0   (30% szans)
NIEPOSPOLITY    8.0   (8% szans)
RZADKI          1.5   (1.5% szans)
LEGENDARNE      0.4   (0.4% szans)
MITYCZNE        0.1   (0.1% szans) ← Ultra rzadkie!
```

### 3. **Modyfikator Szczęścia**
Customowe wędki z bonusem szczęścia zwiększają szanse na rzadsze przedmioty!

---

## 🎨 Rzadkości Przedmiotów

| Rzadkość | Kolor | Szansa | Przykłady |
|----------|-------|--------|-----------|
| **ŚMIECI** | `&8` Ciemnoszary | 60.0 | Stare buty, butelki, gnijące mięso |
| **POSPOLITY** | `&f` Biały | 30.0 | Muszle, wodorosty, kamyki |
| **NIEPOSPOLITY** | `&a` Zielony | 8.0 | Perły, korale, bursztyn |
| **RZADKI** | `&9` Niebieski | 1.5 | Wielkie perły, mapy skarbu, złote haczyki |
| **LEGENDARNE** | `&6` Złoty | 0.4 | Diamentowe perły, Korona Atlantydy, Trydent Posejdona |
| **MITYCZNE** | `&d&l` Różowy Bold | 0.1 | Kryształ Morskich Bogów, Artefakt Atlantydy |

---

## 📦 Lista Przedmiotów

### 🗑️ ŚMIECI (5 przedmiotów)

#### Stara Butelka
- **Wartość:** 0.5 monet
- **Opis:** Zaśmiecona butelka wyłowiona z morza

#### Zepsute Buty
- **Wartość:** 1.0 monet
- **Opis:** Całkowicie zniszczone buty skórzane

#### Zgniła Skóra
- **Wartość:** 0.3 monet
- **Opis:** Kawałek zgniłego mięsa, nie nadaje się do jedzenia

#### Stary Kij
- **Wartość:** 0.2 monet
- **Opis:** Zmoczony kij drewna, prawie się rozpada

#### Zardzewiały Haczyk
- **Wartość:** 2.0 monet
- **Opis:** Stary, zardzewiały haczyk wędkarski

---

### 🐚 POSPOLITE (5 przedmiotów)

#### Muszla
- **Wartość:** 5.0 monet
- **Material:** NAUTILUS_SHELL
- **Opis:** Zwykła muszla morska do kolekcji

#### Wodorosty Morskie
- **Wartość:** 3.0 monet
- **Opis:** Świeże wodorosty z oceanu - używane w kuchni

#### Piasek Morski
- **Wartość:** 1.5 monet
- **Opis:** Drobny piasek z dna morza

#### Kamyk Morski
- **Wartość:** 2.0 monet
- **Opis:** Gładki kamyk wyszlifowany przez fale

#### Rozgwiazda
- **Wartość:** 8.0 monet
- **Material:** ORANGE_DYE (Custom Model Data: 1001)
- **Opis:** Kolorowa rozgwiazda morska, żywa i zdrowa

---

### 💎 NIEPOSPOLITE (5 przedmiotów)

#### Mała Perła
- **Wartość:** 25.0 monet
- **Material:** ENDER_PEARL
- **Opis:** Mała perła znaleziona w muszli, ceniona przez jubilerów

#### Koral
- **Wartość:** 18.0 monet
- **Material:** TUBE_CORAL
- **Opis:** Piękny koralowiec - dekoracyjny i wartościowy

#### Bursztyn Morski
- **Wartość:** 30.0 monet
- **Material:** ORANGE_STAINED_GLASS
- **Opis:** Kawałek bursztynu z morza, zawiera małe inkluzje

#### Meduza w Słoiku
- **Wartość:** 22.0 monet
- **Material:** POTION
- **Enchantment:** LUCK I
- **Opis:** Świecąca meduza zamknięta w słoiku, świeci w ciemności!

#### Szklana Kula Rybaka
- **Wartość:** 35.0 monet
- **Opis:** Stara szklana kula używana przez rybaków do sieci - antyk z oceanu

---

### 🔷 RZADKIE (5 przedmiotów)

#### Wielka Perła
- **Wartość:** 100.0 monet
- **Material:** ENDER_PEARL
- **Enchantment:** LUCK II
- **Opis:** Duża, lśniąca perła - niezwykle wartościowa! Idealna do biżuterii

#### Mapa Skarbu
- **Wartość:** 75.0 monet
- **Material:** FILLED_MAP
- **Opis:** Stara mapa prowadząca do skarbu - może prowadzić do fortuny!

#### Złoty Haczyk
- **Wartość:** 120.0 monet
- **Material:** GOLD_INGOT
- **Enchantment:** LUCK III
- **Efekt:** +15% szansy na rzadkie ryby
- **Opis:** Haczyk wykonany ze złota - przynosi szczęście w łowieniu!

#### Antyczna Moneta
- **Wartość:** 85.0 monet
- **Material:** GOLD_NUGGET
- **Opis:** Moneta z zatopionego statku - bezcenna dla historyków

#### Kompas Kapitana
- **Wartość:** 95.0 monet
- **Material:** COMPASS
- **Enchantment:** UNBREAKING III
- **Opis:** Kompas należący do słynnego kapitana, zawsze wskazuje skarb

---

### 👑 LEGENDARNE (4 przedmioty)

#### Diamentowa Perła
- **Wartość:** 500.0 monet
- **Material:** DIAMOND
- **Enchantmenty:** LUCK V, FORTUNE III
- **Opis:** Perła ze skamieniałym diamentem w środku - **NIEZWYKLE RZADKA!**
- **Uwaga:** Jeden z najcenniejszych skarbów morza

#### Korona Atlantydy
- **Wartość:** 1,000.0 monet
- **Material:** GOLDEN_HELMET
- **Enchantmenty:**
  - RESPIRATION V
  - AQUA_AFFINITY I
  - UNBREAKING X
- **Efekt:** +50% szansy na mityczne ryby
- **Opis:** Korona z zatopionych ruin Atlantydy - moc starożytnych morskich królów

#### Trydent Posejdona
- **Wartość:** 2,000.0 monet
- **Material:** TRIDENT
- **Enchantmenty:**
  - LOYALTY X
  - RIPTIDE V
  - IMPALING X
  - UNBREAKING X
- **Opis:** Trydent należący do samego Posejdona - kontroluje oceany, niezniszczalny

#### Serce Oceanu
- **Wartość:** 1,500.0 monet
- **Material:** HEART_OF_THE_SEA
- **Enchantment:** LUCK X
- **Opis:** Pulsujące serce samego oceanu - źródło całej morskiej magii
- **Moc:** **MITYCZNA MOC** - daje nieograniczoną moc pod wodą

---

### ✨ MITYCZNE (2 przedmioty)

#### Kryształ Morskich Bogów
- **Wartość:** 10,000.0 monet
- **Material:** NETHER_STAR
- **Enchantmenty:** LUCK XX, FORTUNE X, UNBREAKING XX
- **Rzadkość:** **NAJRZADSZY PRZEDMIOT!**

**Efekty:**
- ✨ Nieśmiertelność pod wodą
- ✨ Widzenie w oceanie
- ✨ Kontrola nad istotami morskimi
- ✨ +100% szansy na boskie ryby

**Statystyka:** Tylko **1 na 10,000 rybaków** kiedykolwiek go znajdzie...

#### Artefakt Atlantydy
- **Wartość:** NIE DO SPRZEDANIA
- **Material:** TOTEM_OF_UNDYING
- **Enchantmenty:** LUCK XXV, PROTECTION X
- **Custom Szansa:** 0.05 (ultra rzadki!)

**Legenda:**
> Kto go znajdzie, odnajdzie Atlantydę

**Moc:**
- 🌊 Może przywrócić życie utonięciom
- 🏗️ Daje moc budowy pod wodą
- 🔱 **BOSKA MOC**

---

## 🔧 Konfiguracja

### config.yml

```yaml
# System dropów przedmiotów
drop_system:
  # Czy przedmioty mogą wypadać?
  wlaczone: true

  # Szansa na przedmiot zamiast ryby (0.0 - 1.0)
  # 0.25 = 25% szans na przedmiot
  szansa_na_przedmiot: 0.25

  # Czy wyświetlać wiadomość?
  wiadomosc_po_przedmiocie: true

# Szanse rzadkości przedmiotów
szanse_przedmiotow:
  wlaczone: true
  SMIECI: 60.0
  POSPOLITY: 30.0
  NIEPOSPOLITY: 8.0
  RZADKI: 1.5
  LEGENDARNE: 0.4
  MITYCZNE: 0.1
```

### items.yml

Każdy przedmiot w `items.yml`:
```yaml
nazwa_przedmiotu:
  nazwa: "&aNazwa"
  material: MATERIAL_TYPE
  rzadkosc: RZADKI
  opis:
    - "Linia 1 opisu"
    - "Linia 2"
  wartosc_sprzedazy: 100.0
  custom_szansa: 1.0        # Opcjonalne
  custom_model_data: 1001   # Opcjonalne
  enchantmenty:             # Opcjonalne
    - "LUCK:3"
    - "FORTUNE:2"
```

---

## 📈 Długoterminowa Rozgrywka

### Zmiany dla Długoterminowej Rozgrywki:

#### 1. **Zmniejszone Szanse Ryb** (o ~30%)
```
BARDZO_POSPOLITA: 50.0  (było 75.0)
POSPOLITA: 35.0         (było 50.0)
NIEPOSPOLITA: 20.0      (było 30.0)
RZADKA: 10.0            (było 15.0)
BARDZO_RZADKA: 4.5      (było 7.0)
EPICKI: 2.5             (było 4.0)
LEGENDARNA: 0.6         (było 0.9)
MITYCZNA: 0.05          (było 0.1)
BOSKA: 0.005            (było 0.01)
```

#### 2. **System Przedmiotów**
- 25% szans na przedmiot → więcej różnorodności
- Śmieci są częste → realistyczne łowienie
- Skarby są **BARDZO RZADKIE** → emocjonujące momenty

#### 3. **Ekonomia**
- Przedmioty mają różne wartości sprzedaży
- Legendarne przedmioty warte tysiące monet
- Śmieci prawie bezwartościowe

### Przykładowe Sesje Łowienia:

**Sesja 1-10 łowień:**
- 7x Ryby pospolite
- 2x Śmieci (butelka, kij)
- 1x Muszla

**Sesja 50-100 łowień:**
- 65x Różne ryby
- 25x Przedmioty pospolite
- 8x Przedmioty niepospolite
- 2x Rzadkie przedmioty
- 0x Legendarne (jeszcze nie!)

**Sesja 500+ łowień:**
- Szansa na pierwszy **Legendarny** przedmiot
- Może wypaść Wielka Perła lub Mapa Skarbu
- **Mityczne** dalej prawie niemożliwe

**Sesja 5000+ łowień:**
- Może wypaść pierwszy **Mityczny** artefakt
- Kryształ Morskich Bogów lub Artefakt Atlantydy
- **MEGA OSIĄGNIĘCIE** dla gracza!

---

## 🎯 Cele Systemu

1. **Długoterminowa Motywacja** - rzadkie przedmioty jako cel
2. **Różnorodność** - nie tylko ryby, ale też skarby
3. **Realizm** - czasem wypadają śmieci
4. **Ekonomia** - wartościowe przedmioty można sprzedać
5. **Kolekcjonowanie** - gracze chcą złapać wszystko
6. **Legendy** - Mityczne przedmioty to legendy serwera

---

## 💡 Pomysły na Rozszerzenie

- **Questy** - zbieraj określone przedmioty za nagrody
- **Crafting** - użyj przedmiotów do tworzenia specjalnych itemów
- **Exchangowanie** - wymień przedmioty u NPC na bonusy
- **Sezonowe eventy** - specjalne przedmioty w określonych porach roku
- **Osiągnięcia** - odblokuj achievementy za znalezienie przedmiotów

---

**SimpleFishing - Przedmioty Morskie** 🏺⚓🌊

*Każde łowienie to nowa przygoda!*
