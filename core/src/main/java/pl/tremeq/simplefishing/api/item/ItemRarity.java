package pl.tremeq.simplefishing.api.item;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum reprezentujący rzadkość przedmiotu wyławianego
 * Określa szansę wyłowienia przedmiotu zamiast ryby
 *
 * @author tremeq
 * @version 1.0.0
 */
public enum ItemRarity {

    SMIECI("Śmieci", "&8", 60.0),           // Butelki, stare buty itp.
    POSPOLITY("Pospolity", "&7", 30.0),     // Muszle, wodorosty
    NIEPOSPOLITY("Niepospolity", "&a", 8.0), // Perły, korale
    RZADKI("Rzadki", "&9", 1.5),            // Skarby, artefakty
    LEGENDARNE("Legendarne", "&6", 0.4),    // Antyczne skarby
    MITYCZNE("Mityczne", "&d&l", 0.1);      // Boskie artefakty

    private final String nazwa;
    private final String kolor;
    private final double domyslnaSzansa;

    // Mapa do nadpisywania wartości z configu
    private static final Map<ItemRarity, Double> customSzanse = new HashMap<>();

    ItemRarity(String nazwa, String kolor, double szansa) {
        this.nazwa = nazwa;
        this.kolor = kolor;
        this.domyslnaSzansa = szansa;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getKolor() {
        return kolor;
    }

    /**
     * Pobiera szansę (z configu jeśli ustawiono, lub domyślną)
     */
    public double getSzansa() {
        return customSzanse.getOrDefault(this, domyslnaSzansa);
    }

    /**
     * Pobiera domyślną szansę (bez nadpisań)
     */
    public double getDomyslnaSzansa() {
        return domyslnaSzansa;
    }

    /**
     * Ustawia customową szansę z configu
     */
    public static void ustawCustomowaSzanse(ItemRarity rarity, double szansa) {
        customSzanse.put(rarity, szansa);
    }

    /**
     * Resetuje wszystkie customowe wartości
     */
    public static void resetujCustomoweWartosci() {
        customSzanse.clear();
    }

    /**
     * Pobiera sformatowaną nazwę z kolorem
     * @return Nazwa z kodem koloru
     */
    public String getKolorowaNazwa() {
        return kolor + nazwa;
    }
}
