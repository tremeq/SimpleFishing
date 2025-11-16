package pl.tremeq.simplefishing.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.gui.MainGui;

/**
 * Główna komenda pluginu /simplefishing
 *
 * @author tremeq
 * @version 1.0.0
 */
public class SimpleFishingCommand implements CommandExecutor {

    private final SimpleFishingPlugin plugin;

    public SimpleFishingCommand(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Brak argumentów - otwórz GUI
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(koloruj("&cTa komenda jest dostępna tylko dla graczy!"));
                return true;
            }

            if (!player.hasPermission("simplefishing.gui")) {
                player.sendMessage(koloruj("&cNie masz uprawnień do użycia GUI!"));
                return true;
            }

            // Otwórz główne GUI
            MainGui gui = new MainGui(player, plugin);
            gui.inicjalizuj();
            boolean opened = plugin.getGuiManager().otworzGui(player, gui);
            if (!opened) {
                player.sendMessage(koloruj("&cNie można otworzyć GUI! Spróbuj ponownie."));
            }
            return true;
        }

        // Subkomendy
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                pokazHelp(sender);
                return true;

            case "reload":
                if (!sender.hasPermission("simplefishing.reload")) {
                    sender.sendMessage(koloruj("&cNie masz uprawnień do przeładowania konfiguracji!"));
                    return true;
                }

                plugin.getConfigManager().zaladujKonfiguracje();
                sender.sendMessage(koloruj("&aKonfiguracja została przeładowana!"));
                return true;

            case "contest":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(koloruj("&cTa komenda jest dostępna tylko dla graczy!"));
                    return true;
                }
                obsluzKonkurs(player, args);
                return true;

            case "shop":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(koloruj("&cTa komenda jest dostępna tylko dla graczy!"));
                    return true;
                }

                if (!player.hasPermission("simplefishing.shop")) {
                    player.sendMessage(koloruj("&cNie masz uprawnień do sklepu!"));
                    return true;
                }

                // Otwórz GUI sklepu (zostanie zaimplementowane)
                player.sendMessage(koloruj("&aOtwieranie sklepu..."));
                return true;

            case "info":
                pokazInfo(sender);
                return true;

            default:
                sender.sendMessage(koloruj("&cNieznana komenda! Użyj &e/sf help"));
                return true;
        }
    }

    /**
     * Pokazuje pomoc
     */
    private void pokazHelp(CommandSender sender) {
        sender.sendMessage(koloruj("&e&l=== SimpleFishing - Pomoc ==="));
        sender.sendMessage(koloruj("&a/sf &7- Otwiera główne GUI"));
        sender.sendMessage(koloruj("&a/sf help &7- Pokazuje tę pomoc"));
        sender.sendMessage(koloruj("&a/sf info &7- Informacje o pluginie"));
        sender.sendMessage(koloruj("&a/sf shop &7- Otwiera sklep ryb"));
        sender.sendMessage(koloruj("&a/sf contest &7- Informacje o konkursie"));
        sender.sendMessage(koloruj("&a/sf reload &7- Przeładowuje konfigurację &c(Admin)"));
    }

    /**
     * Pokazuje informacje o pluginie
     */
    private void pokazInfo(CommandSender sender) {
        sender.sendMessage(koloruj("&e&l=== SimpleFishing ==="));
        sender.sendMessage(koloruj("&7Wersja: &a" + plugin.getVersion()));
        sender.sendMessage(koloruj("&7Autor: &atremeq"));
        sender.sendMessage(koloruj("&7Zarejestrowanych ryb: &a" + plugin.getFishRegistry().getLiczbaRyb()));
        sender.sendMessage(koloruj("&7Zarejestrowanych przynęt: &a" + plugin.getBaitRegistry().getLiczbaBaitow()));

        if (plugin.getContestManager().czyJestAktywnyKonkurs()) {
            var contest = plugin.getContestManager().getAktywnyKonkurs();
            contest.ifPresent(c -> sender.sendMessage(koloruj("&7Aktywny konkurs: &a" + c.getNazwa())));
        }
    }

    /**
     * Obsługuje komendy konkursów
     */
    private void obsluzKonkurs(Player player, String[] args) {
        if (!player.hasPermission("simplefishing.contest")) {
            player.sendMessage(koloruj("&cNie masz uprawnień do konkursów!"));
            return;
        }

        // Subkomendy administracyjne
        if (args.length > 1) {
            String subCmd = args[1].toLowerCase();

            switch (subCmd) {
                case "start":
                    if (!player.hasPermission("simplefishing.contest.manage")) {
                        player.sendMessage(koloruj("&cNie masz uprawnień do zarządzania konkursami!"));
                        return;
                    }
                    rozpocznijKonkurs(player, args);
                    return;

                case "stop":
                    if (!player.hasPermission("simplefishing.contest.manage")) {
                        player.sendMessage(koloruj("&cNie masz uprawnień do zarządzania konkursami!"));
                        return;
                    }
                    zatrzymajKonkurs(player);
                    return;

                case "help":
                    pokazHelpKonkursu(player);
                    return;
            }
        }

        // Jeśli brak aktywnego konkursu
        if (!plugin.getContestManager().czyJestAktywnyKonkurs()) {
            player.sendMessage(koloruj("&cObecnie nie ma aktywnego konkursu!"));
            if (player.hasPermission("simplefishing.contest.manage")) {
                player.sendMessage(koloruj("&7Użyj &e/sf contest start <typ> <czas> &7aby rozpocząć konkurs"));
            }
            return;
        }

        // Pokaż informacje o konkursie
        var contest = plugin.getContestManager().getAktywnyKonkurs().get();

        player.sendMessage(koloruj("&e&l=== " + contest.getNazwa() + " ==="));
        player.sendMessage(koloruj("&7Typ: &a" + contest.getTyp().getNazwa()));
        player.sendMessage(koloruj("&7Opis: &f" + contest.getTyp().getOpis()));
        player.sendMessage(koloruj("&7Pozostały czas: &a" + formatujCzas(contest.getPozostaloSekund())));

        int miejsce = contest.getMiejsce(player.getUniqueId());
        if (miejsce > 0) {
            player.sendMessage(koloruj("&7Twoje miejsce: &a#" + miejsce));
            player.sendMessage(koloruj("&7Twój wynik: &a" + String.format("%.2f", contest.getWynik(player.getUniqueId()))));
        } else {
            player.sendMessage(koloruj("&7Nie bierzesz jeszcze udziału w konkursie!"));
            player.sendMessage(koloruj("&eZłów rybę aby dołączyć!"));
        }

        // Ranking top 5
        player.sendMessage(koloruj("&e&lTop 5:"));
        var ranking = contest.getRanking();
        if (ranking.isEmpty()) {
            player.sendMessage(koloruj("&7Brak uczestników"));
        } else {
            for (int i = 0; i < Math.min(5, ranking.size()); i++) {
                var entry = ranking.get(i);
                var p = plugin.getServer().getPlayer(entry.getKey());
                String nick = p != null ? p.getName() : "Nieznany";
                player.sendMessage(koloruj("&a" + (i + 1) + ". &f" + nick + " &7- &a" + String.format("%.2f", entry.getValue())));
            }
        }
    }

    /**
     * Rozpoczyna nowy konkurs
     */
    private void rozpocznijKonkurs(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(koloruj("&cUżycie: &e/sf contest start <typ> <czas_w_minutach>"));
            player.sendMessage(koloruj("&7Dostępne typy:"));
            player.sendMessage(koloruj("&a- NAJWIEKSZA_RYBA &7- Złap największą rybę"));
            player.sendMessage(koloruj("&a- NAJWIECEJ_RYB &7- Złap jak najwięcej ryb"));
            player.sendMessage(koloruj("&a- SUMA_DLUGOSCI &7- Suma długości wszystkich ryb"));
            player.sendMessage(koloruj("&a- NAJDLUZSZA_SUMA &7- Złap najdłuższą rybę"));
            return;
        }

        if (plugin.getContestManager().czyJestAktywnyKonkurs()) {
            player.sendMessage(koloruj("&cJest już aktywny konkurs! Użyj &e/sf contest stop &caby go zatrzymać."));
            return;
        }

        String typStr = args[2].toUpperCase();
        int czas;

        try {
            czas = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(koloruj("&cCzas musi być liczbą całkowitą (minuty)!"));
            return;
        }

        if (czas < 1 || czas > 1440) {
            player.sendMessage(koloruj("&cCzas musi być między 1 a 1440 minutami (24h)!"));
            return;
        }

        // Sprawdź typ konkursu
        pl.tremeq.simplefishing.api.contest.Contest.ContestType typ;
        try {
            typ = pl.tremeq.simplefishing.api.contest.Contest.ContestType.valueOf(typStr);
        } catch (IllegalArgumentException e) {
            player.sendMessage(koloruj("&cNieznany typ konkursu! Dostępne: NAJWIEKSZA_RYBA, NAJWIECEJ_RYB, SUMA_DLUGOSCI, NAJDLUZSZA_SUMA"));
            return;
        }

        // Stwórz i rozpocznij konkurs
        String id = "contest_" + System.currentTimeMillis();
        java.time.LocalDateTime start = java.time.LocalDateTime.now();
        java.time.LocalDateTime koniec = start.plusMinutes(czas);

        var contest = new pl.tremeq.simplefishing.api.contest.Contest(
            id,
            typ.getNazwa(),
            typ,
            start,
            koniec,
            new java.util.ArrayList<>(), // Brak nagród (można dodać później)
            10 // Top 10
        );

        plugin.getContestManager().zarejestrujKonkurs(contest);
        plugin.getContestManager().rozpocznijKonkurs(id);

        // Broadcast do wszystkich graczy
        plugin.getServer().broadcastMessage(koloruj("&e&l=== KONKURS ŁOWIENIA ==="));
        plugin.getServer().broadcastMessage(koloruj("&aRozpoczął się konkurs: &e" + typ.getNazwa()));
        plugin.getServer().broadcastMessage(koloruj("&7Cel: &f" + typ.getOpis()));
        plugin.getServer().broadcastMessage(koloruj("&7Czas trwania: &a" + czas + " minut"));
        plugin.getServer().broadcastMessage(koloruj("&eWeź wędkę i łów ryby!"));
    }

    /**
     * Zatrzymuje aktywny konkurs
     */
    private void zatrzymajKonkurs(Player player) {
        if (!plugin.getContestManager().czyJestAktywnyKonkurs()) {
            player.sendMessage(koloruj("&cNie ma aktywnego konkursu!"));
            return;
        }

        var contest = plugin.getContestManager().getAktywnyKonkurs().get();

        // Pokaż wyniki przed zakończeniem
        player.sendMessage(koloruj("&eZatrzymywanie konkursu: &a" + contest.getNazwa()));

        var ranking = contest.getRanking();
        if (!ranking.isEmpty()) {
            player.sendMessage(koloruj("&e&lWyniki finalne:"));
            for (int i = 0; i < Math.min(5, ranking.size()); i++) {
                var entry = ranking.get(i);
                var p = plugin.getServer().getPlayer(entry.getKey());
                String nick = p != null ? p.getName() : "Nieznany";
                player.sendMessage(koloruj("&a" + (i + 1) + ". &f" + nick + " &7- &a" + String.format("%.2f", entry.getValue())));
            }
        }

        plugin.getContestManager().zakonczAktywnyKonkurs();

        // Broadcast
        plugin.getServer().broadcastMessage(koloruj("&e&l=== KONIEC KONKURSU ==="));
        plugin.getServer().broadcastMessage(koloruj("&7Konkurs &e" + contest.getNazwa() + " &7został zakończony!"));
    }

    /**
     * Pokazuje pomoc dla komend konkursu
     */
    private void pokazHelpKonkursu(Player player) {
        player.sendMessage(koloruj("&e&l=== Konkursy - Pomoc ==="));
        player.sendMessage(koloruj("&a/sf contest &7- Pokaż aktywny konkurs i ranking"));
        player.sendMessage(koloruj("&a/sf contest help &7- Pomoc"));

        if (player.hasPermission("simplefishing.contest.manage")) {
            player.sendMessage(koloruj("&c&lKomendy administracyjne:"));
            player.sendMessage(koloruj("&a/sf contest start <typ> <czas> &7- Rozpocznij konkurs"));
            player.sendMessage(koloruj("&a/sf contest stop &7- Zatrzymaj konkurs"));
        }
    }

    /**
     * Formatuje czas
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

    /**
     * Koloruje tekst
     */
    private String koloruj(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
