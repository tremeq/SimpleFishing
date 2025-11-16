package pl.tremeq.simplefishing.api.rod;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.tremeq.simplefishing.api.bait.Bait;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Menedżer wędek
 * Zarządza customowymi wędkami i ich ulepszeniami
 *
 * @author tremeq
 * @version 1.0.0
 */
public class RodManager {

    private final Map<String, FishingRod> wedki;

    public RodManager() {
        this.wedki = new ConcurrentHashMap<>();
    }

    /**
     * Rejestruje nową wędkę
     * @param rod Wędka do zarejestrowania
     */
    public void zarejestrujWedke(FishingRod rod) {
        wedki.put(rod.getId(), rod);
    }

    /**
     * Wyrejestrowuje wędkę
     * @param id ID wędki
     */
    public void wyrejestrujWedke(String id) {
        wedki.remove(id);
    }

    /**
     * Pobiera wędkę po ID
     * @param id ID wędki
     * @return Optional z wędką lub pusty
     */
    public Optional<FishingRod> getWedka(String id) {
        return Optional.ofNullable(wedki.get(id));
    }

    /**
     * Pobiera wszystkie zarejestrowane wędki
     * @return Kolekcja wszystkich wędek
     */
    public Collection<FishingRod> getAllWedki() {
        return Collections.unmodifiableCollection(wedki.values());
    }

    /**
     * Sprawdza czy ItemStack jest customową wędką
     * @param item ItemStack do sprawdzenia
     * @return true jeśli jest customową wędką
     */
    public boolean czyCustomowaWedka(ItemStack item) {
        if (item == null) return false;
        // Sprawdzenie NBT - będzie implementowane w module 1.21
        return false;
    }

    /**
     * Pobiera ID customowej wędki z ItemStack
     * @param item ItemStack wędki
     * @return Optional z ID wędki lub pusty
     */
    public Optional<String> getWedkaId(ItemStack item) {
        // Odczyt NBT - będzie implementowane w module 1.21
        return Optional.empty();
    }

    /**
     * Nakłada przynętę na wędkę
     * @param rodItem ItemStack wędki
     * @param bait Przynęta do nałożenia
     * @return true jeśli udało się nałożyć
     */
    public boolean nalozPrzynete(ItemStack rodItem, Bait bait) {
        // Implementacja NBT - będzie w module 1.21
        return false;
    }

    /**
     * Pobiera aktywne przynęty na wędce
     * @param rodItem ItemStack wędki
     * @return Lista aktywnych przynęt
     */
    public List<String> getAktywnePrzynety(ItemStack rodItem) {
        // Odczyt NBT - będzie w module 1.21
        return new ArrayList<>();
    }

    /**
     * Usuwa przynętę z wędki
     * @param rodItem ItemStack wędki
     * @param baitId ID przynęty do usunięcia
     * @return true jeśli udało się usunąć
     */
    public boolean usunPrzynete(ItemStack rodItem, String baitId) {
        // Implementacja NBT - będzie w module 1.21
        return false;
    }

    /**
     * Oblicza całkowite szczęście wędki z wszystkimi modyfikatorami
     * @param rodItem ItemStack wędki
     * @return Wartość szczęścia
     */
    public double obliczSzczescie(ItemStack rodItem) {
        Optional<String> rodId = getWedkaId(rodItem);
        if (rodId.isEmpty()) return 1.0;

        Optional<FishingRod> rod = getWedka(rodId.get());
        if (rod.isEmpty()) return 1.0;

        double luck = rod.get().getPodstawowySzczescie();

        // Dodaj bonusy z ulepszeń
        for (RodEnchantment ench : rod.get().getUlepszenia()) {
            if (ench.getTyp() == RodEnchantment.RodEnchantmentType.LUCK_BOOST) {
                luck += ench.getWartosc();
            }
        }

        return luck;
    }
}
