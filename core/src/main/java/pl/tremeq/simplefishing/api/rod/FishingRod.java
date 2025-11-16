package pl.tremeq.simplefishing.api.rod;

import org.bukkit.inventory.ItemStack;
import pl.tremeq.simplefishing.api.bait.Bait;

import java.util.List;
import java.util.Optional;

/**
 * Reprezentuje customową wędkę
 * Wędka może mieć założoną przynętę i dodatkowe modyfikatory
 *
 * @author tremeq
 * @version 1.0.0
 */
public class FishingRod {

    private final String id;
    private final String nazwa;
    private final String displayName;
    private final List<String> lore;
    private final ItemStack itemStack;
    private final double podstawowySzczescie; // Bazowe szczęście wędki
    private final int maxBaity; // Ile przynęt można nałożyć
    private final double wytrzymalosc; // Bazowa wytrzymałość
    private final List<RodEnchantment> ulepszenia;
    private final double cena;

    private FishingRod(RodBuilder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.displayName = builder.displayName;
        this.lore = builder.lore;
        this.itemStack = builder.itemStack;
        this.podstawowySzczescie = builder.podstawowySzczescie;
        this.maxBaity = builder.maxBaity;
        this.wytrzymalosc = builder.wytrzymalosc;
        this.ulepszenia = builder.ulepszenia;
        this.cena = builder.cena;
    }

    // Gettery
    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public ItemStack getItemStack() { return itemStack.clone(); }
    public double getPodstawowySzczescie() { return podstawowySzczescie; }
    public int getMaxBaity() { return maxBaity; }
    public double getWytrzymalosc() { return wytrzymalosc; }
    public List<RodEnchantment> getUlepszenia() { return ulepszenia; }
    public double getCena() { return cena; }

    /**
     * Tworzy ItemStack wędki
     * @return ItemStack wędki
     */
    public ItemStack createRodItem() {
        ItemStack item = itemStack.clone();
        // NBT będzie dodane w implementacji 1.21
        return item;
    }

    /**
     * Builder do tworzenia wędek
     */
    public static class RodBuilder {
        private String id;
        private String nazwa;
        private String displayName;
        private List<String> lore;
        private ItemStack itemStack;
        private double podstawowySzczescie = 1.0;
        private int maxBaity = 1;
        private double wytrzymalosc = 100.0;
        private List<RodEnchantment> ulepszenia = List.of();
        private double cena = 0;

        public RodBuilder(String id) {
            this.id = id;
        }

        public RodBuilder nazwa(String nazwa) {
            this.nazwa = nazwa;
            return this;
        }

        public RodBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public RodBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public RodBuilder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public RodBuilder podstawowySzczescie(double szczescie) {
            this.podstawowySzczescie = szczescie;
            return this;
        }

        public RodBuilder maxBaity(int max) {
            this.maxBaity = max;
            return this;
        }

        public RodBuilder wytrzymalosc(double wytrzymalosc) {
            this.wytrzymalosc = wytrzymalosc;
            return this;
        }

        public RodBuilder ulepszenia(List<RodEnchantment> ulepszenia) {
            this.ulepszenia = ulepszenia;
            return this;
        }

        public RodBuilder cena(double cena) {
            this.cena = cena;
            return this;
        }

        public FishingRod build() {
            return new FishingRod(this);
        }
    }
}
