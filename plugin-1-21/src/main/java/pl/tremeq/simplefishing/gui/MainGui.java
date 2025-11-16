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
            ustawItem(13, contests);
        }

        // Sklep Wędek
        if (player.hasPermission("simplefishing.shop")) {
            ItemStack rodShop = stworzItem(
                Material.FISHING_ROD,
                "&e&lSklep Wędek",
                Arrays.asList(
                    "&7Kup nową wędkę!",
                    "&7Początkowa cena: &610,000 $",
                    "",
                    "&eKliknij aby otworzyć!"
                )
            );
            ustawItem(14, rodShop);
        }

        // Ulepsz Wędkę
        if (player.hasPermission("simplefishing.rod.upgrade")) {
            ItemStack rodUpgrade = stworzItem(
                Material.ANVIL,
                "&5&lUlepsz Wędkę",
                Arrays.asList(
                    "&7Ulepsz swoją wędkę",
                    "&7na wyższy tier!",
                    "&c&lWYMAGA DUŻYCH ZASOBÓW",
                    "",
                    "&eKliknij aby otworzyć!"
                )
            );
            ustawItem(15, rodUpgrade);
        }

        // Zarządzaj Przynętami
        if (player.hasPermission("simplefishing.rod.bait")) {
            ItemStack baits = stworzItem(
                Material.TROPICAL_FISH_BUCKET,
                "&d&lPrzynęty",
                Arrays.asList(
                    "&7Nakładaj przynęty",
                    "&7na swoją wędkę!",
                    "",
                    "&eKliknij aby otworzyć!"
                )
            );
            ustawItem(16, baits);
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
                player.closeInventory();
                FishCollectionGui fishCollectionGui = new FishCollectionGui(player, plugin);
                fishCollectionGui.inicjalizuj();
                plugin.getGuiManager().otworzGui(player, fishCollectionGui);
                break;

            case 12: // Sklep Ryb
                if (player.hasPermission("simplefishing.shop")) {
                    player.closeInventory();
                    ShopGui shopGui = new ShopGui(player, plugin);
                    shopGui.inicjalizuj();
                    plugin.getGuiManager().otworzGui(player, shopGui);
                }
                break;

            case 13: // Konkursy
                if (player.hasPermission("simplefishing.contest")) {
                    player.closeInventory();
                    ContestGui contestGui = new ContestGui(player, plugin);
                    contestGui.inicjalizuj();
                    plugin.getGuiManager().otworzGui(player, contestGui);
                }
                break;

            case 14: // Sklep Wędek
                if (player.hasPermission("simplefishing.shop")) {
                    player.closeInventory();
                    RodShopGui rodShopGui = new RodShopGui(plugin, player);
                    plugin.getGuiManager().otworzGui(player, rodShopGui);
                }
                break;

            case 15: // Ulepsz Wędkę
                if (player.hasPermission("simplefishing.rod.upgrade")) {
                    player.closeInventory();
                    RodUpgradeGui upgradeGui = new RodUpgradeGui(plugin, player);
                    plugin.getGuiManager().otworzGui(player, upgradeGui);
                }
                break;

            case 16: // Przynęty (zarządzaj)
                if (player.hasPermission("simplefishing.rod.bait")) {
                    player.closeInventory();
                    RodGui rodGui = new RodGui(player, plugin);
                    rodGui.inicjalizuj();
                    plugin.getGuiManager().otworzGui(player, rodGui);
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
