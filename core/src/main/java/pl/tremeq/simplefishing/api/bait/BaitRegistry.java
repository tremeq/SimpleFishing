package pl.tremeq.simplefishing.api.bait;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rejestr wszystkich przynęt (baitów)
 * Umożliwia rejestrację i pobieranie przynęt
 *
 * @author tremeq
 * @version 1.0.0
 */
public class BaitRegistry {

    private final Map<String, Bait> baity;

    public BaitRegistry() {
        this.baity = new ConcurrentHashMap<>();
    }

    /**
     * Rejestruje nową przynętę
     * @param bait Przynęta do zarejestrowania
     */
    public void zarejestrujBait(Bait bait) {
        baity.put(bait.getId(), bait);
    }

    /**
     * Wyrejestrowuje przynętę
     * @param id ID przynęty
     */
    public void wyrejestrujBait(String id) {
        baity.remove(id);
    }

    /**
     * Pobiera przynętę po ID
     * @param id ID przynęty
     * @return Optional z przynętą lub pusty
     */
    public Optional<Bait> getBait(String id) {
        return Optional.ofNullable(baity.get(id));
    }

    /**
     * Pobiera wszystkie zarejestrowane przynęty
     * @return Kolekcja wszystkich przynęt
     */
    public Collection<Bait> getAllBaity() {
        return Collections.unmodifiableCollection(baity.values());
    }

    /**
     * Sprawdza czy przynęta o danym ID jest zarejestrowana
     * @param id ID przynęty
     * @return true jeśli przynęta jest zarejestrowana
     */
    public boolean czyZarejestrowana(String id) {
        return baity.containsKey(id);
    }

    /**
     * Pobiera liczbę zarejestrowanych przynęt
     * @return Liczba przynęt
     */
    public int getLiczbaBaitow() {
        return baity.size();
    }

    /**
     * Czyści wszystkie przynęty z rejestru
     */
    public void wyczyscBaity() {
        baity.clear();
    }
}
