package pl.tremeq.simplefishing.api.fish;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rejestr wszystkich customowych ryb
 * Umożliwia rejestrację, pobieranie i losowanie ryb
 *
 * @author tremeq
 * @version 1.0.0
 */
public class FishRegistry {

    private final Map<String, Fish> ryby;
    private final Random random;

    public FishRegistry() {
        this.ryby = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    /**
     * Rejestruje nową rybę
     * @param fish Ryba do zarejestrowania
     */
    public void zarejestrujRybe(Fish fish) {
        ryby.put(fish.getId(), fish);
    }

    /**
     * Wyrejestrowuje rybę
     * @param id ID ryby do wyrejestrowania
     */
    public void wyrejestrujRybe(String id) {
        ryby.remove(id);
    }

    /**
     * Pobiera rybę po ID
     * @param id ID ryby
     * @return Optional z rybą lub pusty
     */
    public Optional<Fish> getRyba(String id) {
        return Optional.ofNullable(ryby.get(id));
    }

    /**
     * Pobiera wszystkie zarejestrowane ryby
     * @return Kolekcja wszystkich ryb
     */
    public Collection<Fish> getAllRyby() {
        return Collections.unmodifiableCollection(ryby.values());
    }

    /**
     * Pobiera ryby według rzadkości
     * @param rarity Rzadkość
     * @return Lista ryb o danej rzadkości
     */
    public List<Fish> getRybyByRzadkosc(FishRarity rarity) {
        return ryby.values().stream()
                .filter(fish -> fish.getRzadkosc() == rarity)
                .toList();
    }

    /**
     * Losuje rybę na podstawie rzadkości
     * @return Wylosowana ryba
     */
    public Fish wylosujRybe() {
        return wylosujRybe(1.0);
    }

    /**
     * Losuje rybę z modyfikatorem szczęścia
     * @param luckModifier Modyfikator szczęścia (1.0 = normalnie, >1.0 = większa szansa na rzadkie)
     * @return Wylosowana ryba
     */
    public Fish wylosujRybe(double luckModifier) {
        if (ryby.isEmpty()) {
            return null;
        }

        // VALIDATION: Sanitize luck modifier
        if (Double.isNaN(luckModifier) || luckModifier < 0) {
            luckModifier = 1.0;
        }

        // Oblicz całkowitą wagę z modyfikatorem
        double totalWeight = 0;
        List<Fish> dostepneRyby = new ArrayList<>(ryby.values());

        for (Fish fish : dostepneRyby) {
            // VALIDATION: Skip null fish
            if (fish == null) {
                continue;
            }

            // Użyj fish.getSzansa() która może być customowa lub z rzadkości
            double weight = fish.getSzansa();

            // VALIDATION: Ensure weight is valid and positive
            if (Double.isNaN(weight) || weight < 0) {
                weight = 0.1; // Default minimal weight
            }

            // Modyfikator zwiększa szansę na rzadsze ryby
            if (luckModifier > 1.0 && fish.getRzadkosc() != null) {
                // 9 rzadkości: 0=BARDZO_POSPOLITA, 8=BOSKA
                double rarityBonus = (9 - fish.getRzadkosc().ordinal()) * (luckModifier - 1.0);
                weight += rarityBonus;
            }
            totalWeight += weight;
        }

        // VALIDATION: If totalWeight is 0 or invalid, return random fish
        if (totalWeight <= 0 || Double.isNaN(totalWeight) || Double.isInfinite(totalWeight)) {
            // Return random fish from list (fallback)
            return dostepneRyby.isEmpty() ? null : dostepneRyby.get(random.nextInt(dostepneRyby.size()));
        }

        // Losuj
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (Fish fish : dostepneRyby) {
            // VALIDATION: Skip null fish
            if (fish == null) {
                continue;
            }

            double weight = fish.getSzansa();

            // VALIDATION: Ensure weight is valid
            if (Double.isNaN(weight) || weight < 0) {
                weight = 0.1;
            }

            if (luckModifier > 1.0 && fish.getRzadkosc() != null) {
                double rarityBonus = (9 - fish.getRzadkosc().ordinal()) * (luckModifier - 1.0);
                weight += rarityBonus;
            }
            currentWeight += weight;

            if (randomValue <= currentWeight) {
                return fish;
            }
        }

        // Fallback - zwróć pierwszą niepustą rybę
        for (Fish fish : dostepneRyby) {
            if (fish != null) {
                return fish;
            }
        }
        return null;
    }

    /**
     * Sprawdza czy ryba o danym ID jest zarejestrowana
     * @param id ID ryby
     * @return true jeśli ryba jest zarejestrowana
     */
    public boolean czyZarejestrowana(String id) {
        return ryby.containsKey(id);
    }

    /**
     * Pobiera liczbę zarejestrowanych ryb
     * @return Liczba ryb
     */
    public int getLiczbaRyb() {
        return ryby.size();
    }

    /**
     * Czyści wszystkie ryby z rejestru
     */
    public void wyczyscRyby() {
        ryby.clear();
    }
}
