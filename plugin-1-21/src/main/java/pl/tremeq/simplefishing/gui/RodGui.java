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
 * GUI zarządzania wędkami
 * Pozwala ulepszać wędki i nakładać przynęty
 *
 * @author tremeq
 * @version 1.0.0
 */
public class RodGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;

    public RodGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.wedki", "&d&lWędki i Przynęty"), 27);
        this.plugin = plugin;
    }

    @Override
    public void inicjalizuj() {
        // Wypełnij tło
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        wypelnij(filler);

        // Informacja o wędkach
        ItemStack info = stworzItem(
            Material.FISHING_ROD,
            "&d&lWędki i Przynęty",
            List.of(
                "&7Ta funkcja jest obecnie",
                "&7w fazie rozwoju.",
                "",
                "&eWkrótce będziesz mógł:",
                "&a▪ &7Ulepszać wędki",
                "&a▪ &7Nakładać przynęty",
                "&a▪ &7Zwiększać szansę na rzadkie ryby",
                "&a▪ &7Zdobywać customowe wędki",
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
