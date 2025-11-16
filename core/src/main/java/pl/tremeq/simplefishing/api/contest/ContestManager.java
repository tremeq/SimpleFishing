package pl.tremeq.simplefishing.api.contest;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Menedżer konkursów łowienia
 * Zarządza aktywnymi i zaplanowanymi konkursami
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ContestManager {

    private final Map<String, Contest> konkursy;
    private Contest aktywnyKonkurs;

    public ContestManager() {
        this.konkursy = new ConcurrentHashMap<>();
        this.aktywnyKonkurs = null;
    }

    /**
     * Rejestruje nowy konkurs
     * @param contest Konkurs do zarejestrowania
     */
    public void zarejestrujKonkurs(Contest contest) {
        konkursy.put(contest.getId(), contest);
    }

    /**
     * Wyrejestrowuje konkurs
     * @param id ID konkursu
     */
    public void wyrejestrujKonkurs(String id) {
        konkursy.remove(id);
    }

    /**
     * Pobiera konkurs po ID
     * @param id ID konkursu
     * @return Optional z konkursem lub pusty
     */
    public Optional<Contest> getKonkurs(String id) {
        return Optional.ofNullable(konkursy.get(id));
    }

    /**
     * Pobiera wszystkie konkursy
     * @return Kolekcja wszystkich konkursów
     */
    public Collection<Contest> getAllKonkursy() {
        return Collections.unmodifiableCollection(konkursy.values());
    }

    /**
     * Pobiera aktywny konkurs
     * @return Optional z aktywnym konkursem lub pusty
     */
    public Optional<Contest> getAktywnyKonkurs() {
        return Optional.ofNullable(aktywnyKonkurs);
    }

    /**
     * Rozpoczyna konkurs
     * @param contestId ID konkursu do rozpoczęcia
     * @return true jeśli udało się rozpocząć
     */
    public boolean rozpocznijKonkurs(String contestId) {
        if (aktywnyKonkurs != null) {
            return false; // Już jest aktywny konkurs
        }

        Optional<Contest> contest = getKonkurs(contestId);
        if (contest.isEmpty()) {
            return false;
        }

        aktywnyKonkurs = contest.get();
        aktywnyKonkurs.rozpocznij();
        return true;
    }

    /**
     * Kończy aktywny konkurs
     * @return true jeśli udało się zakończyć
     */
    public boolean zakonczAktywnyKonkurs() {
        if (aktywnyKonkurs == null) {
            return false;
        }

        aktywnyKonkurs.zakoncz();
        aktywnyKonkurs = null;
        return true;
    }

    /**
     * Dodaje wynik w aktywnym konkursie
     * @param player UUID gracza
     * @param wartosc Wartość wyniku
     */
    public void dodajWynik(UUID player, double wartosc) {
        if (aktywnyKonkurs != null && aktywnyKonkurs.isAktywny()) {
            aktywnyKonkurs.dodajWynik(player, wartosc);
        }
    }

    /**
     * Sprawdza czy jest aktywny konkurs
     * @return true jeśli jest aktywny konkurs
     */
    public boolean czyJestAktywnyKonkurs() {
        return aktywnyKonkurs != null && aktywnyKonkurs.isAktywny();
    }

    /**
     * Pobiera ranking aktywnego konkursu
     * @return Lista wyników lub pusta lista
     */
    public List<Map.Entry<UUID, Double>> getRankingAktywnego() {
        if (aktywnyKonkurs == null) {
            return Collections.emptyList();
        }
        return aktywnyKonkurs.getRanking();
    }

    /**
     * Sprawdza konkursy i aktualizuje ich statusy
     * Powinna być wywoływana regularnie (np. co tick)
     */
    public void aktualizujKonkursy() {
        if (aktywnyKonkurs != null && !aktywnyKonkurs.czyTrwa()) {
            zakonczAktywnyKonkurs();
        }

        // Sprawdź czy jakiś konkurs powinien się rozpocząć
        // (logika auto-startu może być dodana później)
    }

    /**
     * Pobiera konkursy oczekujące na start
     * @return Lista konkursów oczekujących
     */
    public List<Contest> getKonkursyOczekujace() {
        return konkursy.values().stream()
                .filter(c -> !c.isAktywny() && c.getStart().isAfter(java.time.LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    /**
     * Pobiera zakończone konkursy
     * @return Lista zakończonych konkursów
     */
    public List<Contest> getKonkursyZakonczone() {
        return konkursy.values().stream()
                .filter(c -> !c.isAktywny() && c.getKoniec().isBefore(java.time.LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}
