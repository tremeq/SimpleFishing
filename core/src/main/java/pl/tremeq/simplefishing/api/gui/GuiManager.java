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
     * Thread-safe: Sprawdza czy inventory zostało otwarte
     *
     * @param player Gracz
     * @param gui GUI do otwarcia
     * @return true jeśli udało się otworzyć GUI
     */
    public boolean otworzGui(Player player, SimpleFishingGui gui) {
        zamknijGui(player); // Zamknij poprzednie GUI

        // Spróbuj otworzyć inventory
        org.bukkit.inventory.InventoryView view = player.openInventory(gui.getInventory());

        // Sprawdź czy się powiodło (może być null jeśli inventory jest full lub inny błąd)
        if (view != null) {
            // Tylko jeśli inventory zostało otwarte, dodaj do mapy
            otwarteMENU.put(player.getUniqueId(), gui);
            return true;
        } else {
            // Nie udało się otworzyć - NIE dodajemy do mapy (zapobiega memory leak)
            player.sendMessage("§cNie można otworzyć GUI! Spróbuj ponownie.");
            return false;
        }
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
