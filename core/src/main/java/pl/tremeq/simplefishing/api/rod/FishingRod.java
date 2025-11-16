package pl.tremeq.simplefishing.api.rod;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reprezentuje customową wędkę z tierem i możliwością upgrade
 *
 * @author tremeq
 * @version 2.0.0
 */
public class FishingRod {

    private final String id;
    private final String nazwa;
    private final RodTier tier;
    private final double cenaZakupu;
    private final List<String> lore;
    private final UpgradeRequirements upgradeRequirements;

    private FishingRod(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.tier = builder.tier;
        this.cenaZakupu = builder.cenaZakupu;
        this.lore = new ArrayList<>(builder.lore);
        this.upgradeRequirements = builder.upgradeRequirements;
    }

    /**
     * Pobiera ID wędki
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * Pobiera nazwę wędki
     * @return Nazwa
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Pobiera tier wędki
     * @return Tier
     */
    public RodTier getTier() {
        return tier;
    }

    /**
     * Pobiera cenę zakupu (tylko dla COMMON tier)
     * @return Cena
     */
    public double getCenaZakupu() {
        return cenaZakupu;
    }

    /**
     * Pobiera lore wędki
     * @return Lista linii lore
     */
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    /**
     * Pobiera wymagania do upgrade na następny tier
     * @return UpgradeRequirements
     */
    public UpgradeRequirements getUpgradeRequirements() {
        return upgradeRequirements;
    }

    /**
     * Pobiera bonus do luck z tieru
     * @return Luck bonus
     */
    public int getLuckBonus() {
        return tier.getLuckBonus();
    }

    /**
     * Pobiera mnożnik wytrzymałości z tieru
     * @return Durability multiplier
     */
    public double getDurabilityMultiplier() {
        return tier.getDurabilityMultiplier();
    }

    /**
     * Sprawdza czy wędka może być ulepszona
     * @return true jeśli nie jest max tier
     */
    public boolean czyMoznaUlepszac() {
        return !tier.czyMaksymalny();
    }

    /**
     * Pobiera następny tier wędki
     * @return Następny tier lub null jeśli max
     */
    public RodTier getNastepnyTier() {
        return tier.getNastepnyTier();
    }

    /**
     * Builder do tworzenia wędek
     */
    public static class Builder {
        private String id;
        private String nazwa;
        private RodTier tier = RodTier.COMMON;
        private double cenaZakupu = 0;
        private final List<String> lore = new ArrayList<>();
        private UpgradeRequirements upgradeRequirements = new UpgradeRequirements();

        public Builder(String id) {
            this.id = id;
        }

        public Builder nazwa(String nazwa) {
            this.nazwa = nazwa;
            return this;
        }

        public Builder tier(RodTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder cenaZakupu(double cena) {
            this.cenaZakupu = cena;
            return this;
        }

        public Builder addLore(String line) {
            this.lore.add(line);
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore.clear();
            this.lore.addAll(lore);
            return this;
        }

        public Builder upgradeRequirements(UpgradeRequirements requirements) {
            this.upgradeRequirements = requirements;
            return this;
        }

        public FishingRod build() {
            if (id == null || nazwa == null) {
                throw new IllegalStateException("ID i nazwa są wymagane");
            }
            return new FishingRod(this);
        }
    }

    /**
     * Wymagania do upgrade wędki na następny tier
     */
    public static class UpgradeRequirements {
        private double kosztMonet;
        private final Map<String, Integer> requiredFish; // fishId -> ilosc
        private final Map<Material, Integer> requiredMaterials; // material -> ilosc

        public UpgradeRequirements() {
            this.kosztMonet = 0;
            this.requiredFish = new HashMap<>();
            this.requiredMaterials = new HashMap<>();
        }

        public double getKosztMonet() {
            return kosztMonet;
        }

        public UpgradeRequirements setKosztMonet(double kosztMonet) {
            this.kosztMonet = kosztMonet;
            return this;
        }

        public Map<String, Integer> getRequiredFish() {
            return new HashMap<>(requiredFish);
        }

        public UpgradeRequirements addRequiredFish(String fishId, int ilosc) {
            requiredFish.put(fishId, ilosc);
            return this;
        }

        public Map<Material, Integer> getRequiredMaterials() {
            return new HashMap<>(requiredMaterials);
        }

        public UpgradeRequirements addRequiredMaterial(Material material, int ilosc) {
            requiredMaterials.put(material, ilosc);
            return this;
        }

        /**
         * Sprawdza czy są jakiekolwiek wymagania
         * @return true jeśli są wymagania
         */
        public boolean hasRequirements() {
            return kosztMonet > 0 || !requiredFish.isEmpty() || !requiredMaterials.isEmpty();
        }
    }
}
