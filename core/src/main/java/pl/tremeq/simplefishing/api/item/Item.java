package pl.tremeq.simplefishing.api.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

/**
 * Reprezentuje przedmiot możliwy do wyłowienia
 * Przedmioty mogą być śmieciami, skarbami lub innymi obiektami morskimi
 *
 * @author tremeq
 * @version 1.0.0
 */
public class Item {

    private final String id;
    private final String nazwa;
    private final List<String> opis;
    private final Material material;
    private final ItemRarity rzadkosc;
    private final double wartoscSprzedazy;
    private final Double customowaSzansa; // null = użyj szansy z rzadkości
    private final Map<Enchantment, Integer> enchantmenty;
    private final int customModelData;

    private Item(ItemBuilder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.opis = builder.opis;
        this.material = builder.material;
        this.rzadkosc = builder.rzadkosc;
        this.wartoscSprzedazy = builder.wartoscSprzedazy;
        this.customowaSzansa = builder.customowaSzansa;
        this.enchantmenty = builder.enchantmenty;
        this.customModelData = builder.customModelData;
    }

    // Gettery
    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public List<String> getOpis() { return new ArrayList<>(opis); }
    public Material getMaterial() { return material; }
    public ItemRarity getRzadkosc() { return rzadkosc; }
    public double getWartoscSprzedazy() { return wartoscSprzedazy; }
    public Map<Enchantment, Integer> getEnchantmenty() { return new HashMap<>(enchantmenty); }
    public int getCustomModelData() { return customModelData; }

    /**
     * Pobiera szansę wyłowienia tego przedmiotu
     * Jeśli ustawiono customową szansę - użyj jej, w przeciwnym razie użyj szansy z rzadkości
     */
    public double getSzansa() {
        return customowaSzansa != null ? customowaSzansa : rzadkosc.getSzansa();
    }

    /**
     * Builder dla tworzenia przedmiotów
     */
    public static class ItemBuilder {
        private final String id;
        private final String nazwa;
        private final Material material;
        private final ItemRarity rzadkosc;

        private List<String> opis = new ArrayList<>();
        private double wartoscSprzedazy = 0.0;
        private Double customowaSzansa = null;
        private Map<Enchantment, Integer> enchantmenty = new HashMap<>();
        private int customModelData = 0;

        public ItemBuilder(String id, String nazwa, Material material, ItemRarity rzadkosc) {
            this.id = id;
            this.nazwa = nazwa;
            this.material = material;
            this.rzadkosc = rzadkosc;
        }

        public ItemBuilder opis(List<String> opis) {
            this.opis = new ArrayList<>(opis);
            return this;
        }

        public ItemBuilder wartoscSprzedazy(double wartosc) {
            this.wartoscSprzedazy = wartosc;
            return this;
        }

        public ItemBuilder customowaSzansa(Double szansa) {
            this.customowaSzansa = szansa;
            return this;
        }

        public ItemBuilder enchantment(Enchantment enchantment, int poziom) {
            this.enchantmenty.put(enchantment, poziom);
            return this;
        }

        public ItemBuilder customModelData(int data) {
            this.customModelData = data;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", nazwa='" + nazwa + '\'' +
                ", material=" + material +
                ", rzadkosc=" + rzadkosc +
                ", szansa=" + getSzansa() +
                '}';
    }
}
