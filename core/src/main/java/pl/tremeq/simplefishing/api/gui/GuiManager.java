package pl.tremeq.simplefishing.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Menedżer GUI pluginu
 * Zarządza otwartymi menu i ich interakcjami
 *
 * @author tremeq
 * @version 1.0.0
 */
public class GuiManager {

    private final Map<UUID, SimpleFishingGui> otwarteMENU;

    public GuiManager() {
        this.otwarteMENU = new HashMap<>();
    }

    /**
     * Otwiera GUI dla gracza
     * @param player Gracz
     * @param gui GUI do otwarcia
     */
    public void otworzGui(Player player, SimpleFishingGui gui) {
        zamknijGui(player);
        otwarteMENU.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    /**
     * Zamyka GUI gracza
     * @param player Gracz
     */
    public void zamknijGui(Player player) {
        UUID playerId = player.getUniqueId();
        if (otwarteMENU.containsKey(playerId)) {
            otwarteMENU.remove(playerId);
            player.closeInventory();
        }
    }

    /**
     * Pobiera otwarte GUI gracza
     * @param player Gracz
     * @return GUI lub null
     */
    public SimpleFishingGui getOtwarteGui(Player player) {
        return otwarteMENU.get(player.getUniqueId());
    }

    /**
     * Pobiera GUI na podstawie Inventory
     * @param inventory Inventory
     * @return GUI lub null
     */
    public SimpleFishingGui getGuiByInventory(Inventory inventory) {
        for (SimpleFishingGui gui : otwarteMENU.values()) {
            if (gui.getInventory().equals(inventory)) {
                return gui;
            }
        }
        return null;
    }

    /**
     * Sprawdza czy gracz ma otwarte GUI
     * @param player Gracz
     * @return true jeśli ma otwarte
     */
    public boolean maOtwarteGui(Player player) {
        return otwarteMENU.containsKey(player.getUniqueId());
    }

    /**
     * Zamyka wszystkie GUI
     */
    public void zamknijWszystkie() {
        otwarteMENU.clear();
    }
}
