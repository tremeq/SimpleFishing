package pl.tremeq.simplefishing.api.fish;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * Reprezentuje customową rybę w pluginie
 * Zawiera wszystkie informacje o rybie: wygląd, rzadkość, efekty
 *
 * @author tremeq
 * @version 1.0.0
 */
public class Fish {

    private final String id;
    private final String nazwa;
    private final String displayName;
    private final List<String> lore;
    private final FishRarity rzadkosc;
    private final double minDlugosc;
    private final double maxDlugosc;
    private final ItemStack itemStack;
    private final List<PotionEffect> efekty;
    private final double bazowaCena;
    private final int customModelData;
    private final Double customowaSzansa; // Nadpisanie szansy dla tej konkretnej ryby (null = użyj z rzadkości)

    /**
     * Konstruktor ryby
     * @param builder Builder do tworzenia ryby
     */
    private Fish(FishBuilder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.displayName = builder.displayName;
        this.lore = builder.lore;
        this.rzadkosc = builder.rzadkosc;
        this.minDlugosc = builder.minDlugosc;
        this.maxDlugosc = builder.maxDlugosc;
        this.itemStack = builder.itemStack;
        this.efekty = builder.efekty;
        this.bazowaCena = builder.bazowaCena;
        this.customModelData = builder.customModelData;
        this.customowaSzansa = builder.customowaSzansa;
    }

    // Gettery
    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public FishRarity getRzadkosc() { return rzadkosc; }
    public double getMinDlugosc() { return minDlugosc; }
    public double getMaxDlugosc() { return maxDlugosc; }
    public ItemStack getItemStack() { return itemStack.clone(); }
    public List<PotionEffect> getEfekty() { return efekty; }
    public double getBazowaCena() { return bazowaCena; }
    public int getCustomModelData() { return customModelData; }

    /**
     * Pobiera szansę na złowienie tej ryby
     * @return Customowa szansa lub szansa z rzadkości
     */
    public double getSzansa() {
        return customowaSzansa != null ? customowaSzansa : rzadkosc.getSzansa();
    }

    /**
     * Sprawdza czy ryba ma customową szansę
     * @return true jeśli ma nadpisaną szansę
     */
    public boolean maCustomowaSzanse() {
        return customowaSzansa != null;
    }

    /**
     * Tworzy ItemStack ryby z określoną długością
     * @param dlugosc Długość ryby w cm
     * @return ItemStack z odpowiednimi NBT tagami
     */
    public ItemStack createFishItem(double dlugosc) {
        ItemStack item = itemStack.clone();
        // NBT będzie dodane w implementacji 1.21
        return item;
    }

    /**
     * Oblicza cenę ryby na podstawie długości
     * @param dlugosc Długość ryby
     * @return Obliczona cena
     */
    public double obliczCene(double dlugosc) {
        double mnoznikDlugosci = dlugosc / maxDlugosc;
        double mnoznikRzadkosci = rzadkosc.getCenaMnoznik();
        return bazowaCena * mnoznikDlugosci * mnoznikRzadkosci;
    }

    /**
     * Builder do tworzenia ryb
     */
    public static class FishBuilder {
        private String id;
        private String nazwa;
        private String displayName;
        private List<String> lore;
        private FishRarity rzadkosc;
        private double minDlugosc;
        private double maxDlugosc;
        private ItemStack itemStack;
        private List<PotionEffect> efekty;
        private double bazowaCena;
        private int customModelData;
        private Double customowaSzansa;

        public FishBuilder(String id) {
            this.id = id;
        }

        public FishBuilder nazwa(String nazwa) {
            this.nazwa = nazwa;
            return this;
        }

        public FishBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public FishBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public FishBuilder rzadkosc(FishRarity rzadkosc) {
            this.rzadkosc = rzadkosc;
            return this;
        }

        public FishBuilder minDlugosc(double minDlugosc) {
            this.minDlugosc = minDlugosc;
            return this;
        }

        public FishBuilder maxDlugosc(double maxDlugosc) {
            this.maxDlugosc = maxDlugosc;
            return this;
        }

        public FishBuilder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public FishBuilder efekty(List<PotionEffect> efekty) {
            this.efekty = efekty;
            return this;
        }

        public FishBuilder bazowaCena(double bazowaCena) {
            this.bazowaCena = bazowaCena;
            return this;
        }

        public FishBuilder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public FishBuilder customowaSzansa(Double szansa) {
            this.customowaSzansa = szansa;
            return this;
        }

        public Fish build() {
            return new Fish(this);
        }
    }
}
