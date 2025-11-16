package pl.tremeq.simplefishing.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.fish.Fish;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;
import pl.tremeq.simplefishing.api.player.PlayerFishData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * GUI kolekcji ryb gracza
 * Pokazuje odblokowane ryby ze statystykami
 * Ryby nie odblokowane pokazane jako szary barwnik
 *
 * @author tremeq
 * @version 1.0.0
 */
public class FishCollectionGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;
    private int currentPage;
    private List<Fish> allFish;

    private static final int FISH_SLOTS_PER_PAGE = 36; // 4 rzędy × 9 slotów
    private static final int GUI_SIZE = 54; // 6 rzędów

    public FishCollectionGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.kolekcja", "&6&lKolekcja Ryb"), GUI_SIZE);
        this.plugin = plugin;
        this.currentPage = 0;

        // VALIDATION: Check if fish registry is initialized and has fish
        if (plugin.getFishRegistry() == null) {
            plugin.getLogger().severe("FishRegistry is null! Cannot create Fish Collection GUI.");
            player.sendMessage("§cBłąd: System ryb nie został zainicjalizowany! Skontaktuj się z administratorem.");
            this.allFish = new ArrayList<>();
            return;
        }

        var fishCollection = plugin.getFishRegistry().getAllRyby();
        if (fishCollection == null || fishCollection.isEmpty()) {
            plugin.getLogger().warning("Fish registry is empty! No fish registered for Fish Collection GUI.");
            player.sendMessage("§eBrak zarejestrowanych ryb w kolekcji.");
            this.allFish = new ArrayList<>();
            return;
        }

        // Pobierz wszystkie ryby i posortuj po rzadkości
        this.allFish = new ArrayList<>(fishCollection);

        // Safe sort with null rzadkosc handling
        this.allFish.sort(Comparator.comparingInt(f -> {
            if (f == null) {
                plugin.getLogger().warning("Null fish found in registry during sort!");
                return 999; // Sort to end
            }
            if (f.getRzadkosc() == null) {
                plugin.getLogger().warning("Fish " + f.getId() + " has null rzadkosc!");
                return 999; // Sort to end
            }
            return f.getRzadkosc().ordinal();
        }));
    }

    @Override
    public void inicjalizuj() {
        // Nagłówek (rząd 1)
        wypelnijNaglowek();

        // Ryby (rzędy 2-5)
        wypelnijRyby();

        // Nawigacja (rząd 6)
        wypelnijNawigacje();
    }

    /**
     * Wypełnia nagłówek GUI
     */
    private void wypelnijNaglowek() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }

        // Wypełnij pierwszy rząd
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler);
        }

        // Środkowy item - informacje
        ItemStack info = new ItemStack(Material.BOOK);
        meta = info.getItemMeta();
        if (meta != null) {
            PlayerFishData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

            meta.setDisplayName(koloruj("&e&lKolekcja Ryb"));
            List<String> lore = new ArrayList<>();
            lore.add(koloruj("&7Gracz: &f" + player.getName()));
            lore.add("");
            lore.add(koloruj("&7Odblokowane ryby: &a" + playerData.getUniqueFishCaught() + "&7/&e" + allFish.size()));
            lore.add(koloruj("&7Całkowite złowienia: &a" + playerData.getTotalFishCaught()));
            lore.add(koloruj("&7Suma długości: &a" + String.format("%.2f", playerData.getTotalLengthCaught()) + " cm"));
            meta.setLore(lore);
            info.setItemMeta(meta);
        }
        inventory.setItem(4, info);
    }

    /**
     * Wypełnia ryby w GUI
     */
    private void wypelnijRyby() {
        // VALIDATION: Check if allFish is valid
        if (allFish == null || allFish.isEmpty()) {
            // Fill all fish slots with AIR
            for (int slot = 9; slot < 45; slot++) {
                inventory.setItem(slot, new ItemStack(Material.AIR));
            }
            return;
        }

        PlayerFishData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            plugin.getLogger().warning("PlayerData is null for player " + player.getName() + " in Fish Collection GUI!");
            return;
        }

        int startIndex = currentPage * FISH_SLOTS_PER_PAGE;
        int endIndex = Math.min(startIndex + FISH_SLOTS_PER_PAGE, allFish.size());

        int slot = 9; // Zaczynamy od drugiego rzędu

        for (int i = startIndex; i < endIndex; i++) {
            Fish fish = allFish.get(i);

            // Skip null fish (should not happen but be safe)
            if (fish == null) {
                plugin.getLogger().warning("Null fish at index " + i + " in allFish list!");
                continue;
            }

            ItemStack fishItem;

            if (playerData.hasCaughtFish(fish.getId())) {
                // ODBLOKOWANA RYBA - pokaż statystyki
                fishItem = stworzOdblokowanąRybę(fish, playerData);
            } else {
                // ZABLOKOWANA RYBA - szary barwnik
                fishItem = stworzZablokowanąRybę(fish);
            }

            inventory.setItem(slot, fishItem);
            slot++;
        }

        // Wypełnij pozostałe sloty pustymi itemami
        ItemStack filler = new ItemStack(Material.AIR);
        while (slot < 45) { // Do 5 rzędu
            inventory.setItem(slot, filler);
            slot++;
        }
    }

    /**
     * Tworzy item odblokowanej ryby ze statystykami
     */
    private ItemStack stworzOdblokowanąRybę(Fish fish, PlayerFishData playerData) {
        ItemStack item = new ItemStack(fish.getItemStack().getType());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Nazwa z kolorem rzadkości
            meta.setDisplayName(koloruj(fish.getRzadkosc().getKolor() + "&l" + fish.getDisplayName()));

            // Lore ze statystykami
            List<String> lore = new ArrayList<>();
            lore.add(koloruj("&7Rzadkość: " + fish.getRzadkosc().getKolorowaNazwa()));
            lore.add("");

            PlayerFishData.FishStatistics stats = playerData.getFishStatistics(fish.getId());
            if (stats != null) {
                lore.add(koloruj("&a&l✓ ODBLOKOWANA"));
                lore.add("");
                lore.add(koloruj("&e&lStatystyki:"));
                lore.add(koloruj("&7▪ Złowiono razy: &f" + stats.getTimesCaught()));
                lore.add(koloruj("&7▪ Największa: &f" + String.format("%.2f", stats.getLargestCaught()) + " cm"));
                lore.add(koloruj("&7▪ Średnia długość: &f" + String.format("%.2f", stats.getAverageLength()) + " cm"));
                lore.add(koloruj("&7▪ Suma długości: &f" + String.format("%.2f", stats.getTotalLength()) + " cm"));
            }

            lore.add("");
            lore.add(koloruj("&7Zakres długości: &f" + fish.getMinDlugosc() + " - " + fish.getMaxDlugosc() + " cm"));
            lore.add(koloruj("&7Bazowa cena: &e" + fish.getBazowaCena() + " monet"));

            meta.setLore(lore);

            // Custom Model Data
            if (fish.getCustomModelData() > 0) {
                meta.setCustomModelData(fish.getCustomModelData());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Tworzy item zablokowanej ryby (szary barwnik)
     */
    private ItemStack stworzZablokowanąRybę(Fish fish) {
        ItemStack item = new ItemStack(Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Nazwa zasłonięta
            meta.setDisplayName(koloruj("&8&l???"));

            List<String> lore = new ArrayList<>();
            lore.add(koloruj("&7Rzadkość: " + fish.getRzadkosc().getKolorowaNazwa()));
            lore.add("");
            lore.add(koloruj("&c&l✗ NIE ODBLOKOWANO"));
            lore.add("");
            lore.add(koloruj("&7Złów tę rybę aby odblokować!"));
            lore.add(koloruj("&7Zakres długości: &f" + fish.getMinDlugosc() + " - " + fish.getMaxDlugosc() + " cm"));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Wypełnia nawigację (ostatni rząd)
     */
    private void wypelnijNawigacje() {
        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }

        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, filler);
        }

        // Poprzednia strona (slot 45)
        if (currentPage > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            meta = prevPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(koloruj("&e◀ Poprzednia strona"));
                List<String> lore = new ArrayList<>();
                lore.add(koloruj("&7Strona: &f" + currentPage + "&7/&f" + getMaxPage()));
                meta.setLore(lore);
                prevPage.setItemMeta(meta);
            }
            inventory.setItem(45, prevPage);
        }

        // Powrót do menu głównego (slot 49)
        ItemStack home = new ItemStack(Material.DARK_OAK_DOOR);
        meta = home.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(koloruj("&c&lPowrót"));
            List<String> lore = new ArrayList<>();
            lore.add(koloruj("&7Wróć do menu głównego"));
            meta.setLore(lore);
            home.setItemMeta(meta);
        }
        inventory.setItem(49, home);

        // Następna strona (slot 53)
        if (currentPage < getMaxPage()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            meta = nextPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(koloruj("&eNastępna strona ▶"));
                List<String> lore = new ArrayList<>();
                lore.add(koloruj("&7Strona: &f" + (currentPage + 2) + "&7/&f" + (getMaxPage() + 1)));
                meta.setLore(lore);
                nextPage.setItemMeta(meta);
            }
            inventory.setItem(53, nextPage);
        }
    }

    /**
     * Oblicza maksymalną stronę
     */
    private int getMaxPage() {
        // VALIDATION: Handle empty allFish
        if (allFish == null || allFish.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil((double) allFish.size() / FISH_SLOTS_PER_PAGE) - 1;
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getSlot();

        // Poprzednia strona
        if (slot == 45 && currentPage > 0) {
            currentPage--;
            odswiez();
            return;
        }

        // Następna strona
        if (slot == 53 && currentPage < getMaxPage()) {
            currentPage++;
            odswiez();
            return;
        }

        // Powrót do menu głównego
        if (slot == 49) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
            return;
        }

        // Kliknięcie w rybę - nic nie rób (tylko podgląd)
    }

    /**
     * Koloruje tekst
     */
    private String koloruj(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
