package pl.tremeq.simplefishing.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;
import pl.tremeq.simplefishing.api.rod.FishingRod;
import pl.tremeq.simplefishing.api.rod.RodTier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GUI do ulepszania wędek
 * Pozwala graczom ulepszyć wędkę na wyższy tier
 */
public class RodUpgradeGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    public RodUpgradeGui(Player player, SimpleFishingPlugin plugin) {
        super(player, ChatColor.DARK_PURPLE + "⬆ Ulepszanie Wędek", 54);
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

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, filler);
        }

        // Sprawdź czy gracz trzyma custom wędkę
        ItemStack rodInHand = player.getInventory().getItemInMainHand();

        if (!plugin.getRodManager().czyCustomowaWedka(rodInHand)) {
            // Brak wędki w ręce
            wyswietlBrakWedki();
            return;
        }

        // Pobierz informacje o wędce
        Optional<String> rodIdOpt = plugin.getRodManager().getWedkaId(rodInHand);
        Optional<RodTier> currentTierOpt = plugin.getRodManager().getTierWedki(rodInHand);

        if (rodIdOpt.isEmpty() || currentTierOpt.isEmpty()) {
            wyswietlBladWedki();
            return;
        }

        RodTier currentTier = currentTierOpt.get();

        // Sprawdź czy można ulepszyć
        if (currentTier.czyMaksymalny()) {
            wyswietlMaksymalnyTier(rodInHand, currentTier);
            return;
        }

        // Pobierz definicję wędki dla następnego tieru
        RodTier nextTier = currentTier.getNastepnyTier();
        Optional<FishingRod> currentRodDefOpt = pobierzDefinicjeWedki(rodIdOpt.get());

        if (currentRodDefOpt.isEmpty()) {
            wyswietlBladWedki();
            return;
        }

        FishingRod currentRodDef = currentRodDefOpt.get();
        Optional<FishingRod> nextRodDefOpt = znajdzDefinicjeDlaTieru(nextTier);

        if (nextRodDefOpt.isEmpty()) {
            wyswietlBladWedki();
            return;
        }

        FishingRod nextRodDef = nextRodDefOpt.get();

        // Wyświetl GUI upgrade
        wyswietlUpgradeInterface(rodInHand, currentTier, nextTier, currentRodDef, nextRodDef);
    }

    /**
     * Wyświetla interface upgrade
     */
    private void wyswietlUpgradeInterface(ItemStack rodItem, RodTier currentTier, RodTier nextTier,
                                          FishingRod currentDef, FishingRod nextDef) {
        // Aktualna wędka (slot 20)
        ItemStack currentDisplay = rodItem.clone();
        ItemMeta currentMeta = currentDisplay.getItemMeta();
        if (currentMeta != null) {
            List<String> lore = currentMeta.hasLore() ? currentMeta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "⬅ OBECNY TIER");
            currentMeta.setLore(lore);
            currentDisplay.setItemMeta(currentMeta);
        }
        inventory.setItem(20, currentDisplay);

        // Strzałka upgrade (slot 22)
        ItemStack arrow = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();
        if (arrowMeta != null) {
            arrowMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "⬆ ULEPSZENIE");
            List<String> arrowLore = new ArrayList<>();
            arrowLore.add(ChatColor.GRAY + "Ulepsz swoją wędkę");
            arrowLore.add(ChatColor.GRAY + "na wyższy tier!");
            arrowMeta.setLore(arrowLore);
            arrow.setItemMeta(arrowMeta);
        }
        inventory.setItem(22, arrow);

        // Następna wędka (slot 24)
        ItemStack nextDisplay = plugin.getRodManager().stworzCustomowaWedke(nextDef);
        ItemMeta nextMeta = nextDisplay.getItemMeta();
        if (nextMeta != null) {
            List<String> lore = nextMeta.hasLore() ? nextMeta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GREEN + "➜ NOWY TIER");
            nextMeta.setLore(lore);
            nextDisplay.setItemMeta(nextMeta);
        }
        inventory.setItem(24, nextDisplay);

        // Wymagania (sloty 37-43)
        wyswietlWymagania(currentDef, currentTier);

        // Przycisk upgrade (slot 49)
        boolean hasRequirements = sprawdzWymagania(currentDef);
        wyswietlPrzyciskUpgrade(hasRequirements);

        // Przycisk powrotu (slot 45)
        wyswietlPrzyciskPowrotu();
    }

    /**
     * Wyświetla wymagania do upgrade
     */
    private void wyswietlWymagania(FishingRod rodDef, RodTier currentTier) {
        FishingRod.UpgradeRequirements reqs = rodDef.getUpgradeRequirements();
        int slot = 37;

        // Wymagane monety
        if (reqs.getKosztMonet() > 0) {
            ItemStack money = new ItemStack(Material.GOLD_INGOT);
            ItemMeta moneyMeta = money.getItemMeta();
            if (moneyMeta != null) {
                double playerBalance = plugin.isVaultEnabled() && plugin.getEconomy() != null ?
                        plugin.getEconomy().getBalance(player) : 0;
                boolean has = playerBalance >= reqs.getKosztMonet();

                moneyMeta.setDisplayName((has ? ChatColor.GREEN : ChatColor.RED) + "Monety");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Wymagane: " + ChatColor.GOLD +
                        MONEY_FORMAT.format(reqs.getKosztMonet()) + " $");
                lore.add(ChatColor.GRAY + "Posiadasz: " + (has ? ChatColor.GREEN : ChatColor.RED) +
                        MONEY_FORMAT.format(playerBalance) + " $");
                lore.add("");
                lore.add(has ? ChatColor.GREEN + "✔ Wystarczająco!" : ChatColor.RED + "✖ Za mało!");
                moneyMeta.setLore(lore);
                money.setItemMeta(moneyMeta);
            }
            inventory.setItem(slot++, money);
        }

        // Wymagane ryby
        for (Map.Entry<String, Integer> entry : reqs.getRequiredFish().entrySet()) {
            String fishId = entry.getKey();
            int required = entry.getValue();
            int playerHas = policzRybyWEkwipunku(fishId);
            boolean has = playerHas >= required;

            ItemStack fishItem = new ItemStack(Material.TROPICAL_FISH);
            ItemMeta fishMeta = fishItem.getItemMeta();
            if (fishMeta != null) {
                fishMeta.setDisplayName((has ? ChatColor.GREEN : ChatColor.RED) +
                        "Ryba: " + ChatColor.AQUA + fishId);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Wymagane: " + ChatColor.YELLOW + required + "x");
                lore.add(ChatColor.GRAY + "Posiadasz: " + (has ? ChatColor.GREEN : ChatColor.RED) +
                        playerHas + "x");
                lore.add("");
                lore.add(has ? ChatColor.GREEN + "✔ Wystarczająco!" : ChatColor.RED + "✖ Za mało!");
                fishMeta.setLore(lore);
                fishItem.setItemMeta(fishMeta);
            }
            inventory.setItem(slot++, fishItem);
            if (slot > 43) break;
        }

        // Wymagane materiały
        for (Map.Entry<Material, Integer> entry : reqs.getRequiredMaterials().entrySet()) {
            if (slot > 43) break;

            Material material = entry.getKey();
            int required = entry.getValue();
            int playerHas = policzMaterialWEkwipunku(material);
            boolean has = playerHas >= required;

            ItemStack matItem = new ItemStack(material);
            ItemMeta matMeta = matItem.getItemMeta();
            if (matMeta != null) {
                matMeta.setDisplayName((has ? ChatColor.GREEN : ChatColor.RED) +
                        formatMaterialName(material));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Wymagane: " + ChatColor.YELLOW + required + "x");
                lore.add(ChatColor.GRAY + "Posiadasz: " + (has ? ChatColor.GREEN : ChatColor.RED) +
                        playerHas + "x");
                lore.add("");
                lore.add(has ? ChatColor.GREEN + "✔ Wystarczająco!" : ChatColor.RED + "✖ Za mało!");
                matMeta.setLore(lore);
                matItem.setItemMeta(matMeta);
            }
            inventory.setItem(slot++, matItem);
        }
    }

    /**
     * Wyświetla przycisk upgrade
     */
    private void wyswietlPrzyciskUpgrade(boolean canUpgrade) {
        ItemStack upgradeButton = new ItemStack(canUpgrade ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta meta = upgradeButton.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(canUpgrade ?
                    ChatColor.GREEN + "⬆ " + ChatColor.BOLD + "ULEPSZ WĘDKĘ!" :
                    ChatColor.RED + "✖ Nie spełniasz wymagań");
            List<String> lore = new ArrayList<>();
            if (canUpgrade) {
                lore.add(ChatColor.GRAY + "Kliknij aby ulepszyć wędkę");
                lore.add(ChatColor.GRAY + "na wyższy tier!");
            } else {
                lore.add(ChatColor.RED + "Potrzebujesz więcej materiałów");
                lore.add(ChatColor.RED + "i monet aby ulepszyć!");
            }
            meta.setLore(lore);
            upgradeButton.setItemMeta(meta);
        }
        inventory.setItem(49, upgradeButton);
    }

    /**
     * Wyświetla przycisk powrotu
     */
    private void wyswietlPrzyciskPowrotu() {
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "« Powrót");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Powrót do menu głównego");
            backMeta.setLore(lore);
            back.setItemMeta(backMeta);
        }
        inventory.setItem(45, back);
    }

    /**
     * Wyświetla komunikat o braku wędki
     */
    private void wyswietlBrakWedki() {
        ItemStack info = new ItemStack(Material.BARRIER);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.RED + "Brak wędki!");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Musisz trzymać customową");
            lore.add(ChatColor.GRAY + "wędkę w ręce!");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inventory.setItem(22, info);
        wyswietlPrzyciskPowrotu();
    }

    /**
     * Wyświetla komunikat o błędzie wędki
     */
    private void wyswietlBladWedki() {
        ItemStack info = new ItemStack(Material.BARRIER);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.RED + "Błąd wędki!");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Nie można odczytać danych wędki");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inventory.setItem(22, info);
        wyswietlPrzyciskPowrotu();
    }

    /**
     * Wyświetla komunikat o maksymalnym tierze
     */
    private void wyswietlMaksymalnyTier(ItemStack rod, RodTier tier) {
        ItemStack display = rod.clone();
        inventory.setItem(22, display);

        ItemStack info = new ItemStack(Material.DIAMOND);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(tier.getKolor() + "⬆ MAKSYMALNY TIER!");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Ta wędka jest już na");
            lore.add(ChatColor.GRAY + "maksymalnym poziomie!");
            lore.add("");
            lore.add(tier.getKolorowaNazwa() + " Tier");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inventory.setItem(49, info);
        wyswietlPrzyciskPowrotu();
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Powrót
        if (event.getSlot() == 45 && clicked.getType() == Material.ARROW) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
            return;
        }

        // Upgrade
        if (event.getSlot() == 49 && clicked.getType() == Material.EMERALD_BLOCK) {
            wykonajUpgrade();
        }
    }

    /**
     * Wykonuje upgrade wędki
     */
    private void wykonajUpgrade() {
        ItemStack rodInHand = player.getInventory().getItemInMainHand();

        if (!plugin.getRodManager().czyCustomowaWedka(rodInHand)) {
            player.sendMessage(ChatColor.RED + "Musisz trzymać wędkę w ręce!");
            player.closeInventory();
            return;
        }

        Optional<String> rodIdOpt = plugin.getRodManager().getWedkaId(rodInHand);
        Optional<RodTier> currentTierOpt = plugin.getRodManager().getTierWedki(rodInHand);

        if (rodIdOpt.isEmpty() || currentTierOpt.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Błąd odczytu danych wędki!");
            player.closeInventory();
            return;
        }

        RodTier currentTier = currentTierOpt.get();
        if (currentTier.czyMaksymalny()) {
            player.sendMessage(ChatColor.RED + "Wędka jest już na maksymalnym tierze!");
            player.closeInventory();
            return;
        }

        Optional<FishingRod> currentDefOpt = pobierzDefinicjeWedki(rodIdOpt.get());
        if (currentDefOpt.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Błąd: Nie znaleziono definicji wędki!");
            player.closeInventory();
            return;
        }

        FishingRod currentDef = currentDefOpt.get();

        // Sprawdź wymagania jeszcze raz
        if (!sprawdzWymagania(currentDef)) {
            player.sendMessage(ChatColor.RED + "Nie spełniasz wymagań!");
            player.closeInventory();
            return;
        }

        // Zabierz zasoby
        FishingRod.UpgradeRequirements reqs = currentDef.getUpgradeRequirements();

        // Zabierz monety
        if (plugin.isVaultEnabled() && plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(player, reqs.getKosztMonet());
        }

        // Zabierz ryby
        for (Map.Entry<String, Integer> entry : reqs.getRequiredFish().entrySet()) {
            zabierzRyby(entry.getKey(), entry.getValue());
        }

        // Zabierz materiały
        for (Map.Entry<Material, Integer> entry : reqs.getRequiredMaterials().entrySet()) {
            zabierzMaterial(entry.getKey(), entry.getValue());
        }

        // Ulepsz wędkę
        RodTier nextTier = currentTier.getNastepnyTier();
        Optional<FishingRod> nextDefOpt = znajdzDefinicjeDlaTieru(nextTier);

        if (nextDefOpt.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Błąd: Nie znaleziono definicji następnego tieru!");
            return;
        }

        boolean success = plugin.getRodManager().ulepszWedke(rodInHand, nextTier, nextDefOpt.get());

        if (success) {
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "✔ " + ChatColor.BOLD + "UPGRADE UDANY!");
            player.sendMessage(ChatColor.GRAY + "Twoja wędka została ulepszona na tier: " +
                    nextTier.getKolorowaNazwa());
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.RED + "Błąd podczas upgrade! Skontaktuj się z administratorem.");
            player.closeInventory();
        }
    }

    // === METODY POMOCNICZE ===

    private Optional<FishingRod> pobierzDefinicjeWedki(String rodId) {
        return plugin.getRodRegistry().getWedka(rodId);
    }

    private Optional<FishingRod> znajdzDefinicjeDlaTieru(RodTier tier) {
        for (FishingRod rod : plugin.getRodRegistry().getAllWedki()) {
            if (rod.getTier() == tier) {
                return Optional.of(rod);
            }
        }
        return Optional.empty();
    }

    private boolean sprawdzWymagania(FishingRod rodDef) {
        FishingRod.UpgradeRequirements reqs = rodDef.getUpgradeRequirements();

        // Sprawdź monety
        if (plugin.isVaultEnabled() && plugin.getEconomy() != null) {
            double balance = plugin.getEconomy().getBalance(player);
            if (balance < reqs.getKosztMonet()) return false;
        } else if (reqs.getKosztMonet() > 0) {
            return false;
        }

        // Sprawdź ryby
        for (Map.Entry<String, Integer> entry : reqs.getRequiredFish().entrySet()) {
            if (policzRybyWEkwipunku(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }

        // Sprawdź materiały
        for (Map.Entry<Material, Integer> entry : reqs.getRequiredMaterials().entrySet()) {
            if (policzMaterialWEkwipunku(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    private int policzRybyWEkwipunku(String fishId) {
        int count = 0;
        NamespacedKey key = new NamespacedKey(plugin, "fish_id");

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            String itemFishId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (fishId.equals(itemFishId)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private int policzMaterialWEkwipunku(Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private void zabierzRyby(String fishId, int amount) {
        int remaining = amount;
        NamespacedKey key = new NamespacedKey(plugin, "fish_id");

        for (ItemStack item : player.getInventory().getContents()) {
            if (remaining <= 0) break;
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            String itemFishId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (!fishId.equals(itemFishId)) continue;

            int toRemove = Math.min(item.getAmount(), remaining);
            item.setAmount(item.getAmount() - toRemove);
            remaining -= toRemove;
        }
    }

    private void zabierzMaterial(Material material, int amount) {
        int remaining = amount;

        for (ItemStack item : player.getInventory().getContents()) {
            if (remaining <= 0) break;
            if (item == null || item.getType() != material) continue;

            int toRemove = Math.min(item.getAmount(), remaining);
            item.setAmount(item.getAmount() - toRemove);
            remaining -= toRemove;
        }
    }

    private String formatMaterialName(Material material) {
        String name = material.name().replace('_', ' ').toLowerCase();
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }
}
