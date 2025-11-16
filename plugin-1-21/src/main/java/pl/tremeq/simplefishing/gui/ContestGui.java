package pl.tremeq.simplefishing.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.contest.Contest;
import pl.tremeq.simplefishing.api.gui.SimpleFishingGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GUI konkursów łowienia
 * Pokazuje aktywny konkurs, ranking i informacje
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ContestGui extends SimpleFishingGui {

    private final SimpleFishingPlugin plugin;
    private static final int GUI_SIZE = 54;

    public ContestGui(Player player, SimpleFishingPlugin plugin) {
        super(player, plugin.getConfig().getString("gui.tytuly.konkurs", "&b&lKonkurs Łowienia"), GUI_SIZE);
        this.plugin = plugin;
    }

    @Override
    public void inicjalizuj() {
        // Wypełnij tło
        ItemStack filler = stworzItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        wypelnij(filler);

        if (!plugin.getContestManager().czyJestAktywnyKonkurs()) {
            // Brak aktywnego konkursu
            ItemStack noContest = stworzItem(
                Material.BARRIER,
                "&c&lBrak Aktywnego Konkursu",
                List.of(
                    "&7Obecnie nie ma aktywnego konkursu",
                    "&7łowienia ryb.",
                    "",
                    "&eOczekuj na następny konkurs!"
                )
            );
            ustawItem(22, noContest);

            // Powrót
            ItemStack back = stworzItem(Material.DARK_OAK_DOOR, "&c&lPowrót", null);
            ustawItem(49, back);
            return;
        }

        var contest = plugin.getContestManager().getAktywnyKonkurs().get();

        // Informacje o konkursie (góra)
        wypelnijInformacje(contest);

        // Ranking (środek)
        wypelnijRanking(contest);

        // Nawigacja
        ItemStack back = stworzItem(Material.DARK_OAK_DOOR, "&c&lPowrót", null);
        ustawItem(49, back);
    }

    /**
     * Wypełnia informacje o konkursie
     */
    private void wypelnijInformacje(Contest contest) {
        // Ikona konkursu
        ItemStack contestIcon = stworzItem(
            Material.DIAMOND,
            "&b&l" + contest.getNazwa(),
            List.of(
                "&7Typ: &f" + contest.getTyp().getNazwa(),
                "&7Cel: &f" + contest.getTyp().getOpis(),
                "",
                "&7Pozostały czas: &e" + formatujCzas(contest.getPozostaloSekund()),
                "",
                "&eŁów ryby aby wziąć udział!"
            )
        );
        ustawItem(4, contestIcon);

        // Twoje statystyki
        int miejsce = contest.getMiejsce(player.getUniqueId());
        double wynik = contest.getWynik(player.getUniqueId());

        Material material = miejsce > 0 ? Material.GOLD_INGOT : Material.IRON_INGOT;
        List<String> lore = new ArrayList<>();

        if (miejsce > 0) {
            lore.add("&7Twoje miejsce: &a#" + miejsce);
            lore.add("&7Twój wynik: &a" + String.format("%.2f", wynik));
            lore.add("");
            lore.add("&eŁów dalej aby poprawić wynik!");
        } else {
            lore.add("&7Nie bierzesz jeszcze udziału");
            lore.add("&7w konkursie!");
            lore.add("");
            lore.add("&eZłów rybę aby dołączyć!");
        }

        ItemStack yourStats = stworzItem(material, "&e&lTwoje Statystyki", lore);
        ustawItem(13, yourStats);
    }

    /**
     * Wypełnia ranking
     */
    private void wypelnijRanking(Contest contest) {
        var ranking = contest.getRanking();

        if (ranking.isEmpty()) {
            ItemStack empty = stworzItem(
                Material.PAPER,
                "&e&lRanking",
                List.of("&7Brak uczestników", "", "&eZłów pierwszą rybę!")
            );
            ustawItem(22, empty);
            return;
        }

        // Tytuł rankingu
        ItemStack title = stworzItem(
            Material.BOOK,
            "&e&lRanking - Top 10",
            null
        );
        ustawItem(18, title);

        // Wyświetl top 10 (sloty 19-28, 28-37, 37-46)
        int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30};

        for (int i = 0; i < Math.min(10, ranking.size()); i++) {
            var entry = ranking.get(i);
            var p = plugin.getServer().getPlayer(entry.getKey());
            String nick = p != null ? p.getName() : "Nieznany";

            Material mat;
            String prefix;
            if (i == 0) {
                mat = Material.GOLD_BLOCK;
                prefix = "&6&l";
            } else if (i == 1) {
                mat = Material.IRON_BLOCK;
                prefix = "&7&l";
            } else if (i == 2) {
                mat = Material.COPPER_BLOCK;
                prefix = "&c&l";
            } else {
                mat = Material.PLAYER_HEAD;
                prefix = "&f";
            }

            ItemStack rankItem = stworzItem(
                mat,
                prefix + "#" + (i + 1) + " " + nick,
                List.of(
                    "&7Wynik: &a" + String.format("%.2f", entry.getValue()),
                    "",
                    (player.getUniqueId().equals(entry.getKey()) ? "&e&l>>> To Ty! <<<" : "")
                )
            );

            if (i < slots.length) {
                ustawItem(slots[i], rankItem);
            }
        }
    }

    @Override
    public void obsluzKlikniecie(InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getSlot();

        // Powrót do menu głównego
        if (slot == 49) {
            player.closeInventory();
            MainGui mainGui = new MainGui(player, plugin);
            mainGui.inicjalizuj();
            plugin.getGuiManager().otworzGui(player, mainGui);
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
     * Tworzy item z nazwą i lore
     */
    private ItemStack stworzItem(Material material, String nazwa, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(koloruj(nazwa));

            if (lore != null) {
                meta.setLore(lore.stream()
                    .map(this::koloruj)
                    .toList());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Koloruje tekst
     */
    private String koloruj(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
