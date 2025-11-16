package pl.tremeq.simplefishing.api.rod;

import org.bukkit.ChatColor;

/**
 * Tiery (poziomy) wędek
 * Każdy tier ma swój kolor, bonus do szczęścia i mnożnik wytrzymałości
 */
public enum RodTier {
    COMMON("Zwykła", ChatColor.GRAY, 0, 1.0),
    UNCOMMON("Niezwykła", ChatColor.GREEN, 1, 1.2),
    RARE("Rzadka", ChatColor.BLUE, 2, 1.5),
    EPIC("Epicka", ChatColor.DARK_PURPLE, 4, 2.0),
    LEGENDARY("Legendarna", ChatColor.GOLD, 6, 3.0);

    private final String nazwa;
    private final ChatColor kolor;
    private final int luckBonus;
    private final double durabilityMultiplier;

    RodTier(String nazwa, ChatColor kolor, int luckBonus, double durabilityMultiplier) {
        this.nazwa = nazwa;
        this.kolor = kolor;
        this.luckBonus = luckBonus;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    /**
     * Pobiera nazwę tieru
     * @return Nazwa tieru
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Pobiera kolor tieru
     * @return Kolor ChatColor
     */
    public ChatColor getKolor() {
        return kolor;
    }

    /**
     * Pobiera bonus do luck of the sea
     * @return Wartość bonusu
     */
    public int getLuckBonus() {
        return luckBonus;
    }

    /**
     * Pobiera mnożnik wytrzymałości
     * @return Mnożnik (np. 1.5 = +50% durability)
     */
    public double getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    /**
     * Pobiera kolorową nazwę tieru
     * @return Nazwa z kolorem
     */
    public String getKolorowaNazwa() {
        return kolor + nazwa;
    }

    /**
     * Pobiera następny tier (null jeśli to już max)
     * @return Następny tier lub null
     */
    public RodTier getNastepnyTier() {
        RodTier[] values = values();
        int currentIndex = this.ordinal();

        if (currentIndex + 1 < values.length) {
            return values[currentIndex + 1];
        }

        return null;
    }

    /**
     * Sprawdza czy to maksymalny tier
     * @return true jeśli to LEGENDARY
     */
    public boolean czyMaksymalny() {
        return this == LEGENDARY;
    }

    /**
     * Pobiera tier po nazwie
     * @param nazwa Nazwa tieru
     * @return RodTier lub COMMON jako domyślny
     */
    public static RodTier fromString(String nazwa) {
        for (RodTier tier : values()) {
            if (tier.name().equalsIgnoreCase(nazwa)) {
                return tier;
            }
        }
        return COMMON;
    }
}
