package pl.tremeq.simplefishing.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;

import java.util.Arrays;

/**
 * Główne GUI pluginu SimpleFishing
 * Wyświetla opcje: przeglądanie ryb, sklep, konkursy, wędki
 *
 * @author tremeq
 * @version 1.0.0
 */
public class MainGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;

    public MainGui(Player player, SimpleFishingPlugin plugin) {
        super(player, "§6§lSimpleFishing - Menu Główne", 27);
        this.plugin = plugin;
    }

    @Override
    public void inicjalizuj() {
        // Wypełnij tło
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        wypelnij(filler);

        // Kolekcja ryb
        ItemStack fishCollection = stworzItem(
            Material.TROPICAL_FISH,
            "&6&lKolekcja Ryb",
            Arrays.asList(
                "&7Przeglądaj wszystkie złowione ryby",
                "&7i ich statystyki",
                "",
                "&eKliknij aby otworzyć!"
            )
        );
        ustawItem(10, fishCollection);

        // Sklep
        if (player.hasPermission("simplefishing.shop")) {
            ItemStack shop = stworzItem(
                Material.EMERALD,
                "&a&lSklep Ryb",
                Arrays.asList(
                    "&7Sprzedaj swoje ryby",
                    "&7za monety!",
                    "",
                    "&eKliknij aby otworzyć!"
                )
            );
            ustawItem(12, shop);
        }

        // Konkursy
        if (player.hasPermission("simplefishing.contest")) {
            ItemStack contests = stworzItem(
                Material.DIAMOND,
                "&b&lKonkursy",
                Arrays.asList(
                    "&7Zobacz aktywne konkursy",
                    "&7i swoje miejsce w rankingu",
                    "",
                    "&eKliknij aby otworzyć!"
                )
            );
            ustawItem(14, contests);
        }

        // Wędki i ulepszenia
        if (player.hasPermission("simplefishing.rod.upgrade")) {
            ItemStack rods = stworzItem(
                Material.FISHING_ROD,
                "&d&lWędki",
                Arrays.asList(
                    "&7Zarządzaj swoimi wędkami",
                    "&7i nakładaj przynęty",
                    "",
                    "&eKliknij aby otworzyć!"
                )
            );
            ustawItem(16, rods);
        }

        // Informacje
        ItemStack info = stworzItem(
            Material.BOOK,
            "&e&lInformacje",
            Arrays.asList(
                "&7SimpleFishing v1.0.0",
                "&7Autor: &atremeq",
                "",
                "&7Zarejestrowanych ryb: &a" + plugin.getFishRegistry().getLiczbaRyb(),
                "&7Zarejestrowanych przynęt: &a" + plugin.getBaitRegistry().getLiczbaBaitow()
            )
        );
        ustawItem(22, info);

        // Zamknij
        ItemStack close = stworzItem(Material.BARRIER, "&c&lZamknij", null);
        ustawItem(26, close);
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();

        switch (slot) {
            case 10: // Kolekcja ryb
                player.sendMessage(koloruj("&aOtwieranie kolekcji ryb..."));
                // Tutaj otwórz GUI kolekcji
                break;

            case 12: // Sklep
                if (player.hasPermission("simplefishing.shop")) {
                    player.sendMessage(koloruj("&aOtwieranie sklepu..."));
                    // Tutaj otwórz GUI sklepu
                }
                break;

            case 14: // Konkursy
                if (player.hasPermission("simplefishing.contest")) {
                    player.sendMessage(koloruj("&aOtwieranie konkursów..."));
                    // Tutaj otwórz GUI konkursów
                }
                break;

            case 16: // Wędki
                if (player.hasPermission("simplefishing.rod.upgrade")) {
                    player.sendMessage(koloruj("&aOtwieranie menu wędek..."));
                    // Tutaj otwórz GUI wędek
                }
                break;

            case 26: // Zamknij
                player.closeInventory();
                break;
        }
    }

    /**
     * Tworzy item z nazwą i lore
     */
    private ItemStack stworzItem(Material material, String nazwa, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(koloruj(nazwa));

            if (lore != null) {
                meta.setLore(lore.stream()
                    .map(this::koloruj)
                    .collect(java.util.stream.Collectors.toList()));
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Koloruje tekst
     */
    private String koloruj(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
