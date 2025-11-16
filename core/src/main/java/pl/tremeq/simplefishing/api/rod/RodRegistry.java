package pl.tremeq.simplefishing.api.rod;

import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rejestr wszystkich dostępnych wędek
 * Przechowuje definicje wędek i ich wymagania upgrade
 *
 * @author tremeq
 * @version 1.0.0
 */
public class RodRegistry {

    private final Map<String, FishingRod> rods;

    public RodRegistry() {
        this.rods = new ConcurrentHashMap<>();
        zaladujDomyslneWedki();
    }

    /**
     * Rejestruje wędkę w registry
     * @param rod Wędka do zarejestrowania
     */
    public void zarejestrujWedke(FishingRod rod) {
        rods.put(rod.getId(), rod);
    }

    /**
     * Pobiera wędkę po ID
     * @param id ID wędki
     * @return Optional z wędką
     */
    public Optional<FishingRod> getWedka(String id) {
        return Optional.ofNullable(rods.get(id));
    }

    /**
     * Pobiera wszystkie wędki
     * @return Kolekcja wszystkich wędek
     */
    public Collection<FishingRod> getAllWedki() {
        return new ArrayList<>(rods.values());
    }

    /**
     * Pobiera wędki danego tieru
     * @param tier Tier do filtrowania
     * @return Lista wędek danego tieru
     */
    public List<FishingRod> getWedkiPoTierze(RodTier tier) {
        List<FishingRod> result = new ArrayList<>();
        for (FishingRod rod : rods.values()) {
            if (rod.getTier() == tier) {
                result.add(rod);
            }
        }
        return result;
    }

    /**
     * Sprawdza czy wędka o danym ID istnieje
     * @param id ID wędki
     * @return true jeśli istnieje
     */
    public boolean czyWedkaIstnieje(String id) {
        return rods.containsKey(id);
    }

    /**
     * Ładuje domyślne wędki do registry
     * Definiuje wędki dla każdego tieru z odpowiednimi wymaganiami upgrade
     */
    private void zaladujDomyslneWedki() {
        // ========== COMMON ==========
        FishingRod.UpgradeRequirements commonUpgrade = new FishingRod.UpgradeRequirements()
                .setKosztMonet(50000) // 50k monet
                .addRequiredFish("carp", 50) // 50x Karp
                .addRequiredFish("perch", 30) // 30x Okoń
                .addRequiredMaterial(Material.DIAMOND, 5); // 5x Diament

        FishingRod commonRod = new FishingRod.Builder("starter_rod")
                .nazwa("Wędka Początkującego")
                .tier(RodTier.COMMON)
                .cenaZakupu(10000) // 10k monet na start
                .addLore("&7Podstawowa wędka dla początkujących")
                .addLore("&7rybaków. Nic specjalnego, ale")
                .addLore("&7można ją ulepszyć!")
                .addLore("")
                .addLore("&8Luck Bonus: &a+" + RodTier.COMMON.getLuckBonus())
                .addLore("&8Wytrzymałość: &e" + (int)(100 * RodTier.COMMON.getDurabilityMultiplier()) + "%")
                .upgradeRequirements(commonUpgrade)
                .build();

        // ========== UNCOMMON ==========
        FishingRod.UpgradeRequirements uncommonUpgrade = new FishingRod.UpgradeRequirements()
                .setKosztMonet(150000) // 150k monet
                .addRequiredFish("salmon", 40) // 40x Łosoś
                .addRequiredFish("pike", 25) // 25x Szczupak
                .addRequiredMaterial(Material.EMERALD, 10); // 10x Szmaragd

        FishingRod uncommonRod = new FishingRod.Builder("enhanced_rod")
                .nazwa("Ulepszona Wędka")
                .tier(RodTier.UNCOMMON)
                .addLore("&aNiezwykła wędka z lepszymi")
                .addLore("&astatystykami. Zwiększa szanse")
                .addLore("&ana rzadsze ryby!")
                .addLore("")
                .addLore("&8Luck Bonus: &a+" + RodTier.UNCOMMON.getLuckBonus())
                .addLore("&8Wytrzymałość: &e" + (int)(100 * RodTier.UNCOMMON.getDurabilityMultiplier()) + "%")
                .upgradeRequirements(uncommonUpgrade)
                .build();

        // ========== RARE ==========
        FishingRod.UpgradeRequirements rareUpgrade = new FishingRod.UpgradeRequirements()
                .setKosztMonet(500000) // 500k monet
                .addRequiredFish("tuna", 30) // 30x Tuńczyk (RARE)
                .addRequiredFish("swordfish", 20) // 20x Miecznik (RARE)
                .addRequiredMaterial(Material.NETHERITE_INGOT, 5); // 5x Netherite Ingot

        FishingRod rareRod = new FishingRod.Builder("master_rod")
                .nazwa("Wędka Mistrza")
                .tier(RodTier.RARE)
                .addLore("&9Rzadka wędka stworzona przez")
                .addLore("&9mistrzów rybołówstwa. Znacznie")
                .addLore("&9zwiększa szanse na epicki połów!")
                .addLore("")
                .addLore("&8Luck Bonus: &a+" + RodTier.RARE.getLuckBonus())
                .addLore("&8Wytrzymałość: &e" + (int)(100 * RodTier.RARE.getDurabilityMultiplier()) + "%")
                .upgradeRequirements(rareUpgrade)
                .build();

        // ========== EPIC ==========
        FishingRod.UpgradeRequirements epicUpgrade = new FishingRod.UpgradeRequirements()
                .setKosztMonet(2000000) // 2M monet
                .addRequiredFish("marlin", 20) // 20x Marlin (EPIC)
                .addRequiredFish("whale", 10) // 10x Wieloryb (EPIC)
                .addRequiredFish("kraken_tentacle", 5) // 5x Macka Krakena (LEGENDARY)
                .addRequiredMaterial(Material.NETHER_STAR, 10); // 10x Nether Star

        FishingRod epicRod = new FishingRod.Builder("legendary_fisher_rod")
                .nazwa("Wędka Legendarnego Rybaka")
                .tier(RodTier.EPIC)
                .addLore("&5Epicka wędka, której używają")
                .addLore("&5tylko najwięksi mistrzowie.")
                .addLore("&5Legendy mówią, że może złowić")
                .addLore("&5nawet Krakena!")
                .addLore("")
                .addLore("&8Luck Bonus: &a+" + RodTier.EPIC.getLuckBonus())
                .addLore("&8Wytrzymałość: &e" + (int)(100 * RodTier.EPIC.getDurabilityMultiplier()) + "%")
                .upgradeRequirements(epicUpgrade)
                .build();

        // ========== LEGENDARY ==========
        FishingRod legendaryRod = new FishingRod.Builder("poseidon_rod")
                .nazwa("Trójząb Posejdona")
                .tier(RodTier.LEGENDARY)
                .addLore("&6Legendarna wędka bogów mórz.")
                .addLore("&6Posejdon sam błogosławi każdego,")
                .addLore("&6kto dzierży tę potężną broń.")
                .addLore("&6Maksymalna moc rybołówstwa!")
                .addLore("")
                .addLore("&8Luck Bonus: &a+" + RodTier.LEGENDARY.getLuckBonus())
                .addLore("&8Wytrzymałość: &e" + (int)(100 * RodTier.LEGENDARY.getDurabilityMultiplier()) + "%")
                .addLore("")
                .addLore("&d&l✦ MAKSYMALNY POZIOM ✦")
                .build();

        // Rejestracja wszystkich wędek
        zarejestrujWedke(commonRod);
        zarejestrujWedke(uncommonRod);
        zarejestrujWedke(rareRod);
        zarejestrujWedke(epicRod);
        zarejestrujWedke(legendaryRod);
    }

    /**
     * Pobiera ilość zarejestrowanych wędek
     * @return Ilość wędek
     */
    public int getIloscWedek() {
        return rods.size();
    }
}
