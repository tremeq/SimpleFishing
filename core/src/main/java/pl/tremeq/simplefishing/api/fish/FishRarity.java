package pl.tremeq.simplefishing.api.fish;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum reprezentujący rzadkość ryby
 * Określa szansę złowienia i mnożnik ceny
 * Wartości mogą być nadpisane z configu
 *
 * @author tremeq
 * @version 1.0.0
 */
public enum FishRarity {

    BARDZO_POSPOLITA("Bardzo Pospolita", "&7", 75.0, 0.5),
    POSPOLITA("Pospolita", "&f", 50.0, 1.0),
    NIEPOSPOLITA("Niepospolita", "&a", 30.0, 1.5),
    RZADKA("Rzadka", "&9", 15.0, 2.5),
    BARDZO_RZADKA("Bardzo Rzadka", "&3", 7.0, 4.0),
    EPICKI("Epicka", "&5", 4.0, 6.0),
    LEGENDARNA("Legendarna", "&6", 0.9, 12.0),
    MITYCZNA("Mityczna", "&c", 0.1, 30.0),
    BOSKA("Boska", "&d&l", 0.01, 100.0);

    private final String nazwa;
    private final String kolor;
    private final double domyslnaSzansa; // Domyślna szansa
    private final double domyslnyCenaMnoznik; // Domyślny mnożnik ceny

    // Mapy do nadpisywania wartości z configu
    private static final Map<FishRarity, Double> customSzanse = new HashMap<>();
    private static final Map<FishRarity, Double> customCenaMnozniki = new HashMap<>();

    FishRarity(String nazwa, String kolor, double szansa, double cenaMnoznik) {
        this.nazwa = nazwa;
        this.kolor = kolor;
        this.domyslnaSzansa = szansa;
        this.domyslnyCenaMnoznik = cenaMnoznik;
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
     * Pobiera mnożnik ceny (z configu jeśli ustawiono, lub domyślny)
     */
    public double getCenaMnoznik() {
        return customCenaMnozniki.getOrDefault(this, domyslnyCenaMnoznik);
    }

    /**
     * Pobiera domyślną szansę (bez nadpisań)
     */
    public double getDomyslnaSzansa() {
        return domyslnaSzansa;
    }

    /**
     * Pobiera domyślny mnożnik ceny (bez nadpisań)
     */
    public double getDomyslnyCenaMnoznik() {
        return domyslnyCenaMnoznik;
    }

    /**
     * Ustawia customową szansę z configu
     */
    public static void ustawCustomowaSzanse(FishRarity rarity, double szansa) {
        customSzanse.put(rarity, szansa);
    }

    /**
     * Ustawia customowy mnożnik ceny z configu
     */
    public static void ustawCustomowyCenaMnoznik(FishRarity rarity, double mnoznik) {
        customCenaMnozniki.put(rarity, mnoznik);
    }

    /**
     * Resetuje wszystkie customowe wartości
     */
    public static void resetujCustomoweWartosci() {
        customSzanse.clear();
        customCenaMnozniki.clear();
    }

    /**
     * Pobiera sformatowaną nazwę z kolorem
     * @return Nazwa z kodem koloru
     */
    public String getKolorowaNazwa() {
        return kolor + nazwa;
    }
}
