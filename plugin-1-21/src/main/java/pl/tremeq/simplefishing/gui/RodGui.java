package pl.tremeq.simplefishing.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.bait.Bait;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI zarządzania wędkami
 * Pozwala ulepszać wędki i nakładać przynęty
 *
 * @author tremeq
 * @version 1.0.0
 */
public class RodGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;
    private final Map<Integer, Bait> slotToBaitMap;
    private static final int GUI_SIZE = 54;

    public RodGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.wedki", "&d&lWędki i Przynęty"), GUI_SIZE);
        this.plugin = plugin;
        this.slotToBaitMap = new HashMap<>();
    }

    @Override
    public void inicjalizuj() {
        slotToBaitMap.clear();

        // Wypełnij tło
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        wypelnij(filler);

        // Nagłówek - informacje o wędce
        wypelnijInformacjeWedki();

        // Przynęty
        wypelnijPrzynety();

        // Nawigacja
        wypelnijNawigacje();
    }

    /**
     * Wypełnia informacje o wędce gracza
     */
    private void wypelnijInformacjeWedki() {
        ItemStack rod = player.getInventory().getItemInMainHand();

        // Sprawdź czy gracz trzyma wędkę
        if (rod.getType() != Material.FISHING_ROD) {
            ItemStack noRod = stworzItem(
                Material.BARRIER,
                "&c&lBrak Wędki",
                List.of(
                    "&7Musisz trzymać wędkę",
                    "&7w głównej ręce!",
                    "",
                    "&eWeź wędkę do ręki i otwórz ponownie"
                )
            );
            ustawItem(4, noRod);
            return;
        }

        // Sprawdź czy to customowa wędka
        boolean isCustom = plugin.getRodManager().czyCustomowaWedka(rod);
        double luckModifier = plugin.getRodManager().obliczSzczescie(rod);

        List<String> lore = new ArrayList<>();
        lore.add("&7Typ: " + (isCustom ? "&aCustomowa Wędka" : "&7Zwykła Wędka"));
        lore.add("");
        lore.add("&e&lStatystyki:");
        lore.add("&7Modyfikator szczęścia: &a" + String.format("%.1fx", luckModifier));
        lore.add("&7Wytrzymałość: &a" + (rod.getType().getMaxDurability() - rod.getDurability()) +
            "&7/&e" + rod.getType().getMaxDurability());
        lore.add("");

        // Zawsze pokazuj przynęty (nie tylko dla custom)
        lore.add("&d&lAktywne przynęty:");
        var baits = plugin.getRodManager().getAktywnePrzynety(rod);
        if (baits.isEmpty()) {
            lore.add("&7Brak przynęt");
        } else {
            // getAktywnePrzynety() już zwraca sformatowane stringi
            baits.forEach(bait -> lore.add(bait));
        }

        if (!isCustom) {
            lore.add("");
            lore.add("&7Możesz nakładać przynęty");
            lore.add("&7aby zwiększyć szanse na rzadkie ryby!");
        }

        lore.add("");
        lore.add("&eKliknij przynętę poniżej aby ją nałożyć!");

        ItemStack rodDisplay = stworzItem(Material.FISHING_ROD, "&d&lTwoja Wędka", lore);
        ustawItem(4, rodDisplay);
    }

    /**
     * Wypełnia dostępne przynęty
     */
    private void wypelnijPrzynety() {
        var allBaits = plugin.getBaitRegistry().getAllBaity();

        if (allBaits.isEmpty()) {
            ItemStack noBaits = stworzItem(
                Material.PAPER,
                "&c&lBrak Przynęt",
                List.of(
                    "&7Brak zarejestrowanych przynęt",
                    "&7w systemie!",
                    "",
                    "&7Skontaktuj się z administratorem"
                )
            );
            ustawItem(22, noBaits);
            return;
        }

        // Tytuł sekcji przynęt
        ItemStack title = stworzItem(
            Material.BOOK,
            "&e&lDostępne Przynęty",
            List.of(
                "&7Kliknij na przynętę aby ją nałożyć",
                "&7na wędkę w twojej ręce",
                "",
                "&7Przynęty zwiększają szansę",
                "&7na złowienie rzadkich ryb!"
            )
        );
        ustawItem(13, title);

        // Sloty dla przynęt
        int[] baitSlots = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };

        int index = 0;
        for (Bait bait : allBaits) {
            if (index >= baitSlots.length) break;

            ItemStack baitItem = stworzPrzynetaItem(bait);
            int slot = baitSlots[index];
            ustawItem(slot, baitItem);
            slotToBaitMap.put(slot, bait);
            index++;
        }
    }

    /**
     * Tworzy item przynęty do wyświetlenia
     */
    private ItemStack stworzPrzynetaItem(Bait bait) {
        List<String> lore = new ArrayList<>();
        lore.add("&e&lBonusy:");
        lore.add("&7Bonus szansy: &a+" + String.format("%.1f%%", (bait.getSzansaBonusOgolem() - 1.0) * 100));
        int uses = bait.getMaxUzycia();
        String usesText = uses == -1 ? "∞" : String.valueOf(uses);
        lore.add("&7Użycia: &a" + usesText);
        lore.add("");

        if (bait.getLore() != null && !bait.getLore().isEmpty()) {
            lore.add("&7Opis:");
            bait.getLore().forEach(line -> lore.add("&f" + line));
            lore.add("");
        }

        // Sprawdź czy gracz ma tę przynętę w ekwipunku
        int iloscWEkwipunku = policzPrzynetyWEkwipunku(bait);

        if (iloscWEkwipunku > 0) {
            lore.add("&aW ekwipunku: &e" + iloscWEkwipunku + "x");
            lore.add("");
            lore.add("&eKliknij aby nałożyć!");
        } else {
            lore.add("&c&lNie masz tej przynęty!");
            lore.add("&7Zdobądź ją łowiąc ryby");
        }

        return stworzItem(bait.getItemStack().getType(), "&d&l" + bait.getNazwa(), lore);
    }

    /**
     * Liczy ile przynęt danego typu ma gracz w ekwipunku
     */
    private int policzPrzynetyWEkwipunku(Bait bait) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) continue;

            // Sprawdź PDC czy to przynęta
            var meta = item.getItemMeta();
            if (meta == null) continue;

            var key = new org.bukkit.NamespacedKey(plugin, "bait_id");
            if (!meta.getPersistentDataContainer().has(key)) continue;

            String baitId = meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
            if (bait.getId().equals(baitId)) {
                count += item.getAmount();
            }
        }
        return count;
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

        // Powrót (slot 49)
        if (slot == 49) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
            return;
        }

        // Aplikuj przynętę
        if (slotToBaitMap.containsKey(slot)) {
            Bait bait = slotToBaitMap.get(slot);
            aplikujPrzynete(bait);
            odswiez();
        }
    }

    /**
     * Aplikuje przynętę na wędkę gracza
     */
    private void aplikujPrzynete(Bait bait) {
        ItemStack rod = player.getInventory().getItemInMainHand();

        // Sprawdź czy gracz trzyma wędkę
        if (rod.getType() != Material.FISHING_ROD) {
            player.sendMessage(koloruj("&cMusisz trzymać wędkę w głównej ręce!"));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Sprawdź czy gracz ma przynętę w ekwipunku
        if (!usuńPrzyneteZEkwipunku(bait)) {
            player.sendMessage(koloruj("&cNie masz tej przynęty w ekwipunku!"));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Aplikuj przynętę
        boolean success = plugin.getRodManager().aplikujPrzynete(rod, bait);

        if (success) {
            // Aktualizuj wędkę w ręce gracza
            player.getInventory().setItemInMainHand(rod);

            int uses = bait.getMaxUzycia();
            String usesText = uses == -1 ? "∞" : String.valueOf(uses);

            player.sendMessage(koloruj("&aPomyślnie nałożono przynętę: &e" + bait.getNazwa() + "&a!"));
            player.sendMessage(koloruj("&7Bonus szansy: &a+" + String.format("%.1f%%", (bait.getSzansaBonusOgolem() - 1.0) * 100)));
            player.sendMessage(koloruj("&7Liczba użyć: &a" + usesText));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        } else {
            player.sendMessage(koloruj("&cNie udało się nałożyć przynęty!"));
            player.sendMessage(koloruj("&7Możliwe że wędka ma już tę przynętę."));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

            // Zwróć przynętę
            zwrocPrzynete(bait);
        }
    }

    /**
     * Usuwa przynętę z ekwipunku gracza
     */
    private boolean usuńPrzyneteZEkwipunku(Bait bait) {
        var key = new org.bukkit.NamespacedKey(plugin, "bait_id");

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            var meta = item.getItemMeta();
            if (meta == null || !meta.getPersistentDataContainer().has(key)) continue;

            String baitId = meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
            if (bait.getId().equals(baitId)) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().setItem(i, null);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Zwraca przynętę do ekwipunku gracza
     */
    private void zwrocPrzynete(Bait bait) {
        ItemStack baitItem = createBaitItemStack(bait);
        player.getInventory().addItem(baitItem);
    }

    /**
     * Tworzy ItemStack przynęty
     */
    private ItemStack createBaitItemStack(Bait bait) {
        ItemStack item = new ItemStack(bait.getItemStack().getType());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(koloruj("&d&l" + bait.getNazwa()));

            List<String> lore = new ArrayList<>();
            lore.add(koloruj(""));
            lore.add(koloruj("&7Bonus szansy: &a+" + String.format("%.1f%%", (bait.getSzansaBonusOgolem() - 1.0) * 100)));
            int uses = bait.getMaxUzycia();
            String usesText = uses == -1 ? "∞" : String.valueOf(uses);
            lore.add(koloruj("&7Użycia: &a" + usesText));

            meta.setLore(lore);

            // Zapisz ID przynęty w PDC
            var key = new org.bukkit.NamespacedKey(plugin, "bait_id");
            meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, bait.getId());

            item.setItemMeta(meta);
        }

        return item;
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
