package pl.tremeq.simplefishing.api.item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rejestr wszystkich przedmiotów możliwych do wyłowienia
 * Umożliwia rejestrację, pobieranie i losowanie przedmiotów
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ItemRegistry {

    private final Map<String, Item> przedmioty;
    private final Random random;

    public ItemRegistry() {
        this.przedmioty = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    /**
     * Rejestruje nowy przedmiot
     * @param item Przedmiot do zarejestrowania
     */
    public void zarejestrujPrzedmiot(Item item) {
        przedmioty.put(item.getId(), item);
    }

    /**
     * Wyrejestrowuje przedmiot
     * @param id ID przedmiotu do wyrejestrowania
     */
    public void wyrejestrujPrzedmiot(String id) {
        przedmioty.remove(id);
    }

    /**
     * Pobiera przedmiot po ID
     * @param id ID przedmiotu
     * @return Optional z przedmiotem lub pusty
     */
    public Optional<Item> getPrzedmiot(String id) {
        return Optional.ofNullable(przedmioty.get(id));
    }

    /**
     * Pobiera wszystkie zarejestrowane przedmioty
     * @return Kolekcja wszystkich przedmiotów
     */
    public Collection<Item> getAllPrzedmioty() {
        return Collections.unmodifiableCollection(przedmioty.values());
    }

    /**
     * Pobiera przedmioty według rzadkości
     * @param rarity Rzadkość
     * @return Lista przedmiotów o danej rzadkości
     */
    public List<Item> getPrzedmiotyByRzadkosc(ItemRarity rarity) {
        return przedmioty.values().stream()
                .filter(item -> item.getRzadkosc() == rarity)
                .toList();
    }

    /**
     * Losuje przedmiot na podstawie rzadkości
     * @return Wylosowany przedmiot
     */
    public Item wylosujPrzedmiot() {
        return wylosujPrzedmiot(1.0);
    }

    /**
     * Losuje przedmiot z modyfikatorem szczęścia
     * @param luckModifier Modyfikator szczęścia (1.0 = normalnie, >1.0 = większa szansa na rzadkie)
     * @return Wylosowany przedmiot
     */
    public Item wylosujPrzedmiot(double luckModifier) {
        if (przedmioty.isEmpty()) {
            return null;
        }

        // Oblicz całkowitą wagę z modyfikatorem
        double totalWeight = 0;
        List<Item> dostepnePrzedmioty = new ArrayList<>(przedmioty.values());

        for (Item item : dostepnePrzedmioty) {
            double weight = item.getSzansa();

            // Modyfikator zwiększa szansę na rzadsze przedmioty
            if (luckModifier > 1.0) {
                // 6 rzadkości: 0=SMIECI, 5=MITYCZNE
                double rarityBonus = (6 - item.getRzadkosc().ordinal()) * (luckModifier - 1.0);
                weight += rarityBonus;
            }
            totalWeight += weight;
        }

        // Losuj
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (Item item : dostepnePrzedmioty) {
            double weight = item.getSzansa();

            if (luckModifier > 1.0) {
                double rarityBonus = (6 - item.getRzadkosc().ordinal()) * (luckModifier - 1.0);
                weight += rarityBonus;
            }
            currentWeight += weight;

            if (randomValue <= currentWeight) {
                return item;
            }
        }

        // Fallback - zwróć pierwszy przedmiot
        return dostepnePrzedmioty.get(0);
    }

    /**
     * Sprawdza czy przedmiot o danym ID jest zarejestrowany
     * @param id ID przedmiotu
     * @return true jeśli przedmiot jest zarejestrowany
     */
    public boolean czyZarejestrowany(String id) {
        return przedmioty.containsKey(id);
    }

    /**
     * Pobiera liczbę zarejestrowanych przedmiotów
     * @return Liczba przedmiotów
     */
    public int getLiczbaPrzedmiotow() {
        return przedmioty.size();
    }

    /**
     * Czyści wszystkie przedmioty z rejestru
     */
    public void wyczyscPrzedmioty() {
        przedmioty.clear();
    }
}
