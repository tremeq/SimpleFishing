package pl.tremeq.simplefishing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.tremeq.simplefishing.SimpleFishingPlugin;

/**
 * Listener obsługujący eventy graczy
 *
 * @author tremeq
 * @version 1.0.0
 */
public class PlayerListener implements Listener {

    private final SimpleFishingPlugin plugin;

    public PlayerListener(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Załaduj dane gracza z pliku do cache
        var playerId = event.getPlayer().getUniqueId();

        // Uruchom asynchronicznie aby nie blokować głównego wątku
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            var playerData = plugin.getPlayerDataFileManager().loadPlayerData(playerId);
            plugin.getPlayerDataManager().savePlayerData(playerData);

            plugin.getLogger().info("Załadowano dane gracza: " + event.getPlayer().getName() +
                    " (Unikalne ryby: " + playerData.getUniqueFishCaught() +
                    ", Całkowite złowienia: " + playerData.getTotalFishCaught() + ")");
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var playerId = event.getPlayer().getUniqueId();

        // Zamknij GUI gracza jeśli ma otwarte (PRZED zapisem danych)
        if (plugin.getGuiManager().maOtwarteGui(event.getPlayer())) {
            plugin.getGuiManager().zamknijGui(event.getPlayer());
        }

        // Pobierz dane z cache (ale NIE usuwaj jeszcze!)
        var playerData = plugin.getPlayerDataManager().getPlayerData(playerId);

        if (playerData != null) {
            // NAJPIERW zapisz synchronicznie (blokując), POTEM usuń z cache
            try {
                plugin.getPlayerDataFileManager().savePlayerData(playerData);
                plugin.getLogger().info("Zapisano dane gracza: " + event.getPlayer().getName());

                // Dopiero TERAZ usuń z cache (po udanym zapisie)
                plugin.getPlayerDataManager().removeFromCache(playerId);
            } catch (Exception e) {
                plugin.getLogger().severe("KRYTYCZNY BŁĄD: Nie udało się zapisać danych gracza " +
                    event.getPlayer().getName() + " (" + playerId + "): " + e.getMessage());
                e.printStackTrace();
                // NIE usuwamy z cache jeśli zapis się nie powiódł!
                // Dane pozostają w cache i zostaną zapisane przez auto-save lub onDisable
            }
        }
    }
}
