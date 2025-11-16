package pl.tremeq.simplefishing.api.contest;

import org.bukkit.entity.Player;
import pl.tremeq.simplefishing.api.contest.reward.ContestReward;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reprezentuje konkurs łowienia
 * Konkursy mogą mieć różne tryby (największa ryba, najwięcej ryb, itp.)
 *
 * @author tremeq
 * @version 1.0.0
 */
public class Contest {

    private final String id;
    private final String nazwa;
    private final ContestType typ;
    private final LocalDateTime start;
    private final LocalDateTime koniec;
    private final List<ContestReward> nagrody;
    private final Map<UUID, Double> wyniki; // UUID gracza -> wynik
    private final int maxMiejsc; // Ile miejsc nagradzanych
    private boolean aktywny;

    public Contest(String id, String nazwa, ContestType typ, LocalDateTime start,
                   LocalDateTime koniec, List<ContestReward> nagrody, int maxMiejsc) {
        this.id = id;
        this.nazwa = nazwa;
        this.typ = typ;
        this.start = start;
        this.koniec = koniec;
        this.nagrody = nagrody;
        this.wyniki = new ConcurrentHashMap<>();
        this.maxMiejsc = maxMiejsc;
        this.aktywny = false;
    }

    // Gettery
    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public ContestType getTyp() { return typ; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getKoniec() { return koniec; }
    public List<ContestReward> getNagrody() { return nagrody; }
    public Map<UUID, Double> getWyniki() { return Collections.unmodifiableMap(wyniki); }
    public int getMaxMiejsc() { return maxMiejsc; }
    public boolean isAktywny() { return aktywny; }

    /**
     * Rozpoczyna konkurs
     */
    public void rozpocznij() {
        this.aktywny = true;
        this.wyniki.clear();
    }

    /**
     * Kończy konkurs
     */
    public void zakoncz() {
        this.aktywny = false;
    }

    /**
     * Dodaje wynik gracza
     * @param player Gracz
     * @param wartosc Wartość wyniku (długość ryby, liczba ryb, itp.)
     */
    public void dodajWynik(UUID player, double wartosc) {
        if (!aktywny) return;

        wyniki.merge(player, wartosc, (stary, nowy) -> {
            switch (typ) {
                case NAJWIEKSZA_RYBA:
                case NAJDLUZSZA_SUMA:
                    return Math.max(stary, nowy);
                case NAJWIECEJ_RYB:
                case SUMA_DLUGOSCI:
                    return stary + nowy;
                default:
                    return nowy;
            }
        });
    }

    /**
     * Pobiera ranking graczy
     * @return Lista map entry posortowanych od najlepszego
     */
    public List<Map.Entry<UUID, Double>> getRanking() {
        return wyniki.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(maxMiejsc)
                .toList();
    }

    /**
     * Pobiera miejsce gracza w rankingu
     * @param player UUID gracza
     * @return Miejsce (1 = pierwsze) lub -1 jeśli nie uczestniczy
     */
    public int getMiejsce(UUID player) {
        List<Map.Entry<UUID, Double>> ranking = getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getKey().equals(player)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Pobiera wynik gracza
     * @param player UUID gracza
     * @return Wynik lub 0 jeśli nie ma
     */
    public double getWynik(UUID player) {
        return wyniki.getOrDefault(player, 0.0);
    }

    /**
     * Sprawdza czy konkurs trwa
     * @return true jeśli konkurs jest w trakcie
     */
    public boolean czyTrwa() {
        LocalDateTime teraz = LocalDateTime.now();
        return aktywny && teraz.isAfter(start) && teraz.isBefore(koniec);
    }

    /**
     * Pobiera pozostały czas w sekundach
     * @return Pozostały czas lub 0 jeśli zakończony
     */
    public long getPozostaloSekund() {
        if (!czyTrwa()) return 0;
        return java.time.Duration.between(LocalDateTime.now(), koniec).getSeconds();
    }

    /**
     * Typ konkursu łowienia
     */
    public enum ContestType {
        NAJWIEKSZA_RYBA("Największa Ryba", "Złap największą rybę!"),
        NAJWIECEJ_RYB("Najwięcej Ryb", "Złap jak najwięcej ryb!"),
        SUMA_DLUGOSCI("Suma Długości", "Suma długości wszystkich ryb!"),
        NAJDLUZSZA_SUMA("Najdłuższa Ryba", "Złap najdłuższą rybę!");

        private final String nazwa;
        private final String opis;

        ContestType(String nazwa, String opis) {
            this.nazwa = nazwa;
            this.opis = opis;
        }

        public String getNazwa() { return nazwa; }
        public String getOpis() { return opis; }
    }
}
