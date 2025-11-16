package pl.tremeq.simplefishing.api.rod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
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
    private final Plugin plugin;

    public RodManager(Plugin plugin) {
        this.wedki = new ConcurrentHashMap<>();
        this.plugin = plugin;
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
     * Aplikuje przynętę na wędkę (zapisuje do PDC i aktualizuje lore)
     * @param rodItem ItemStack wędki
     * @param bait Przynęta do aplikacji
     * @return true jeśli udało się aplikować
     */
    public boolean aplikujPrzynete(ItemStack rodItem, Bait bait) {
        if (rodItem == null || !rodItem.hasItemMeta()) return false;

        ItemMeta meta = rodItem.getItemMeta();
        if (meta == null) return false;

        // Klucze PDC
        NamespacedKey baitIdKey = new NamespacedKey(plugin, "bait_" + bait.getId() + "_id");
        NamespacedKey baitTimeKey = new NamespacedKey(plugin, "bait_" + bait.getId() + "_time");
        NamespacedKey baitDurationKey = new NamespacedKey(plugin, "bait_" + bait.getId() + "_duration");

        // Sprawdź czy przynęta już istnieje na wędce
        if (meta.getPersistentDataContainer().has(baitIdKey)) {
            return false; // Już ma tę przynętę
        }

        // Zapisz przynętę do PDC
        meta.getPersistentDataContainer().set(baitIdKey, PersistentDataType.STRING, bait.getId());
        meta.getPersistentDataContainer().set(baitTimeKey, PersistentDataType.LONG, System.currentTimeMillis());
        meta.getPersistentDataContainer().set(baitDurationKey, PersistentDataType.INTEGER, bait.getCzasTrwania());

        // Aktualizuj lore wędki
        aktualizujLoreWedki(meta);

        rodItem.setItemMeta(meta);
        return true;
    }

    /**
     * Aktualizuje lore wędki aby pokazać aktywne przynęty
     * @param meta ItemMeta wędki
     */
    private void aktualizujLoreWedki(ItemMeta meta) {
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        // Usuń stare informacje o przynętach (zaczynające się od "§d🎣")
        lore.removeIf(line -> line.startsWith(ChatColor.LIGHT_PURPLE + "🎣") ||
                              line.startsWith(ChatColor.LIGHT_PURPLE + "Przynęty:") ||
                              line.startsWith(ChatColor.GRAY + "  • "));

        // Dodaj separator jeśli lore nie jest puste
        if (!lore.isEmpty() && !lore.get(lore.size() - 1).isEmpty()) {
            lore.add("");
        }

        // Pobierz aktywne przynęty
        List<String> aktywnePrzynety = getAktywnePrzynety(meta);

        if (!aktywnePrzynety.isEmpty()) {
            lore.add(ChatColor.LIGHT_PURPLE + "🎣 Aktywne Przynęty:");
            lore.addAll(aktywnePrzynety);
        }

        meta.setLore(lore);
    }

    /**
     * Pobiera aktywne przynęty z meta (wewnętrzna metoda)
     * @param meta ItemMeta wędki
     * @return Lista opisów przynęt
     */
    private List<String> getAktywnePrzynety(ItemMeta meta) {
        List<String> baits = new ArrayList<>();

        if (meta == null) return baits;

        var container = meta.getPersistentDataContainer();
        long currentTime = System.currentTimeMillis();

        // Przejdź przez wszystkie klucze i znajdź przynęty
        for (NamespacedKey key : container.getKeys()) {
            if (!key.getKey().startsWith("bait_") || !key.getKey().endsWith("_id")) continue;

            String baitId = container.get(key, PersistentDataType.STRING);
            if (baitId == null) continue;

            // Pobierz czas aplikacji i czas trwania
            NamespacedKey timeKey = new NamespacedKey(plugin, "bait_" + baitId + "_time");
            NamespacedKey durationKey = new NamespacedKey(plugin, "bait_" + baitId + "_duration");

            Long applicationTime = container.get(timeKey, PersistentDataType.LONG);
            Integer duration = container.get(durationKey, PersistentDataType.INTEGER);

            if (applicationTime == null || duration == null) continue;

            // Oblicz pozostały czas
            long elapsedSeconds = (currentTime - applicationTime) / 1000;
            long remainingSeconds = duration - elapsedSeconds;

            if (remainingSeconds > 0) {
                // Przynęta nadal aktywna
                String baitName = baitId; // Można by pobrać nazwę z registry
                baits.add(ChatColor.GRAY + "  • " + ChatColor.AQUA + baitName +
                         ChatColor.GRAY + " (" + ChatColor.GREEN + remainingSeconds + "s" + ChatColor.GRAY + ")");
            } else {
                // Przynęta wygasła - można by ją usunąć, ale zostawiamy to do czyszczenia
            }
        }

        return baits;
    }

    /**
     * Pobiera aktywne przynęty na wędce
     * @param rodItem ItemStack wędki
     * @return Lista aktywnych przynęt
     */
    public List<String> getAktywnePrzynety(ItemStack rodItem) {
        if (rodItem == null || !rodItem.hasItemMeta()) {
            return new ArrayList<>();
        }

        ItemMeta meta = rodItem.getItemMeta();
        if (meta == null) {
            return new ArrayList<>();
        }

        return getAktywnePrzynety(meta);
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
