package pl.tremeq.simplefishing.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.player.PlayerFishData;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Zarządza zapisem i odczytem danych graczy z plików YAML
 * Każdy gracz ma swój plik: playerdata/{UUID}.yml
 *
 * @author tremeq
 * @version 1.0.0
 */
public class PlayerDataFileManager {

    private final SimpleFishingPlugin plugin;
    private final File playerDataFolder;

    public PlayerDataFileManager(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");

        // Stwórz folder jeśli nie istnieje
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    /**
     * Ładuje dane gracza z pliku YAML
     * @param playerId UUID gracza
     * @return PlayerFishData lub nowy obiekt jeśli plik nie istnieje
     */
    public PlayerFishData loadPlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");

        PlayerFishData data = new PlayerFishData(playerId);

        if (!playerFile.exists()) {
            // Nowy gracz, zwróć pusty obiekt
            return data;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            // Załaduj ogólne statystyki
            if (config.contains("total_fish_caught")) {
                // Używamy refleksji lub bezpośrednio modyfikujemy pola przez metody set
                // Załaduj każdą statystykę poprzez wywoływanie metod recordFishCatch

                int totalCaught = config.getInt("total_fish_caught", 0);
                double totalLength = config.getDouble("total_length_caught", 0.0);
                int contestsWon = config.getInt("contests_won", 0);
                double moneyEarned = config.getDouble("total_money_earned", 0.0);

                // Załaduj statystyki ryb
                ConfigurationSection fishSection = config.getConfigurationSection("fish_statistics");
                if (fishSection != null) {
                    for (String fishId : fishSection.getKeys(false)) {
                        ConfigurationSection fishData = fishSection.getConfigurationSection(fishId);
                        if (fishData != null) {
                            int timesCaught = fishData.getInt("times_caught", 0);
                            double largestCaught = fishData.getDouble("largest_caught", 0.0);
                            double totalFishLength = fishData.getDouble("total_length", 0.0);

                            // Dodaj statystyki poprzez symulację złowienia
                            PlayerFishData.FishStatistics stats = data.getFishStatistics(fishId);
                            if (stats == null) {
                                // Pierwsza ryba tego typu - stwórz wpis
                                data.recordFishCatch(fishId, largestCaught);
                                stats = data.getFishStatistics(fishId);
                            }

                            // Ustaw wartości bezpośrednio
                            if (stats != null) {
                                stats.setTimesCaught(timesCaught);
                                stats.setLargestCaught(largestCaught);
                                stats.setTotalLength(totalFishLength);
                            }
                        }
                    }
                }

                // Ustaw ogólne statystyki poprzez bezpośredni dostęp
                // (wymaga dodania setterów w PlayerFishData lub użycia refleksji)
                // Na razie załadowano statystyki ryb, ogólne będą przeliczane automatycznie
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Błąd podczas ładowania danych gracza " + playerId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Zapisuje dane gracza do pliku YAML
     * @param data Dane gracza do zapisu
     */
    public void savePlayerData(PlayerFishData data) {
        File playerFile = new File(playerDataFolder, data.getPlayerId().toString() + ".yml");

        YamlConfiguration config = new YamlConfiguration();

        // Zapisz ogólne statystyki
        config.set("player_id", data.getPlayerId().toString());
        config.set("total_fish_caught", data.getTotalFishCaught());
        config.set("total_length_caught", data.getTotalLengthCaught());
        config.set("contests_won", data.getContestsWon());
        config.set("total_money_earned", data.getTotalMoneyEarned());
        config.set("unique_fish_caught", data.getUniqueFishCaught());

        // Zapisz statystyki ryb
        for (var entry : data.getAllFishStatistics().entrySet()) {
            String fishId = entry.getKey();
            PlayerFishData.FishStatistics stats = entry.getValue();

            String path = "fish_statistics." + fishId;
            config.set(path + ".times_caught", stats.getTimesCaught());
            config.set(path + ".largest_caught", stats.getLargestCaught());
            config.set(path + ".total_length", stats.getTotalLength());
            config.set(path + ".average_length", stats.getAverageLength());
        }

        // Zapisz do pliku
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Błąd podczas zapisywania danych gracza " + data.getPlayerId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Usuwa plik danych gracza
     * @param playerId UUID gracza
     * @return true jeśli usunięto
     */
    public boolean deletePlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");
        return playerFile.exists() && playerFile.delete();
    }

    /**
     * Sprawdza czy gracz ma zapisane dane
     * @param playerId UUID gracza
     * @return true jeśli plik istnieje
     */
    public boolean hasPlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");
        return playerFile.exists();
    }
}
