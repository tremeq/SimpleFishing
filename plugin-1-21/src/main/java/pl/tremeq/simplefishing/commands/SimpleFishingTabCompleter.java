package pl.tremeq.simplefishing.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.tremeq.simplefishing.SimpleFishingPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completion dla komendy /simplefishing
 *
 * @author tremeq
 * @version 1.0.0
 */
public class SimpleFishingTabCompleter implements TabCompleter {

    private final SimpleFishingPlugin plugin;

    public SimpleFishingTabCompleter(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Główne subkomendy
            List<String> mainCommands = Arrays.asList("help", "info", "reload", "contest", "shop");

            completions.addAll(mainCommands.stream()
                .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList()));
        }
        else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("contest")) {
                // Subkomendy konkursu
                List<String> contestCommands = new ArrayList<>();
                contestCommands.add("help");

                // Dodaj admin commands jeśli ma uprawnienia
                if (sender.hasPermission("simplefishing.contest.manage")) {
                    contestCommands.add("start");
                    contestCommands.add("stop");
                }

                completions.addAll(contestCommands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList()));
            }
        }
        else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String contestSubCmd = args[1].toLowerCase();

            if (subCommand.equals("contest") && contestSubCmd.equals("start")) {
                // Typy konkursów
                if (sender.hasPermission("simplefishing.contest.manage")) {
                    List<String> contestTypes = Arrays.asList(
                        "NAJWIEKSZA_RYBA",
                        "NAJWIECEJ_RYB",
                        "SUMA_DLUGOSCI",
                        "NAJDLUZSZA_SUMA"
                    );

                    completions.addAll(contestTypes.stream()
                        .filter(type -> type.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList()));
                }
            }
        }
        else if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            String contestSubCmd = args[1].toLowerCase();

            if (subCommand.equals("contest") && contestSubCmd.equals("start")) {
                // Sugestie czasu (w minutach)
                if (sender.hasPermission("simplefishing.contest.manage")) {
                    List<String> timeSuggestions = Arrays.asList("5", "10", "15", "30", "60", "120");

                    completions.addAll(timeSuggestions.stream()
                        .filter(time -> time.startsWith(args[3]))
                        .collect(Collectors.toList()));
                }
            }
        }

        return completions;
    }
}
