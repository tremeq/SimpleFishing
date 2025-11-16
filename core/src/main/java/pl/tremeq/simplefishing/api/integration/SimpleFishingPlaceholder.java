package pl.tremeq.simplefishing.api.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.tremeq.simplefishing.api.SimpleFishingAPI;
import pl.tremeq.simplefishing.api.contest.Contest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Integracja z PlaceholderAPI
 * Udostępnia placeholdery związane z konkursami i statystykami
 *
 * @author tremeq
 * @version 1.0.0
 */
public class SimpleFishingPlaceholder extends PlaceholderExpansion {

    private final SimpleFishingAPI api;

    public SimpleFishingPlaceholder(SimpleFishingAPI api) {
        this.api = api;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "simplefishing";
    }

    @Override
    public @NotNull String getAuthor() {
        return "tremeq";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // %simplefishing_contest_active% - Czy jest aktywny konkurs
        if (params.equals("contest_active")) {
            return api.getContestManager().czyJestAktywnyKonkurs() ? "Tak" : "Nie";
        }

        // %simplefishing_contest_name% - Nazwa aktywnego konkursu
        if (params.equals("contest_name")) {
            Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
            return contest.map(Contest::getNazwa).orElse("Brak");
        }

        // %simplefishing_contest_type% - Typ aktywnego konkursu
        if (params.equals("contest_type")) {
            Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
            return contest.map(c -> c.getTyp().getNazwa()).orElse("Brak");
        }

        // %simplefishing_contest_time% - Pozostały czas konkursu
        if (params.equals("contest_time")) {
            Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
            if (contest.isEmpty()) return "Brak";

            long sekundy = contest.get().getPozostaloSekund();
            return formatujCzas(sekundy);
        }

        // %simplefishing_contest_place% - Miejsce gracza w konkursie
        if (params.equals("contest_place")) {
            if (player == null) return "0";
            Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
            if (contest.isEmpty()) return "0";

            int miejsce = contest.get().getMiejsce(player.getUniqueId());
            return miejsce == -1 ? "Brak" : String.valueOf(miejsce);
        }

        // %simplefishing_contest_score% - Wynik gracza w konkursie
        if (params.equals("contest_score")) {
            if (player == null) return "0";
            Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
            if (contest.isEmpty()) return "0.0";

            double wynik = contest.get().getWynik(player.getUniqueId());
            return String.format("%.2f", wynik);
        }

        // %simplefishing_contest_leader_1% - Lider konkursu (1 miejsce)
        if (params.startsWith("contest_leader_")) {
            String[] split = params.split("_");
            if (split.length < 3) return "Brak";

            try {
                int miejsce = Integer.parseInt(split[2]);
                Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
                if (contest.isEmpty()) return "Brak";

                List<Map.Entry<UUID, Double>> ranking = contest.get().getRanking();
                if (miejsce <= 0 || miejsce > ranking.size()) return "Brak";

                UUID leaderId = ranking.get(miejsce - 1).getKey();
                Player leader = org.bukkit.Bukkit.getPlayer(leaderId);
                return leader != null ? leader.getName() : "Nieznany";

            } catch (NumberFormatException e) {
                return "Błąd";
            }
        }

        // %simplefishing_contest_leader_score_1% - Wynik lidera (1 miejsce)
        if (params.startsWith("contest_leader_score_")) {
            String[] split = params.split("_");
            if (split.length < 4) return "0";

            try {
                int miejsce = Integer.parseInt(split[3]);
                Optional<Contest> contest = api.getContestManager().getAktywnyKonkurs();
                if (contest.isEmpty()) return "0.0";

                List<Map.Entry<UUID, Double>> ranking = contest.get().getRanking();
                if (miejsce <= 0 || miejsce > ranking.size()) return "0.0";

                double wynik = ranking.get(miejsce - 1).getValue();
                return String.format("%.2f", wynik);

            } catch (NumberFormatException e) {
                return "0.0";
            }
        }

        // %simplefishing_fish_count% - Liczba zarejestrowanych ryb
        if (params.equals("fish_count")) {
            return String.valueOf(api.getFishRegistry().getLiczbaRyb());
        }

        // %simplefishing_bait_count% - Liczba zarejestrowanych baitów
        if (params.equals("bait_count")) {
            return String.valueOf(api.getBaitRegistry().getLiczbaBaitow());
        }

        return null;
    }

    /**
     * Formatuje czas w sekundach do postaci HH:MM:SS
     * @param sekundy Sekundy
     * @return Sformatowany czas
     */
    private String formatujCzas(long sekundy) {
        long godziny = sekundy / 3600;
        long minuty = (sekundy % 3600) / 60;
        long sek = sekundy % 60;

        if (godziny > 0) {
            return String.format("%02d:%02d:%02d", godziny, minuty, sek);
        } else {
            return String.format("%02d:%02d", minuty, sek);
        }
    }
}
