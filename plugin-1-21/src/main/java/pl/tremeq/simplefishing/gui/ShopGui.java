package pl.tremeq.simplefishing.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;

import java.util.List;

/**
 * GUI sklepu ryb
 * Pozwala sprzedawać złowione ryby za monety
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ShopGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;

    public ShopGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.sklep", "&a&lSklep Ryb"), 27);
        this.plugin = plugin;
    }

    @Override
    public void inicjalizuj() {
        // Wypełnij tło
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        wypelnij(filler);

        // Informacja o sklepie
        ItemStack info = stworzItem(
            Material.EMERALD,
            "&a&lSklep Ryb",
            List.of(
                "&7Ta funkcja jest obecnie",
                "&7w fazie rozwoju.",
                "",
                "&eWkrótce będziesz mógł:",
                "&a▪ &7Sprzedawać złowione ryby",
                "&a▪ &7Kupować przynęty",
                "&a▪ &7Kupować ulepszenia wędek",
                "",
                "&6Już niedługo!"
            )
        );
        ustawItem(13, info);

        // Powrót
        ItemStack back = stworzItem(Material.DARK_OAK_DOOR, "&c&lPowrót", null);
        ustawItem(22, back);
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getSlot() == 22) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
        }
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
