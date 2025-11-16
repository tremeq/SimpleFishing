package pl.tremeq.simplefishing.api.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.tremeq.simplefishing.api.fish.Fish;

import java.util.*;

/**
 * Menedżer sklepu do sprzedaży ryb
 * Chroni przed duplikacją itemów i oblicza ceny
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ShopManager {

    private final Set<UUID> transakcjeWToku;
    private final Map<UUID, Long> ostatniaTransakcja;
    private final long cooldownMs = 500; // Cooldown między transakcjami

    public ShopManager() {
        this.transakcjeWToku = new HashSet<>();
        this.ostatniaTransakcja = new HashMap<>();
    }

    /**
     * Sprzedaje rybę
     * @param player Gracz sprzedający
     * @param fishItem ItemStack z rybą
     * @param fish Obiekt Fish
     * @return Wynik transakcji
     */
    public SprzedazResult sprzedajRybe(Player player, ItemStack fishItem, Fish fish) {
        UUID playerId = player.getUniqueId();

        // Sprawdź cooldown
        if (!sprawdzCooldown(playerId)) {
            return new SprzedazResult(false, 0, "Poczekaj chwilę przed następną sprzedażą!");
        }

        // Sprawdź czy transakcja już trwa
        if (transakcjeWToku.contains(playerId)) {
            return new SprzedazResult(false, 0, "Transakcja już trwa!");
        }

        try {
            transakcjeWToku.add(playerId);

            // Sprawdź czy gracz ma item
            if (!player.getInventory().contains(fishItem)) {
                return new SprzedazResult(false, 0, "Nie posiadasz tej ryby!");
            }

            // Pobierz długość ryby z NBT (będzie implementowane w module 1.21)
            double dlugosc = fish.getMaxDlugosc(); // Placeholder

            // Oblicz cenę
            double cena = fish.obliczCene(dlugosc);

            // Usuń item z ekwipunku
            player.getInventory().removeItem(fishItem);

            // Dodaj pieniądze (wymaga Vault - będzie w głównym pluginie)
            // economy.depositPlayer(player, cena);

            ostatniaTransakcja.put(playerId, System.currentTimeMillis());

            return new SprzedazResult(true, cena, "Sprzedano rybę za " + String.format("%.2f", cena) + " monet!");

        } finally {
            transakcjeWToku.remove(playerId);
        }
    }

    /**
     * Sprzedaje wszystkie ryby z ekwipunku
     * @param player Gracz
     * @return Wynik transakcji zbiorczej
     */
    public SprzedazResult sprzedajWszystkie(Player player) {
        UUID playerId = player.getUniqueId();

        if (!sprawdzCooldown(playerId)) {
            return new SprzedazResult(false, 0, "Poczekaj chwilę przed następną sprzedażą!");
        }

        if (transakcjeWToku.contains(playerId)) {
            return new SprzedazResult(false, 0, "Transakcja już trwa!");
        }

        try {
            transakcjeWToku.add(playerId);

            double sumaKwota = 0;
            int liczbaRyb = 0;

            // Iteracja po ekwipunku i sprzedaż wszystkich customowych ryb
            // Implementacja będzie w module 1.21 z NBT

            ostatniaTransakcja.put(playerId, System.currentTimeMillis());

            if (liczbaRyb == 0) {
                return new SprzedazResult(false, 0, "Nie posiadasz żadnych ryb do sprzedania!");
            }

            return new SprzedazResult(true, sumaKwota,
                "Sprzedano " + liczbaRyb + " ryb za " + String.format("%.2f", sumaKwota) + " monet!");

        } finally {
            transakcjeWToku.remove(playerId);
        }
    }

    /**
     * Sprawdza cooldown transakcji
     * @param playerId UUID gracza
     * @return true jeśli może wykonać transakcję
     */
    private boolean sprawdzCooldown(UUID playerId) {
        Long ostatnia = ostatniaTransakcja.get(playerId);
        if (ostatnia == null) return true;

        long roznica = System.currentTimeMillis() - ostatnia;
        return roznica >= cooldownMs;
    }

    /**
     * Oblicza wartość wszystkich ryb w ekwipunku gracza
     * @param player Gracz
     * @return Całkowita wartość
     */
    public double obliczWartoscEkwipunku(Player player) {
        // Implementacja będzie w module 1.21
        return 0.0;
    }

    /**
     * Sprawdza czy item jest rybą możliwą do sprzedania
     * @param item ItemStack do sprawdzenia
     * @return true jeśli jest customową rybą
     */
    public boolean czyMoznaSprzedac(ItemStack item) {
        // Sprawdzenie NBT - będzie w module 1.21
        return false;
    }

    /**
     * Wynik transakcji sprzedaży
     */
    public static class SprzedazResult {
        private final boolean sukces;
        private final double kwota;
        private final String wiadomosc;

        public SprzedazResult(boolean sukces, double kwota, String wiadomosc) {
            this.sukces = sukces;
            this.kwota = kwota;
            this.wiadomosc = wiadomosc;
        }

        public boolean isSukces() { return sukces; }
        public double getKwota() { return kwota; }
        public String getWiadomosc() { return wiadomosc; }
    }
}
