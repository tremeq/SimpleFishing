package pl.tremeq.simplefishing.api.rod;

/**
 * Reprezentuje ulepszenie wędki
 *
 * @author tremeq
 * @version 1.0.0
 */
public class RodEnchantment {

    private final String id;
    private final String nazwa;
    private final String opis;
    private final int poziom;
    private final RodEnchantmentType typ;
    private final double wartosc;

    public RodEnchantment(String id, String nazwa, String opis, int poziom, RodEnchantmentType typ, double wartosc) {
        this.id = id;
        this.nazwa = nazwa;
        this.opis = opis;
        this.poziom = poziom;
        this.typ = typ;
        this.wartosc = wartosc;
    }

    // Gettery
    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getOpis() { return opis; }
    public int getPoziom() { return poziom; }
    public RodEnchantmentType getTyp() { return typ; }
    public double getWartosc() { return wartosc; }

    /**
     * Typ ulepszenia wędki
     */
    public enum RodEnchantmentType {
        LUCK_BOOST,          // Zwiększa szczęście
        DURABILITY_BOOST,    // Zwiększa wytrzymałość
        BAIT_CAPACITY,       // Zwiększa ilość slotów na baity
        RARE_FISH_CHANCE,    // Zwiększa szansę na rzadkie ryby
        SIZE_BONUS,          // Zwiększa rozmiar złowionych ryb
        SPEED_BONUS          // Przyspiesza łowienie
    }
}
