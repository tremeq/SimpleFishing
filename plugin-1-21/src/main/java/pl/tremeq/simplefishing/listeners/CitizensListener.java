package pl.tremeq.simplefishing.listeners;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.gui.MainGui;

/**
 * Listener obsługujący integrację z Citizens
 * Kliknięcie w NPC otwiera główne GUI
 *
 * @author tremeq
 * @version 1.0.0
 */
public class CitizensListener implements Listener {

    private final SimpleFishingPlugin plugin;

    public CitizensListener(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCClick(NPCRightClickEvent event) {
        Player player = event.getClicker();

        // Sprawdź czy NPC ma metadata SimpleFishing
        if (event.getNPC().data().has("simplefishing")) {
            // Otwórz główne GUI
            MainGui gui = new MainGui(player, plugin);
            gui.inicjalizuj();
            boolean opened = plugin.getGuiManager().otworzGui(player, gui);
            if (!opened) {
                player.sendMessage("§cNie można otworzyć GUI! Spróbuj ponownie.");
            }
        }
    }
}
