package pl.tremeq.simplefishing.api.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Przechowuje dane gracza o złowionych rybach
 * Dla każdej ryby trzyma:
 * - Czy została złowiona (odblokowana)
 * - Ile razy została złowiona
 * - Największa złowiona długość
 *
 * @author tremeq
 * @version 1.0.0
 */
public class PlayerFishData {

    private final UUID playerId;

    // Mapa: fishId -> FishStatistics
    private final Map<String, FishStatistics> fishStats;

    // Ogólne statystyki
    private int totalFishCaught;          // Całkowita liczba złowionych ryb
    private double totalLengthCaught;     // Suma długości wszystkich ryb
    private int contestsWon;              // Liczba wygranych konkursów
    private double totalMoneyEarned;      // Zarobione monety ze sprzedaży

    public PlayerFishData(UUID playerId) {
        this.playerId = playerId;
        this.fishStats = new HashMap<>();
        this.totalFishCaught = 0;
        this.totalLengthCaught = 0.0;
        this.contestsWon = 0;
        this.totalMoneyEarned = 0.0;
    }

    /**
     * Zapisuje złowienie ryby
     * @param fishId ID ryby
     * @param length Długość złowionej ryby
     */
    public void recordFishCatch(String fishId, double length) {
        FishStatistics stats = fishStats.computeIfAbsent(fishId, k -> new FishStatistics());
        stats.recordCatch(length);

        totalFishCaught++;
        totalLengthCaught += length;
    }

    /**
     * Sprawdza czy gracz złowił daną rybę
     * @param fishId ID ryby
     * @return true jeśli ryba została złowiona (odblokowana)
     */
    public boolean hasCaughtFish(String fishId) {
        return fishStats.containsKey(fishId);
    }

    /**
     * Pobiera statystyki dla danej ryby
     * @param fishId ID ryby
     * @return Statystyki lub null jeśli nie złowiono
     */
    public FishStatistics getFishStatistics(String fishId) {
        return fishStats.get(fishId);
    }

    /**
     * Pobiera wszystkie statystyki ryb
     * @return Mapa fishId -> FishStatistics
     */
    public Map<String, FishStatistics> getAllFishStatistics() {
        return new HashMap<>(fishStats);
    }

    /**
     * Dodaje wygrane pieniądze
     * @param amount Kwota
     */
    public void addMoneyEarned(double amount) {
        this.totalMoneyEarned += amount;
    }

    /**
     * Dodaje wygrane konkursy
     */
    public void addContestWon() {
        this.contestsWon++;
    }

    // Gettery
    public UUID getPlayerId() { return playerId; }
    public int getTotalFishCaught() { return totalFishCaught; }
    public double getTotalLengthCaught() { return totalLengthCaught; }
    public int getContestsWon() { return contestsWon; }
    public double getTotalMoneyEarned() { return totalMoneyEarned; }
    public int getUniqueFishCaught() { return fishStats.size(); }

    /**
     * Klasa wewnętrzna przechowująca statystyki dla konkretnej ryby
     */
    public static class FishStatistics {
        private int timesCaught;           // Ile razy złowiono
        private double largestCaught;      // Największa złowiona
        private double totalLength;        // Suma długości wszystkich

        public FishStatistics() {
            this.timesCaught = 0;
            this.largestCaught = 0.0;
            this.totalLength = 0.0;
        }

        /**
         * Zapisuje złowienie
         * @param length Długość
         */
        public void recordCatch(double length) {
            timesCaught++;
            totalLength += length;

            if (length > largestCaught) {
                largestCaught = length;
            }
        }

        // Gettery
        public int getTimesCaught() { return timesCaught; }
        public double getLargestCaught() { return largestCaught; }
        public double getTotalLength() { return totalLength; }
        public double getAverageLength() {
            return timesCaught > 0 ? totalLength / timesCaught : 0.0;
        }

        // Settery (dla ładowania z pliku)
        public void setTimesCaught(int times) { this.timesCaught = times; }
        public void setLargestCaught(double length) { this.largestCaught = length; }
        public void setTotalLength(double length) { this.totalLength = length; }
    }
}
