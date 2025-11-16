package pl.tremeq.simplefishing.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;

/**
 * Listener obsługujący interakcje z GUI
 *
 * @author tremeq
 * @version 1.0.0
 */
public class GuiListener implements Listener {

    private final SimpleFishingPlugin plugin;

    public GuiListener(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Sprawdź czy to nasze GUI
        SimpleFishingGui gui = plugin.getGuiManager().getGuiByInventory(event.getInventory());

        if (gui != null) {
            event.setCancelled(true); // Anuluj domyślne działanie

            // Przekaż event do GUI
            gui.obsluzKlikniecie(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        // Usuń GUI z managera
        SimpleFishingGui gui = plugin.getGuiManager().getOtwarteGui(player);
        if (gui != null && gui.getInventory().equals(event.getInventory())) {
            plugin.getGuiManager().zamknijGui(player);
        }
    }
}
