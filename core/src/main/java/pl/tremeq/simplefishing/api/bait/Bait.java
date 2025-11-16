package pl.tremeq.simplefishing.api.bait;

import org.bukkit.inventory.ItemStack;
import pl.tremeq.simplefishing.api.fish.FishRarity;

import java.util.List;
import java.util.Map;

/**
 * Reprezentuje przynętę (bait) w pluginie
 * Przynęta zwiększa szansę na złowienie określonych typów ryb
 *
 * @author tremeq
 * @version 1.0.0
 */
public class Bait {

    private final String id;
    private final String nazwa;
    private final String displayName;
    private final List<String> lore;
    private final ItemStack itemStack;
    private final double szansaBonusOgolem; // Ogólny bonus do szansy
    private final Map<FishRarity, Double> bonusyRzadkosci; // Bonusy dla konkretnych rzadkości
    private final List<String> preferencjeRyb; // ID ryb, na które bait daje bonus
    private final double bonusDlaPreferencji; // Dodatkowy mnożnik dla preferowanych ryb
    private final int maxUzycia; // Ile razy można użyć (-1 = nieskończone)
    private final double cenaSklep;

    private Bait(BaitBuilder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.displayName = builder.displayName;
        this.lore = builder.lore;
        this.itemStack = builder.itemStack;
        this.szansaBonusOgolem = builder.szansaBonusOgolem;
        this.bonusyRzadkosci = builder.bonusyRzadkosci;
        this.preferencjeRyb = builder.preferencjeRyb;
        this.bonusDlaPreferencji = builder.bonusDlaPreferencji;
        this.maxUzycia = builder.maxUzycia;
        this.cenaSklep = builder.cenaSklep;
    }

    // Gettery
    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public ItemStack getItemStack() { return itemStack.clone(); }
    public double getSzansaBonusOgolem() { return szansaBonusOgolem; }
    public Map<FishRarity, Double> getBonusyRzadkosci() { return bonusyRzadkosci; }
    public List<String> getPreferencjeRyb() { return preferencjeRyb; }
    public double getBonusDlaPreferencji() { return bonusDlaPreferencji; }
    public int getMaxUzycia() { return maxUzycia; }
    public double getCenaSklep() { return cenaSklep; }

    /**
     * Sprawdza czy bait daje bonus dla danej ryby
     * @param fishId ID ryby
     * @return true jeśli daje bonus
     */
    public boolean maPreferencjeRyby(String fishId) {
        return preferencjeRyb.contains(fishId);
    }

    /**
     * Pobiera bonus dla danej rzadkości
     * @param rarity Rzadkość
     * @return Wartość bonusu (1.0 = bez bonusu)
     */
    public double getBonusRzadkosci(FishRarity rarity) {
        return bonusyRzadkosci.getOrDefault(rarity, 1.0);
    }

    /**
     * Builder do tworzenia przynęt
     */
    public static class BaitBuilder {
        private String id;
        private String nazwa;
        private String displayName;
        private List<String> lore;
        private ItemStack itemStack;
        private double szansaBonusOgolem = 1.0;
        private Map<FishRarity, Double> bonusyRzadkosci = Map.of();
        private List<String> preferencjeRyb = List.of();
        private double bonusDlaPreferencji = 2.0; // Domyślnie x2 dla preferowanych
        private int maxUzycia = -1;
        private double cenaSklep = 0;

        public BaitBuilder(String id) {
            this.id = id;
        }

        public BaitBuilder nazwa(String nazwa) {
            this.nazwa = nazwa;
            return this;
        }

        public BaitBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public BaitBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public BaitBuilder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public BaitBuilder szansaBonusOgolem(double bonus) {
            this.szansaBonusOgolem = bonus;
            return this;
        }

        public BaitBuilder bonusyRzadkosci(Map<FishRarity, Double> bonusy) {
            this.bonusyRzadkosci = bonusy;
            return this;
        }

        public BaitBuilder preferencjeRyb(List<String> preferencje) {
            this.preferencjeRyb = preferencje;
            return this;
        }

        public BaitBuilder bonusDlaPreferencji(double bonus) {
            this.bonusDlaPreferencji = bonus;
            return this;
        }

        public BaitBuilder maxUzycia(int max) {
            this.maxUzycia = max;
            return this;
        }

        public BaitBuilder cenaSklep(double cena) {
            this.cenaSklep = cena;
            return this;
        }

        public Bait build() {
            return new Bait(this);
        }
    }
}
