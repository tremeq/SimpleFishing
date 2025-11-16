# 🎲 SimpleFishing - Przewodnik Konfiguracji Szans

**Kompletny przewodnik po systemie szans i ich modyfikacji przez pliki YAML**

---

## 📋 Spis Treści

1. [Wprowadzenie](#wprowadzenie)
2. [Szanse Rzadkości (config.yml)](#szanse-rzadkości-configyml)
3. [Custom Szanse dla Ryb (fish.yml)](#custom-szanse-dla-ryb-fishyml)
4. [Przynęty i Ich Bonusy (baits.yml)](#przynęty-i-ich-bonusy-baitsyml)
5. [Wędki i Szczęście (rods.yml)](#wędki-i-szczęście-rodsyml)
6. [Jak to Wszystko Działa Razem](#jak-to-wszystko-działa-razem)
7. [Przykłady i Obliczenia](#przykłady-i-obliczenia)
8. [Najczęstsze Pytania](#najczęstsze-pytania)

---

## 🌟 Wprowadzenie

SimpleFishing oferuje **3-poziomowy system konfiguracji szans**:

1. **Poziom 1: Szanse Rzadkości** (config.yml) - Bazowe szanse dla wszystkich ryb danej rzadkości
2. **Poziom 2: Custom Szanse Ryb** (fish.yml) - Nadpisanie szansy dla konkretnej ryby
3. **Poziom 3: Modyfikatory** (wędki + przynęty) - Bonusy zwiększające szanse podczas łowienia

---

## ⚙️ Szanse Rzadkości (config.yml)

### Podstawowa Konfiguracja

```yaml
szanse_rzadkosci:
  # Czy włączyć customowe szanse
  wlaczone: true

  # Wagi dla każdej rzadkości
  POSPOLITA: 50.0
  NIEPOSPOLITA: 30.0
  RZADKA: 15.0
  EPICKI: 4.0
  LEGENDARNA: 0.9
  MITYCZNA: 0.1
```

### Jak Działają Wagi?

Wagi są **względne**, nie bezpośrednie procenty:

**Przykład 1: Domyślne Wartości**
- Suma wag: 50 + 30 + 15 + 4 + 0.9 + 0.1 = **100**
- Szansa Pospolita: 50/100 = **50%**
- Szansa Mityczna: 0.1/100 = **0.1%**

**Przykład 2: Zwiększone Szanse na Rzadkie**
```yaml
POSPOLITA: 30.0      # Zmniejszono
MITYCZNA: 10.0       # Zwiększono z 0.1 na 10.0!
```
- Suma: 30 + 30 + 15 + 4 + 0.9 + 10 = **89.9**
- Szansa Pospolita: 30/89.9 = **33.4%** ↓
- Szansa Mityczna: 10/89.9 = **11.1%** ↑↑↑ (110x więcej!)

**Przykład 3: Tylko Legendarne i Mityczne**
```yaml
POSPOLITA: 0.0       # Wyłączone
NIEPOSPOLITA: 0.0    # Wyłączone
RZADKA: 0.0          # Wyłączone
EPICKI: 0.0          # Wyłączone
LEGENDARNA: 50.0     # 50%
MITYCZNA: 50.0       # 50%
```

### Mnożniki Ceny

```yaml
mnozniki_cen:
  POSPOLITA: 1.0      # Cena x1
  MITYCZNA: 25.0      # Cena x25
```

Jeśli ryba ma `bazowa_cena: 100`, to:
- Pospolita: 100 × 1.0 = **100 monet**
- Mityczna: 100 × 25.0 = **2500 monet**

---

## 🐟 Custom Szanse dla Ryb (fish.yml)

### Nadpisywanie Szansy Konkretnej Ryby

Możesz nadpisać szansę dla **konkretnej ryby**, ignorując rzadkość:

```yaml
ryby:
  zloty_karp:
    nazwa: "Złoty Karp"
    rzadkosc: "LEGENDARNA"     # Rzadkość = 0.9% normalnie
    custom_szansa: 5.0         # ALE ta ryba ma 5%!
    # ... reszta parametrów
```

### Przykłady Zastosowań

**1. Łatwiejsza Legendarna Ryba**
```yaml
latwy_legendarny_sum:
    rzadkosc: "LEGENDARNA"     # Normalni: 0.9%
    custom_szansa: 10.0        # Ta ryba: 10% (11x łatwiej!)
```

**2. Ultra Rzadka Pospolita Ryba**
```yaml
ukryta_pospolita:
    rzadkosc: "POSPOLITA"      # Normalnie: 50%
    custom_szansa: 0.01        # Ta ryba: 0.01% (rzadsza niż mityczna!)
```

**3. Event-owa Ryba**
```yaml
# Podczas eventu ustaw wysoką szansę
swiateczna_ryba:
    rzadkosc: "MITYCZNA"
    custom_szansa: 15.0        # 15% podczas eventu!
    # Po evencie zmień na 0.1
```

### Ważne Informacje

- `custom_szansa` **całkowicie nadpisuje** szansę z rzadkości
- Jeśli nie ustawisz `custom_szansa`, ryba używa szansy z `RZADKOŚĆ`
- Custom szansa **NIE zmienia** mnożnika ceny (nadal z rzadkości)

---

## 🪱 Przynęty i Ich Bonusy (baits.yml)

Przynęty modyfikują szanse w **3 sposoby**:

### 1. Ogólny Bonus (`szansa_bonus`)

```yaml
robak:
  szansa_bonus: 1.2   # +20% do WSZYSTKICH ryb
```

**Jak działa:**
- Bazowa szansa Mityczna: 0.1%
- Z robakiem: 0.1% × 1.2 = **0.12%**

### 2. Bonus dla Rzadkości (`bonus_rzadkosci`)

```yaml
zlota_przyneta:
  szansa_bonus: 3.0   # x3 ogólnie
  bonus_rzadkosci:
    LEGENDARNA: 5.0   # x5 dla legendarnych!
    MITYCZNA: 10.0    # x10 dla mitycznych!
```

**Obliczenia:**
- Pospolita: 50% × 3.0 = **150% wagi** (nie szansy!)
- Legendarna: 0.9% × 3.0 × 5.0 = **13.5% wagi**
- Mityczna: 0.1% × 3.0 × 10.0 = **3.0% wagi**

*Końcowa szansa zależy od sumy wszystkich wag*

### 3. Preferencje Ryb (`preferencje_ryb` + `bonus_dla_preferencji`)

```yaml
przyneta_na_karpia:
  szansa_bonus: 1.5
  preferencje_ryb:
    - "zloty_karp"
    - "karp_lustrzany"
  bonus_dla_preferencji: 3.0   # x3 dla preferowanych!
```

**Jak działa:**
- Złoty Karp (preferowany): bazowa × 1.5 × **3.0** = **4.5x**
- Inna ryba: bazowa × 1.5 = **1.5x**

### Pełny Przykład Przynęty

```yaml
mega_przyneta:
  szansa_bonus: 2.0              # x2 dla wszystkich
  bonus_rzadkosci:
    EPICKI: 3.0                  # x3 dla epickich
    LEGENDARNA: 5.0              # x5 dla legendarnych
    MITYCZNA: 10.0               # x10 dla mitycznych
  preferencje_ryb:
    - "lewiatian"                # Preferuje Lewiatiana
  bonus_dla_preferencji: 4.0     # x4 dla Lewiatiana

# WYNIK DLA LEWIATIANA (MITYCZNA):
# Waga = 0.1 × 2.0 × 10.0 × 4.0 = 8.0 (80x więcej!)
```

---

## 🎣 Wędki i Szczęście (rods.yml)

### Podstawowy Modyfikator Szczęścia

```yaml
wedki:
  diamentowa:
    szczescie: 1.7   # +70% bonusu

  neptuna:
    szczescie: 5.0   # +400% bonusu!
```

### Algorytm Bonusu (config.yml)

```yaml
algorytm_szczescia:
  typ: "linear"                      # linear, exponential, logarithmic
  multiplier: 1.0                    # Siła efektu
  wiekszy_bonus_dla_rzadkich: true   # Rzadsze = więcej bonusu
```

### Jak Działa Szczęście Wędki?

**Formuła (linear + wiekszy_bonus_dla_rzadkich=true):**
```
bonus = (6 - pozycja_rzadkości) × (szczęście - 1.0) × multiplier
```

**Pozycje rzadkości:**
- POSPOLITA = 0
- NIEPOSPOLITA = 1
- RZADKA = 2
- EPICKI = 3
- LEGENDARNA = 4
- MITYCZNA = 5

**Przykład: Wędka Neptuna (szczęście = 5.0)**

| Rzadkość | Pozycja | Bazowa Waga | Bonus | Nowa Waga |
|----------|---------|-------------|-------|-----------|
| Pospolita | 0 | 50.0 | (6-0) × 4.0 = **24** | 74.0 |
| Mityczna | 5 | 0.1 | (6-5) × 4.0 = **4** | 4.1 |

*Mityczna dostaje proporcjonalnie ogromny boost!*

---

## 🔄 Jak to Wszystko Działa Razem

### Kompletny Przykład Łowienia

**Sytuacja:**
- Gracz ma **Wędkę Neptuna** (szczęście = 5.0)
- Nałożona **Złota Przynęta** (bonus ogólny = 3.0, bonus MITYCZNA = 10.0)
- Łowi **Lewiatiana** (MITYCZNA, bazowa szansa = 0.1%)

**Krok 1: Bazowa Szansa**
- Lewiatian (MITYCZNA): **0.1 wagi**

**Krok 2: Bonus od Wędki**
- Bonus = (6 - 5) × (5.0 - 1.0) = **4.0**
- Nowa waga = 0.1 + 4.0 = **4.1 wagi**

**Krok 3: Bonus od Przynęty**
- Ogólny: 4.1 × 3.0 = **12.3**
- Rzadkość (MITYCZNA): 12.3 × 10.0 = **123 wagi**

**Krok 4: Obliczenie Szansy**
- Suma wszystkich wag (przykładowo): 300
- Szansa na Lewiatiana: 123 / 300 = **41%**

**WYNIK:** Z 0.1% do 41% - **410x wzrost szansy!**

---

## 📊 Przykłady i Obliczenia

### Przykład 1: "Chcę więcej legendarnych ryb"

**config.yml:**
```yaml
szanse_rzadkosci:
  wlaczone: true
  POSPOLITA: 40.0      # Zmniejsz z 50
  LEGENDARNA: 5.0      # Zwiększ z 0.9
  MITYCZNA: 0.5        # Zwiększ z 0.1
```

### Przykład 2: "Event z 50% szansą na Lewiatiana"

**fish.yml:**
```yaml
lewiatian:
  custom_szansa: 50.0  # Zmień z 0.1 na 50!
```

### Przykład 3: "Przynęta tylko na Mityczne"

**baits.yml:**
```yaml
przyneta_na_mityczne:
  szansa_bonus: 1.0      # Brak ogólnego bonusu
  bonus_rzadkosci:
    MITYCZNA: 100.0      # x100 tylko dla mitycznych!
```

### Przykład 4: "Super łatwy serwer"

**config.yml:**
```yaml
szanse_rzadkosci:
  POSPOLITA: 20.0
  NIEPOSPOLITA: 20.0
  RZADKA: 20.0
  EPICKI: 20.0
  LEGENDARNA: 10.0
  MITYCZNA: 10.0
  # Wszystkie rzadkości mają podobne szanse!
```

---

## ❓ Najczęstsze Pytania

### Q: Czy custom_szansa zastępuje szansę z rzadkości?
**A:** Tak, całkowicie. Jeśli ustawisz `custom_szansa: 10.0` dla MITYCZNEJ ryby, będzie miała 10 wagi zamiast 0.1.

### Q: Czy mogę mieć różne szanse dla ryb tej samej rzadkości?
**A:** Tak! Użyj `custom_szansa` dla każdej ryby osobno.

### Q: Jak działa szansa_bonus = 2.0 w przynętach?
**A:** Mnożnik. Jeśli ryba ma 10 wagi, z przynętą będzie miała 20 wagi.

### Q: Czy bonusy się dodają czy mnożą?
**A:** **MNOŻĄ**. Wędka + Przynęta = efekt pomnożony!

### Q: Jak obliczyć rzeczywistą szansę w %?
**A:** Wzór: `(waga_ryby / suma_wszystkich_wag) × 100%`

### Q: Czy mogę wyłączyć pospolite ryby?
**A:** Tak, ustaw `POSPOLITA: 0.0` w config.yml.

### Q: Jak zrobić żeby wszystkie ryby miały taką samą szansę?
**A:** Ustaw wszystkie rzadkości na tę samą wagę (np. wszystkie = 10.0).

### Q: Przynęty działają automatycznie?
**A:** Tak, jeśli są nałożone na wędkę (system implementowany w plugin-1-21).

### Q: Jak przetestować szanse?
**A:** Włącz `debug: true` w config.yml - plugin będzie logował obliczenia.

### Q: Czy zmiana configu wymaga restartu?
**A:** Nie! Użyj `/sf reload` aby przeładować config bez restartu.

---

## 🎯 Przykładowy Config dla Różnych Serwerów

### 🏆 Serwer Hardcore (rzadkie ryby są BARDZO rzadkie)

```yaml
# config.yml
szanse_rzadkosci:
  POSPOLITA: 70.0
  NIEPOSPOLITA: 25.0
  RZADKA: 4.0
  EPICKI: 0.8
  LEGENDARNA: 0.15
  MITYCZNA: 0.05
```

### 🎮 Serwer Casual (łatwe rzadkie)

```yaml
# config.yml
szanse_rzadkosci:
  POSPOLITA: 35.0
  NIEPOSPOLITA: 30.0
  RZADKA: 20.0
  EPICKI: 10.0
  LEGENDARNA: 4.0
  MITYCZNA: 1.0
```

### 🎪 Serwer Event (wszystko możliwe!)

```yaml
# config.yml
szanse_rzadkosci:
  POSPOLITA: 16.6
  NIEPOSPOLITA: 16.7
  RZADKA: 16.7
  EPICKI: 16.7
  LEGENDARNA: 16.7
  MITYCZNA: 16.6
```

---

**SimpleFishing** - Pełna kontrola nad szansami łowienia! 🎣
