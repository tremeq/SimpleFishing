package pl.tremeq.simplefishing.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.fish.Fish;
import pl.tremeq.simplefishing.api.item.Item;
import pl.tremeq.simplefishing.api.rod.RodManager;

import java.util.Random;

/**
 * Listener obsługujący łowienie ryb
 * Zastępuje domyślne łowienie customowymi rybami
 *
 * @author tremeq
 * @version 1.0.0
 */
public class FishingListener implements Listener {

    private final SimpleFishingPlugin plugin;
    private final Random random;

    public FishingListener(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        // Sprawdź czy gracz złowił rybę
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        Player player = event.getPlayer();

        // Sprawdź uprawnienia
        if (!player.hasPermission("simplefishing.use")) {
            return;
        }

        // Pobierz wędkę gracza
        ItemStack rod = player.getInventory().getItemInMainHand();
        RodManager rodManager = plugin.getRodManager();

        // Oblicz modyfikator szczęścia
        double luckModifier = 1.0;
        if (rodManager.czyCustomowaWedka(rod)) {
            luckModifier = rodManager.obliczSzczescie(rod);
        }

        // Sprawdź czy system dropów przedmiotów jest włączony
        boolean itemSystemEnabled = plugin.getConfig().getBoolean("drop_system.wlaczone", true);
        double itemDropChance = plugin.getConfig().getDouble("drop_system.szansa_na_przedmiot", 0.25);

        // Losuj czy ma wypaść przedmiot czy ryba
        boolean dropItem = itemSystemEnabled && random.nextDouble() < itemDropChance;

        // Zastąp domyślny łup
        if (event.getCaught() != null) {
            event.getCaught().remove();
        }

        if (dropItem && plugin.getItemRegistry().getLiczbaPrzedmiotow() > 0) {
            // WYLOSUJ PRZEDMIOT
            Item item = plugin.getItemRegistry().wylosujPrzedmiot(luckModifier);

            if (item != null) {
                // Stwórz ItemStack przedmiotu
                ItemStack itemStack = stworzPrzedmiotItem(item);

                // Dodaj przedmiot do ekwipunku
                player.getInventory().addItem(itemStack);

                // Wiadomość dla gracza
                if (plugin.getConfig().getBoolean("drop_system.wiadomosc_po_przedmiocie", true)) {
                    String wiadomosc = String.format("&eWyłowiłeś przedmiot: %s%s&e!",
                            item.getRzadkosc().getKolor(), item.getNazwa());
                    player.sendMessage(koloruj(wiadomosc));
                }
                return;
            }
            // Jeśli nie udało się wylosować przedmiotu, kontynuuj z rybą
        }

        // WYLOSUJ RYBĘ (domyślne zachowanie)
        Fish fish = plugin.getFishRegistry().wylosujRybe(luckModifier);

        if (fish == null) {
            // Brak zarejestrowanych ryb, użyj domyślnego mechanizmu
            return;
        }

        // Wylosuj długość ryby
        double minLen = fish.getMinDlugosc();
        double maxLen = fish.getMaxDlugosc();
        double dlugosc = minLen + (maxLen - minLen) * random.nextDouble();

        // Stwórz ItemStack ryby
        ItemStack fishItem = stworzRybeItem(fish, dlugosc);

        // Dodaj rybę do ekwipunku
        player.getInventory().addItem(fishItem);

        // Zapisz statystyki złowienia ryby
        var playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        playerData.recordFishCatch(fish.getId(), dlugosc);

        // Dodaj wynik do konkursu (jeśli aktywny)
        if (plugin.getContestManager().czyJestAktywnyKonkurs()) {
            plugin.getContestManager().dodajWynik(player.getUniqueId(), dlugosc);
        }

        // Wiadomość dla gracza
        if (plugin.getConfig().getBoolean("lowienie.wiadomosc_po_zlowieniu", true)) {
            String wiadomosc = String.format("&aZłowiłeś %s &a(%.2f cm)!",
                    fish.getDisplayName(), dlugosc);
            player.sendMessage(koloruj(wiadomosc));
        }

        // Aplikuj efekty ryby (jeśli ma)
        if (fish.getEfekty() != null && !fish.getEfekty().isEmpty()) {
            fish.getEfekty().forEach(effect -> player.addPotionEffect(effect));
        }
    }

    /**
     * Tworzy ItemStack ryby z NBT
     * @param fish Ryba
     * @param dlugosc Długość
     * @return ItemStack z NBT
     */
    private ItemStack stworzRybeItem(Fish fish, double dlugosc) {
        ItemStack item = fish.createFishItem(dlugosc);

        // Dodanie NBT tagów (PDC - Persistent Data Container)
        // Implementacja dla 1.21:
        var meta = item.getItemMeta();
        if (meta != null) {
            // Nazwa
            meta.setDisplayName(koloruj(fish.getRzadkosc().getKolor() + fish.getDisplayName()));

            // Lore
            var lore = fish.getLore().stream()
                    .map(this::koloruj)
                    .collect(java.util.stream.Collectors.toList());
            lore.add(koloruj("&7Długość: &f" + String.format("%.2f", dlugosc) + " cm"));
            lore.add(koloruj("&7Rzadkość: " + fish.getRzadkosc().getKolorowaNazwa()));
            meta.setLore(lore);

            // Custom Model Data
            if (fish.getCustomModelData() > 0) {
                meta.setCustomModelData(fish.getCustomModelData());
            }

            // PDC - zapisz ID ryby i długość
            var pdc = meta.getPersistentDataContainer();
            var key = new org.bukkit.NamespacedKey(plugin, "fish_id");
            pdc.set(key, org.bukkit.persistence.PersistentDataType.STRING, fish.getId());

            var lengthKey = new org.bukkit.NamespacedKey(plugin, "fish_length");
            pdc.set(lengthKey, org.bukkit.persistence.PersistentDataType.DOUBLE, dlugosc);

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Tworzy ItemStack przedmiotu z NBT
     * @param przedmiot Przedmiot
     * @return ItemStack z NBT
     */
    private ItemStack stworzPrzedmiotItem(Item przedmiot) {
        ItemStack item = new ItemStack(przedmiot.getMaterial());

        var meta = item.getItemMeta();
        if (meta != null) {
            // Nazwa
            meta.setDisplayName(koloruj(przedmiot.getRzadkosc().getKolor() + przedmiot.getNazwa()));

            // Lore
            var lore = przedmiot.getOpis().stream()
                    .map(this::koloruj)
                    .collect(java.util.stream.Collectors.toList());
            lore.add("");
            lore.add(koloruj("&7Rzadkość: " + przedmiot.getRzadkosc().getKolorowaNazwa()));
            if (przedmiot.getWartoscSprzedazy() > 0) {
                lore.add(koloruj("&7Wartość: &e" + String.format("%.2f", przedmiot.getWartoscSprzedazy()) + " monet"));
            }
            meta.setLore(lore);

            // Custom Model Data
            if (przedmiot.getCustomModelData() > 0) {
                meta.setCustomModelData(przedmiot.getCustomModelData());
            }

            // Enchantmenty
            if (!przedmiot.getEnchantmenty().isEmpty()) {
                przedmiot.getEnchantmenty().forEach((enchant, level) -> {
                    meta.addEnchant(enchant, level, true);
                });
            }

            // PDC - zapisz ID przedmiotu
            var pdc = meta.getPersistentDataContainer();
            var key = new org.bukkit.NamespacedKey(plugin, "item_id");
            pdc.set(key, org.bukkit.persistence.PersistentDataType.STRING, przedmiot.getId());

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Koloruje tekst
     * @param text Tekst
     * @return Skolorowany tekst
     */
    private String koloruj(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
