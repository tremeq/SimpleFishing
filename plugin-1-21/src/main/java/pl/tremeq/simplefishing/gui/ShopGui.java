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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI sklepu ryb
 * Pozwala sprzedawać złowione ryby za monety
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ShopGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;
    private final Map<Integer, FishSellData> slotToFishMap;
    private static final int GUI_SIZE = 54;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 28; // 4 rzędy × 7 slotów

    public ShopGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.sklep", "&a&lSklep Ryb"), GUI_SIZE);
        this.plugin = plugin;
        this.slotToFishMap = new HashMap<>();
    }

    @Override
    public void inicjalizuj() {
        slotToFishMap.clear();

        // Wypełnij tło
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        wypelnij(filler);

        // Nagłówek
        wypelnijNaglowek();

        // Ryby do sprzedaży
        wypelnijRyby();

        // Nawigacja
        wypelnijNawigacje();
    }

    /**
     * Wypełnia nagłówek
     */
    private void wypelnijNaglowek() {
        // Informacje o saldzie
        double saldo = plugin.getEconomy() != null ? plugin.getEconomy().getBalance(player) : 0.0;

        ItemStack balance = stworzItem(
            Material.GOLD_INGOT,
            "&e&lTwoje Saldo",
            List.of(
                "&7Aktualne saldo: &a" + String.format("%.2f", saldo) + " monet",
                "",
                "&7Kliknij rybę aby ją sprzedać!"
            )
        );
        ustawItem(4, balance);

        // Instrukcja
        ItemStack info = stworzItem(
            Material.BOOK,
            "&6&lJak to działa?",
            List.of(
                "&7Sklep wykrywa automatycznie",
                "&7wszystkie ryby w twoim ekwipunku",
                "",
                "&eKliknij lewym &7aby sprzedać &a1 rybę",
                "&eKliknij prawym &7aby sprzedać &awszystkie",
                "",
                "&7Cena zależy od:",
                "&a▪ &7Bazowej ceny ryby",
                "&a▪ &7Długości ryby",
                "&a▪ &7Rzadkości"
            )
        );
        ustawItem(13, info);
    }

    /**
     * Wypełnia ryby z ekwipunku gracza
     */
    private void wypelnijRyby() {
        List<FishSellData> fishToSell = skanujRyby();

        if (fishToSell.isEmpty()) {
            ItemStack noFish = stworzItem(
                Material.BARRIER,
                "&c&lBrak Ryb",
                List.of(
                    "&7Nie masz żadnych ryb",
                    "&7do sprzedania!",
                    "",
                    "&eZłów rybę i wróć tutaj!"
                )
            );
            ustawItem(31, noFish);
            return;
        }

        // Sloty dla ryb (4 rzędy × 7 kolumn)
        int[] fishSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, fishToSell.size());

        for (int i = startIndex; i < endIndex; i++) {
            FishSellData data = fishToSell.get(i);
            int slotIndex = i - startIndex;

            if (slotIndex < fishSlots.length) {
                ItemStack displayItem = stworzRybeSprzedaz(data);
                int slot = fishSlots[slotIndex];
                ustawItem(slot, displayItem);
                slotToFishMap.put(slot, data);
            }
        }
    }

    /**
     * Skanuje ekwipunek gracza w poszukiwaniu ryb
     */
    private List<FishSellData> skanujRyby() {
        List<FishSellData> result = new ArrayList<>();
        NamespacedKey fishIdKey = new NamespacedKey(plugin, "fish_id");
        NamespacedKey lengthKey = new NamespacedKey(plugin, "fish_length");

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.getPersistentDataContainer().has(fishIdKey)) continue;

            String fishId = meta.getPersistentDataContainer().get(fishIdKey, PersistentDataType.STRING);
            Double length = meta.getPersistentDataContainer().get(lengthKey, PersistentDataType.DOUBLE);

            if (fishId == null || length == null) continue;

            Fish fish = plugin.getFishRegistry().getRyba(fishId).orElse(null);
            if (fish == null) continue;

            // Oblicz cenę
            double cena = obliczCene(fish, length);

            result.add(new FishSellData(i, item, fish, length, cena, item.getAmount()));
        }

        return result;
    }

    /**
     * Oblicza cenę sprzedaży ryby
     */
    private double obliczCene(Fish fish, double dlugosc) {
        double bazowa = fish.getBazowaCena();
        double dlugoscModifier = dlugosc / ((fish.getMinDlugosc() + fish.getMaxDlugosc()) / 2.0);
        double rzadkoscModifier = 1.0 + (fish.getRzadkosc().ordinal() * 0.2); // Każda rzadkość +20%

        return bazowa * dlugoscModifier * rzadkoscModifier;
    }

    /**
     * Tworzy item do wyświetlenia w GUI
     */
    private ItemStack stworzRybeSprzedaz(FishSellData data) {
        ItemStack item = new ItemStack(data.fish.getItemStack().getType());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(koloruj(data.fish.getRzadkosc().getKolor() + "&l" + data.fish.getDisplayName()));

            List<String> lore = new ArrayList<>();
            lore.add(koloruj("&7Długość: &f" + String.format("%.2f", data.length) + " cm"));
            lore.add(koloruj("&7Rzadkość: " + data.fish.getRzadkosc().getKolorowaNazwa()));
            lore.add(koloruj("&7Ilość: &f" + data.amount + "x"));
            lore.add("");
            lore.add(koloruj("&7Cena za 1 sztukę: &a" + String.format("%.2f", data.cena) + " monet"));
            lore.add(koloruj("&7Łączna wartość: &a" + String.format("%.2f", data.cena * data.amount) + " monet"));
            lore.add("");
            lore.add(koloruj("&eLPM &7- Sprzedaj 1x"));
            lore.add(koloruj("&ePPM &7- Sprzedaj wszystkie (&a" + data.amount + "x&7)"));

            meta.setLore(lore);

            if (data.fish.getCustomModelData() > 0) {
                meta.setCustomModelData(data.fish.getCustomModelData());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Wypełnia nawigację
     */
    private void wypelnijNawigacje() {
        // Powrót (slot 49)
        ItemStack back = stworzItem(Material.DARK_OAK_DOOR, "&c&lPowrót", null);
        ustawItem(49, back);
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getSlot();

        // Powrót
        if (slot == 49) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
            return;
        }

        // Sprzedaż ryby
        if (slotToFishMap.containsKey(slot)) {
            FishSellData data = slotToFishMap.get(slot);

            if (event.isLeftClick()) {
                // Sprzedaj 1 sztukę
                sprzedajRybe(data, 1);
            } else if (event.isRightClick()) {
                // Sprzedaj wszystkie
                sprzedajRybe(data, data.amount);
            }

            // Odśwież GUI
            odswiez();
        }
    }

    /**
     * Sprzedaje rybę
     */
    private void sprzedajRybe(FishSellData data, int ilosc) {
        if (plugin.getEconomy() == null) {
            player.sendMessage(koloruj("&cBłąd: System ekonomii nie jest dostępny!"));
            return;
        }

        // Usuń ryby z ekwipunku
        ItemStack invItem = player.getInventory().getItem(data.inventorySlot);
        if (invItem == null || invItem.getAmount() < ilosc) {
            player.sendMessage(koloruj("&cNie masz wystarczającej ilości tej ryby!"));
            return;
        }

        int amountToRemove = Math.min(ilosc, invItem.getAmount());
        double cenaLaczna = data.cena * amountToRemove;

        if (invItem.getAmount() == amountToRemove) {
            player.getInventory().setItem(data.inventorySlot, null);
        } else {
            invItem.setAmount(invItem.getAmount() - amountToRemove);
        }

        // Dodaj pieniądze
        plugin.getEconomy().depositPlayer(player, cenaLaczna);

        // Wiadomość
        player.sendMessage(koloruj("&aSprzedano &e" + amountToRemove + "x " + data.fish.getDisplayName() +
            " &aza &e" + String.format("%.2f", cenaLaczna) + " monet&a!"));
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
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

    /**
     * Klasa przechowująca dane o rybie do sprzedaży
     */
    private static class FishSellData {
        final int inventorySlot;
        final ItemStack item;
        final Fish fish;
        final double length;
        final double cena;
        final int amount;

        FishSellData(int inventorySlot, ItemStack item, Fish fish, double length, double cena, int amount) {
            this.inventorySlot = inventorySlot;
            this.item = item;
            this.fish = fish;
            this.length = length;
            this.cena = cena;
            this.amount = amount;
        }
    }
}
