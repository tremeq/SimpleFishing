package pl.tremeq.simplefishing.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;
import pl.tremeq.simplefishing.api.rod.FishingRod;
import pl.tremeq.simplefishing.api.rod.RodTier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI sklepu z wędkami
 * Pozwala graczom kupić podstawowe wędki (COMMON tier)
 */
public class RodShopGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    public RodShopGui(Player player, SimpleFishingPlugin plugin) {
        super(player, ChatColor.DARK_GREEN + "⚒ Sklep z Wędkami", 27);
        this.plugin = plugin;
    }

    @Override
    public void inicjalizuj() {

        // Dekoracje
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, filler);
        }

        // Pobierz tylko COMMON wędki do sprzedaży
        List<FishingRod> commonRods = plugin.getRodRegistry().getWedkiPoTierze(RodTier.COMMON);

        // Wyświetl wędki do kupienia
        int[] rodSlots = {11, 13, 15}; // 3 sloty na wędki
        int slotIndex = 0;

        for (FishingRod rod : commonRods) {
            if (slotIndex >= rodSlots.length) break;

            ItemStack rodDisplay = stworzWyswietlaczeWedki(rod);
            inventory.setItem(rodSlots[slotIndex], rodDisplay);
            slotIndex++;
        }

        // Przycisk powrotu
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "« Powrót");
            List<String> backLore = new ArrayList<>();
            backLore.add(ChatColor.GRAY + "Kliknij aby wrócić do menu głównego");
            backMeta.setLore(backLore);
            backButton.setItemMeta(backMeta);
        }
        inventory.setItem(22, backButton);
    }

    /**
     * Tworzy ItemStack wyświetlający wędkę w sklepie
     */
    private ItemStack stworzWyswietlaczeWedki(FishingRod rod) {
        ItemStack display = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = display.getItemMeta();
        if (meta == null) return display;

        // Nazwa z kolorem tieru
        meta.setDisplayName(rod.getTier().getKolorowaNazwa() + ChatColor.BOLD + " " + rod.getNazwa());

        // Lore
        List<String> lore = new ArrayList<>();
        lore.add("");

        // Lore z wędki
        for (String line : rod.getLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        lore.add("");
        lore.add(ChatColor.GOLD + "┃ " + ChatColor.YELLOW + "Cena: " +
                ChatColor.GREEN + MONEY_FORMAT.format(rod.getCenaZakupu()) + " $");
        lore.add("");

        // Info o saldzie gracza
        if (plugin.isVaultEnabled() && plugin.getEconomy() != null) {
            double balance = plugin.getEconomy().getBalance(player);
            boolean canAfford = balance >= rod.getCenaZakupu();

            lore.add(ChatColor.GRAY + "Twoje saldo: " +
                    (canAfford ? ChatColor.GREEN : ChatColor.RED) +
                    MONEY_FORMAT.format(balance) + " $");
        }

        lore.add("");
        lore.add(ChatColor.GREEN + "» " + ChatColor.BOLD + "KLIKNIJ ABY KUPIĆ");

        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Przycisk powrotu
        if (event.getSlot() == 22 && clicked.getType() == Material.ARROW) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
            return;
        }

        // Kliknięcie na wędkę
        if (clicked.getType() == Material.FISHING_ROD) {
            handleRodPurchase(clicked);
        }
    }

    /**
     * Obsługuje kupno wędki
     */
    private void handleRodPurchase(ItemStack display) {
        // Znajdź wędkę po nazwie wyświetlanej
        List<FishingRod> commonRods = plugin.getRodRegistry().getWedkiPoTierze(RodTier.COMMON);

        for (FishingRod rod : commonRods) {
            String expectedName = rod.getTier().getKolor() + ChatColor.BOLD.toString() + " " + rod.getNazwa();
            String actualName = display.getItemMeta() != null ? display.getItemMeta().getDisplayName() : "";

            if (actualName.equals(expectedName)) {
                attemptPurchase(rod);
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "Błąd: Nie znaleziono wędki!");
    }

    /**
     * Próbuje kupić wędkę
     */
    private void attemptPurchase(FishingRod rod) {
        // Sprawdź czy Vault jest włączony
        if (!plugin.isVaultEnabled() || plugin.getEconomy() == null) {
            player.sendMessage(ChatColor.RED + "System ekonomii nie jest dostępny!");
            return;
        }

        double cost = rod.getCenaZakupu();
        double balance = plugin.getEconomy().getBalance(player);

        // Sprawdź czy gracz ma wystarczająco monet
        if (balance < cost) {
            player.sendMessage(ChatColor.RED + "Nie masz wystarczająco monet!");
            player.sendMessage(ChatColor.GRAY + "Potrzebujesz: " + ChatColor.GOLD +
                    MONEY_FORMAT.format(cost) + " $");
            player.sendMessage(ChatColor.GRAY + "Masz: " + ChatColor.YELLOW +
                    MONEY_FORMAT.format(balance) + " $");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Sprawdź czy gracz ma miejsce w ekwipunku
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Nie masz miejsca w ekwipunku!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Pobierz pieniądze
        plugin.getEconomy().withdrawPlayer(player, cost);

        // Stwórz i daj wędkę graczowi
        ItemStack rodItem = plugin.getRodManager().stworzCustomowaWedke(rod);
        player.getInventory().addItem(rodItem);

        // Komunikaty
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "✔ " + ChatColor.BOLD + "ZAKUP UDANY!");
        player.sendMessage(ChatColor.GRAY + "Kupiłeś: " + rod.getTier().getKolor() +
                ChatColor.BOLD + rod.getNazwa());
        player.sendMessage(ChatColor.GRAY + "Zapłacono: " + ChatColor.GOLD +
                MONEY_FORMAT.format(cost) + " $");
        player.sendMessage(ChatColor.GRAY + "Nowe saldo: " + ChatColor.GREEN +
                MONEY_FORMAT.format(plugin.getEconomy().getBalance(player)) + " $");
        player.sendMessage("");

        // Dźwięk sukcesu
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);

        // Odśwież GUI
        odswiez();
    }
}
