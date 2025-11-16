package pl.tremeq.simplefishing.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Bazowa klasa dla wszystkich GUI w pluginie
 *
 * @author tremeq
 * @version 1.0.0
 */
public abstract class SimpleFishingGui implements InventoryHolder {

    protected final Player player;
    protected final Inventory inventory;
    protected final String tytul;
    protected final int rozmiar;

    /**
     * Konstruktor GUI
     * @param player Gracz dla którego tworzone jest GUI
     * @param tytul Tytuł GUI
     * @param rozmiar Rozmiar (musi być wielokrotnością 9, max 54)
     */
    public SimpleFishingGui(Player player, String tytul, int rozmiar) {
        this.player = player;
        this.tytul = tytul;
        this.rozmiar = rozmiar;
        this.inventory = Bukkit.createInventory(this, rozmiar, tytul);
    }

    /**
     * Inicjalizuje zawartość GUI
     * Powinna być wywołana po utworzeniu GUI
     */
    public abstract void inicjalizuj();

    /**
     * Obsługuje kliknięcie w GUI
     * @param event Event kliknięcia
     */
    public abstract void obsluzKlikniecie(InventoryClickEvent event);

    /**
     * Odświeża GUI
     */
    public void odswiez() {
        inventory.clear();
        inicjalizuj();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public String getTytul() {
        return tytul;
    }

    public int getRozmiar() {
        return rozmiar;
    }

    /**
     * Ustawia item w GUI
     * @param slot Slot
     * @param item Item do ustawienia
     */
    protected void ustawItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < rozmiar) {
            inventory.setItem(slot, item);
        }
    }

    /**
     * Wypełnia GUI określonym itemem
     * @param item Item do wypełnienia
     */
    protected void wypelnij(ItemStack item) {
        for (int i = 0; i < rozmiar; i++) {
            inventory.setItem(i, item);
        }
    }

    /**
     * Wypełnia granice GUI określonym itemem
     * @param item Item do wypełnienia
     */
    protected void wypelnijGranice(ItemStack item) {
        int rows = rozmiar / 9;

        // Górna i dolna linia
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, item);
            inventory.setItem(rozmiar - 9 + i, item);
        }

        // Boki
        for (int i = 1; i < rows - 1; i++) {
            inventory.setItem(i * 9, item);
            inventory.setItem(i * 9 + 8, item);
        }
    }
}
