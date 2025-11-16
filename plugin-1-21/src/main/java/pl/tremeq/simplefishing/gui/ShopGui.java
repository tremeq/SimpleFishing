package pl.tremeq.simplefishing.gui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.fish.Fish;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI sklepu ryb - tradycyjny system
 * Wkładasz ryby do slotów, klikasz "Sprzedaj" i wszystko się sprzedaje
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ShopGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;
    private static final int GUI_SIZE = 54;

    // Sloty dla ryb do sprzedania (7x4 = 28 slotów)
    private static final int[] FISH_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    public ShopGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.sklep", "&a&lSklep Ryb"), GUI_SIZE);
        this.plugin = plugin;
    }

    @Override
    public void inicjalizuj() {
        // Wypełnij tło (ale nie sloty dla ryb!)
        wypelnijTlo();

        // Nagłówek
        wypelnijNaglowek();

        // Przyciski akcji
        wypelnijPrzyciski();
    }

    /**
     * Wypełnia tło (pomijając sloty dla ryb)
     */
    private void wypelnijTlo() {
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);

        for (int i = 0; i < GUI_SIZE; i++) {
            // Pomiń sloty dla ryb
            boolean isFishSlot = false;
            for (int slot : FISH_SLOTS) {
                if (i == slot) {
                    isFishSlot = true;
                    break;
                }
            }

            if (!isFishSlot && i != 49 && i != 45 && i != 53) {
                ustawItem(i, filler);
            }
        }
    }

    /**
     * Wypełnia nagłówek
     */
    private void wypelnijNaglowek() {
        double saldo = plugin.getEconomy() != null ? plugin.getEconomy().getBalance(player) : 0.0;

        ItemStack balance = stworzItem(
            Material.GOLD_INGOT,
            "&e&lTwoje Saldo",
            List.of(
                "&7Aktualne saldo: &a" + String.format("%.2f", saldo) + " monet",
                "",
                "&7Wkładaj ryby do slotów poniżej",
                "&7i kliknij &aSPRZEDAJ WSZYSTKO"
            )
        );
        ustawItem(4, balance);
    }

    /**
     * Wypełnia przyciski
     */
    private void wypelnijPrzyciski() {
        // Sprzedaj wszystko (slot 49)
        ItemStack sellAll = stworzItem(
            Material.EMERALD_BLOCK,
            "&a&lSPRZEDAJ WSZYSTKO",
            List.of(
                "&7Kliknij aby sprzedać wszystkie",
                "&7ryby ze slotów powyżej",
                "",
                "&eCena zależy od:",
                "&a▪ &7Bazowej ceny ryby",
                "&a▪ &7Długości złowionej ryby",
                "&a▪ &7Rzadkości ryby"
            )
        );
        ustawItem(49, sellAll);

        // Powrót (slot 45)
        ItemStack back = stworzItem(Material.DARK_OAK_DOOR, "&c&lPowrót", null);
        ustawItem(45, back);

        // Wyczyść (slot 53)
        ItemStack clear = stworzItem(
            Material.BARRIER,
            "&c&lWyczyść",
            List.of(
                "&7Zwraca wszystkie ryby",
                "&7z GUI do ekwipunku"
            )
        );
        ustawItem(53, clear);
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        int slot = event.getSlot();

        // Powrót
        if (slot == 45) {
            event.setCancelled(true);
            zwrocRybyDoEkwipunku();
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
            return;
        }

        // Sprzedaj wszystko
        if (slot == 49) {
            event.setCancelled(true);
            sprzedajWszystko();
            return;
        }

        // Wyczyść
        if (slot == 53) {
            event.setCancelled(true);
            zwrocRybyDoEkwipunku();
            odswiez();
            return;
        }

        // Sprawdź czy to slot dla ryb
        boolean isFishSlot = false;
        for (int fishSlot : FISH_SLOTS) {
            if (slot == fishSlot) {
                isFishSlot = true;
                break;
            }
        }

        // Jeśli to slot dla ryb - pozwól na interakcję
        if (isFishSlot) {
            // Nie anuluj - pozwól graczowi wkładać/wyciągać itemy
            return;
        }

        // Reszta slotów - zablokuj
        event.setCancelled(true);
    }

    /**
     * Sprzedaje wszystkie ryby z GUI
     */
    private void sprzedajWszystko() {
        if (plugin.getEconomy() == null) {
            player.sendMessage(koloruj("&cBłąd: System ekonomii nie jest dostępny!"));
            return;
        }

        NamespacedKey fishIdKey = new NamespacedKey(plugin, "fish_id");
        NamespacedKey lengthKey = new NamespacedKey(plugin, "fish_length");

        int sprzedaneRyby = 0;
        double lacznaCena = 0.0;

        // Przejdź przez wszystkie sloty dla ryb
        for (int slot : FISH_SLOTS) {
            ItemStack item = inventory.getItem(slot);

            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.getPersistentDataContainer().has(fishIdKey)) continue;

            // To jest ryba!
            String fishId = meta.getPersistentDataContainer().get(fishIdKey, PersistentDataType.STRING);
            Double length = meta.getPersistentDataContainer().get(lengthKey, PersistentDataType.DOUBLE);

            if (fishId == null || length == null) continue;

            Fish fish = plugin.getFishRegistry().getRyba(fishId).orElse(null);
            if (fish == null) continue;

            // Oblicz cenę
            double cenaZaJedna = obliczCene(fish, length);
            int ilosc = item.getAmount();

            sprzedaneRyby += ilosc;
            lacznaCena += cenaZaJedna * ilosc;

            // Usuń ze slotu
            inventory.setItem(slot, null);
        }

        if (sprzedaneRyby == 0) {
            player.sendMessage(koloruj("&cNie ma żadnych ryb do sprzedania!"));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Dodaj pieniądze
        plugin.getEconomy().depositPlayer(player, lacznaCena);

        // Wiadomość
        player.sendMessage(koloruj("&a&l✓ Sprzedano!"));
        player.sendMessage(koloruj("&7Ilość ryb: &e" + sprzedaneRyby + "x"));
        player.sendMessage(koloruj("&7Otrzymano: &a" + String.format("%.2f", lacznaCena) + " monet"));
        player.sendMessage(koloruj("&7Nowe saldo: &a" + String.format("%.2f", plugin.getEconomy().getBalance(player)) + " monet"));

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

        // Odśwież GUI
        odswiez();
    }

    /**
     * Zwraca ryby z GUI do ekwipunku gracza
     */
    private void zwrocRybyDoEkwipunku() {
        for (int slot : FISH_SLOTS) {
            ItemStack item = inventory.getItem(slot);
            if (item != null) {
                player.getInventory().addItem(item);
                inventory.setItem(slot, null);
            }
        }
    }

    /**
     * Oblicza cenę sprzedaży ryby
     */
    private double obliczCene(Fish fish, double dlugosc) {
        double bazowa = fish.getBazowaCena();
        double sredniaRyby = (fish.getMinDlugosc() + fish.getMaxDlugosc()) / 2.0;
        double dlugoscModifier = dlugosc / sredniaRyby;
        double rzadkoscModifier = 1.0 + (fish.getRzadkosc().ordinal() * 0.2); // +20% za rzadkość

        return bazowa * dlugoscModifier * rzadkoscModifier;
    }

    /**
     * Tworzy item z nazwą i lore
     */
    private ItemStack stworzItem(Material material, String nazwa, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(koloruj(nazwa));

            if (lore != null) {
                meta.setLore(lore.stream()
                    .map(this::koloruj)
                    .toList());
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
