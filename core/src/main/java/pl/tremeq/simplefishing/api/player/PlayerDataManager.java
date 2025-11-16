package pl.tremeq.simplefishing.api.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zarządza danymi wszystkich graczy
 * Cache w pamięci + zapis do plików YAML
 *
 * @author tremeq
 * @version 1.0.0
 */
public class PlayerDataManager {

    // Cache danych graczy w pamięci
    private final Map<UUID, PlayerFishData> playerDataCache;

    public PlayerDataManager() {
        this.playerDataCache = new ConcurrentHashMap<>();
    }

    /**
     * Pobiera dane gracza (ładuje z pliku jeśli nie w cache)
     * @param playerId UUID gracza
     * @return PlayerFishData
     */
    public PlayerFishData getPlayerData(UUID playerId) {
        return playerDataCache.computeIfAbsent(playerId, PlayerFishData::new);
    }

    /**
     * Zapisuje dane gracza (do cache, zapis do pliku wywołany osobno)
     * @param data Dane gracza
     */
    public void savePlayerData(PlayerFishData data) {
        playerDataCache.put(data.getPlayerId(), data);
    }

    /**
     * Usuwa dane gracza z cache (np. przy wylogowaniu)
     * @param playerId UUID gracza
     * @return Usunięte dane (do zapisu) lub null
     */
    public PlayerFishData removeFromCache(UUID playerId) {
        return playerDataCache.remove(playerId);
    }

    /**
     * Sprawdza czy gracz ma dane w cache
     * @param playerId UUID gracza
     * @return true jeśli w cache
     */
    public boolean isInCache(UUID playerId) {
        return playerDataCache.containsKey(playerId);
    }

    /**
     * Pobiera wszystkie dane z cache (np. do zapisu wszystkich)
     * @return Mapa UUID -> PlayerFishData
     */
    public Map<UUID, PlayerFishData> getAllCachedData() {
        return new ConcurrentHashMap<>(playerDataCache);
    }

    /**
     * Czyści cały cache
     */
    public void clearCache() {
        playerDataCache.clear();
    }
}
