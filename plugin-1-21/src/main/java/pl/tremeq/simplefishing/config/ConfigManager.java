package pl.tremeq.simplefishing.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.tremeq.simplefishing.SimpleFishingPlugin;
import pl.tremeq.simplefishing.api.bait.Bait;
import pl.tremeq.simplefishing.api.fish.Fish;
import pl.tremeq.simplefishing.api.fish.FishRarity;
import pl.tremeq.simplefishing.api.item.Item;
import pl.tremeq.simplefishing.api.item.ItemRarity;
import pl.tremeq.simplefishing.api.rod.FishingRod;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zarządza konfiguracją pluginu
 * Ładuje ryby, baity, wędki oraz szanse z plików YAML
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ConfigManager {

    private final SimpleFishingPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration fishConfig;
    private FileConfiguration baitConfig;
    private FileConfiguration itemConfig;
    private FileConfiguration rodConfig;
    private FileConfiguration messagesConfig;

    public ConfigManager(SimpleFishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Ładuje wszystkie konfiguracje
     */
    public void zaladujKonfiguracje() {
        // Utwórz folder pluginu jeśli nie istnieje
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        // Załaduj główną konfigurację
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        // Załaduj inne pliki konfiguracyjne
        fishConfig = zaladujPlik("fish.yml");
        baitConfig = zaladujPlik("baits.yml");
        itemConfig = zaladujPlik("items.yml");
        rodConfig = zaladujPlik("rods.yml");
        messagesConfig = zaladujPlik("messages.yml");

        // Załaduj customowe szanse rzadkości z config.yml
        zaladujSzanseRzadkosci();
        zaladujSzansePrzedmiotow();

        // Załaduj ryby, baity, przedmioty
        zaladujRyby();
        zaladujBaity();
        zaladujPrzedmioty();
        // Wędki są teraz ładowane automatycznie przez RodRegistry.zaladujDomyslneWedki()
        // zaladujWedki();

        plugin.getLogger().info("Konfiguracja załadowana!");
    }

    /**
     * Ładuje plik konfiguracyjny
     */
    private FileConfiguration zaladujPlik(String nazwa) {
        File plik = new File(plugin.getDataFolder(), nazwa);

        if (!plik.exists()) {
            plugin.saveResource(nazwa, false);
        }

        return YamlConfiguration.loadConfiguration(plik);
    }

    /**
     * Ładuje customowe szanse rzadkości z config.yml
     */
    private void zaladujSzanseRzadkosci() {
        if (!config.getBoolean("szanse_rzadkosci.wlaczone", false)) {
            plugin.getLogger().info("Używam domyślnych szans rzadkości z kodu");
            return;
        }

        // Resetuj customowe wartości
        FishRarity.resetujCustomoweWartosci();

        // Załaduj customowe szanse
        for (FishRarity rarity : FishRarity.values()) {
            String path = "szanse_rzadkosci." + rarity.name();
            if (config.contains(path)) {
                double szansa = config.getDouble(path);
                FishRarity.ustawCustomowaSzanse(rarity, szansa);
                plugin.getLogger().info("Ustawiono szansę dla " + rarity.getNazwa() + ": " + szansa);
            }

            // Załaduj mnożniki ceny
            String cenaMnoznikPath = "szanse_rzadkosci.mnozniki_cen." + rarity.name();
            if (config.contains(cenaMnoznikPath)) {
                double mnoznik = config.getDouble(cenaMnoznikPath);
                FishRarity.ustawCustomowyCenaMnoznik(rarity, mnoznik);
            }
        }

        plugin.getLogger().info("Załadowano customowe szanse rzadkości z config.yml!");
    }

    /**
     * Ładuje customowe szanse przedmiotów z config.yml
     */
    private void zaladujSzansePrzedmiotow() {
        if (!config.getBoolean("szanse_przedmiotow.wlaczone", false)) {
            plugin.getLogger().info("Używam domyślnych szans przedmiotów z kodu");
            return;
        }

        // Resetuj customowe wartości
        ItemRarity.resetujCustomoweWartosci();

        // Załaduj customowe szanse
        for (ItemRarity rarity : ItemRarity.values()) {
            String path = "szanse_przedmiotow." + rarity.name();
            if (config.contains(path)) {
                double szansa = config.getDouble(path);
                ItemRarity.ustawCustomowaSzanse(rarity, szansa);
                plugin.getLogger().info("Ustawiono szansę dla przedmiotu " + rarity.getNazwa() + ": " + szansa);
            }
        }

        plugin.getLogger().info("Załadowano customowe szanse przedmiotów z config.yml!");
    }

    /**
     * Ładuje ryby z konfiguracji
     */
    private void zaladujRyby() {
        if (fishConfig == null) return;

        ConfigurationSection rybySection = fishConfig.getConfigurationSection("ryby");
        if (rybySection == null) {
            plugin.getLogger().warning("Brak sekcji 'ryby' w fish.yml!");
            return;
        }

        int licznik = 0;

        for (String key : rybySection.getKeys(false)) {
            ConfigurationSection rybaSection = rybySection.getConfigurationSection(key);
            if (rybaSection == null) continue;

            try {
                // Podstawowe dane
                String nazwa = rybaSection.getString("nazwa", key);
                String displayName = rybaSection.getString("display_name", nazwa);
                List<String> lore = rybaSection.getStringList("lore");

                // Rzadkość
                String rarityStr = rybaSection.getString("rzadkosc", "POSPOLITA");
                FishRarity rzadkosc = FishRarity.valueOf(rarityStr.toUpperCase());

                // Długość
                double minDlugosc = rybaSection.getDouble("min_dlugosc", 10.0);
                double maxDlugosc = rybaSection.getDouble("max_dlugosc", 50.0);

                // Material
                String materialStr = rybaSection.getString("material", "COD");
                Material material = Material.valueOf(materialStr.toUpperCase());
                ItemStack itemStack = new ItemStack(material);

                // Cena
                double cena = rybaSection.getDouble("cena", 10.0);

                // Custom Model Data
                int customModelData = rybaSection.getInt("custom_model_data", 0);

                // Customowa szansa (OPCJONALNE)
                Double customSzansa = null;
                if (rybaSection.contains("custom_szansa")) {
                    customSzansa = rybaSection.getDouble("custom_szansa");
                }

                // Efekty
                List<PotionEffect> efekty = new ArrayList<>();
                if (rybaSection.contains("efekty")) {
                    List<String> efektyList = rybaSection.getStringList("efekty");
                    for (String efektStr : efektyList) {
                        String[] parts = efektStr.split(":");
                        if (parts.length >= 3) {
                            PotionEffectType type = PotionEffectType.getByName(parts[0]);
                            int duration = Integer.parseInt(parts[1]) * 20; // sekundy -> ticki
                            int amplifier = Integer.parseInt(parts[2]) - 1;

                            if (type != null) {
                                efekty.add(new PotionEffect(type, duration, amplifier));
                            }
                        }
                    }
                }

                // Stwórz i zarejestruj rybę
                Fish.FishBuilder builder = new Fish.FishBuilder(key)
                        .nazwa(nazwa)
                        .displayName(displayName)
                        .lore(lore)
                        .rzadkosc(rzadkosc)
                        .minDlugosc(minDlugosc)
                        .maxDlugosc(maxDlugosc)
                        .itemStack(itemStack)
                        .efekty(efekty)
                        .bazowaCena(cena)
                        .customModelData(customModelData);

                // Dodaj customową szansę jeśli jest
                if (customSzansa != null) {
                    builder.customowaSzansa(customSzansa);
                    plugin.getLogger().info("Ryba '" + key + "' ma customową szansę: " + customSzansa);
                }

                Fish fish = builder.build();

                plugin.getFishRegistry().zarejestrujRybe(fish);
                licznik++;

            } catch (Exception e) {
                plugin.getLogger().warning("Błąd podczas ładowania ryby: " + key);
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Załadowano " + licznik + " ryb!");
    }

    /**
     * Ładuje przedmioty z konfiguracji
     */
    private void zaladujPrzedmioty() {
        if (itemConfig == null) return;

        int licznik = 0;

        for (String key : itemConfig.getKeys(false)) {
            ConfigurationSection itemSection = itemConfig.getConfigurationSection(key);
            if (itemSection == null) continue;

            try {
                // Podstawowe dane
                String nazwa = itemSection.getString("nazwa", key);
                List<String> opis = itemSection.getStringList("opis");

                // Material
                String materialStr = itemSection.getString("material", "STONE");
                Material material = Material.valueOf(materialStr.toUpperCase());

                // Rzadkość
                String rarityStr = itemSection.getString("rzadkosc", "POSPOLITY");
                ItemRarity rzadkosc = ItemRarity.valueOf(rarityStr.toUpperCase());

                // Wartość sprzedaży
                double wartoscSprzedazy = itemSection.getDouble("wartosc_sprzedazy", 0.0);

                // Custom Model Data
                int customModelData = itemSection.getInt("custom_model_data", 0);

                // Customowa szansa (OPCJONALNE)
                Double customSzansa = null;
                if (itemSection.contains("custom_szansa")) {
                    customSzansa = itemSection.getDouble("custom_szansa");
                }

                // Stwórz i zarejestruj przedmiot
                Item.ItemBuilder builder = new Item.ItemBuilder(key, nazwa, material, rzadkosc)
                        .opis(opis)
                        .wartoscSprzedazy(wartoscSprzedazy)
                        .customModelData(customModelData);

                // Dodaj customową szansę jeśli jest
                if (customSzansa != null) {
                    builder.customowaSzansa(customSzansa);
                    plugin.getLogger().info("Przedmiot '" + key + "' ma customową szansę: " + customSzansa);
                }

                // Enchantmenty (OPCJONALNE)
                if (itemSection.contains("enchantmenty")) {
                    List<String> enchantList = itemSection.getStringList("enchantmenty");
                    for (String enchantStr : enchantList) {
                        String[] parts = enchantStr.split(":");
                        if (parts.length >= 2) {
                            try {
                                org.bukkit.enchantments.Enchantment enchant =
                                    org.bukkit.enchantments.Enchantment.getByName(parts[0].toUpperCase());
                                int poziom = Integer.parseInt(parts[1]);
                                if (enchant != null) {
                                    builder.enchantment(enchant, poziom);
                                }
                            } catch (Exception e) {
                                plugin.getLogger().warning("Błędny enchantment dla przedmiotu '" + key + "': " + enchantStr);
                            }
                        }
                    }
                }

                Item item = builder.build();

                plugin.getItemRegistry().zarejestrujPrzedmiot(item);
                licznik++;

            } catch (Exception e) {
                plugin.getLogger().warning("Błąd podczas ładowania przedmiotu: " + key);
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Załadowano " + licznik + " przedmiotów!");
    }

    /**
     * Ładuje baity z konfiguracji
     */
    private void zaladujBaity() {
        if (baitConfig == null) return;

        ConfigurationSection baitySection = baitConfig.getConfigurationSection("baity");
        if (baitySection == null) {
            plugin.getLogger().warning("Brak sekcji 'baity' w baits.yml!");
            return;
        }

        int licznik = 0;

        for (String key : baitySection.getKeys(false)) {
            ConfigurationSection baitSection = baitySection.getConfigurationSection(key);
            if (baitSection == null) continue;

            try {
                String nazwa = baitSection.getString("nazwa", key);
                String displayName = baitSection.getString("display_name", nazwa);
                List<String> lore = baitSection.getStringList("lore");

                String materialStr = baitSection.getString("material", "WHEAT");
                Material material = Material.valueOf(materialStr.toUpperCase());
                ItemStack itemStack = new ItemStack(material);

                double szansaBonus = baitSection.getDouble("szansa_bonus", 1.0);
                int maxUzycia = baitSection.getInt("max_uzycia", -1);
                double cena = baitSection.getDouble("cena", 50.0);

                List<String> preferencjeRyb = baitSection.getStringList("preferencje_ryb");
                double bonusDlaPreferencji = baitSection.getDouble("bonus_dla_preferencji", 2.0);

                // Bonusy dla rzadkości (OPCJONALNE)
                Map<FishRarity, Double> bonusyRzadkosci = new HashMap<>();
                if (baitSection.contains("bonus_rzadkosci")) {
                    ConfigurationSection bonusySection = baitSection.getConfigurationSection("bonus_rzadkosci");
                    if (bonusySection != null) {
                        for (String rarityName : bonusySection.getKeys(false)) {
                            try {
                                FishRarity rarity = FishRarity.valueOf(rarityName.toUpperCase());
                                double bonus = bonusySection.getDouble(rarityName);
                                bonusyRzadkosci.put(rarity, bonus);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Nieznana rzadkość w baicie '" + key + "': " + rarityName);
                            }
                        }
                    }
                }

                Bait bait = new Bait.BaitBuilder(key)
                        .nazwa(nazwa)
                        .displayName(displayName)
                        .lore(lore)
                        .itemStack(itemStack)
                        .szansaBonusOgolem(szansaBonus)
                        .preferencjeRyb(preferencjeRyb)
                        .bonusDlaPreferencji(bonusDlaPreferencji)
                        .bonusyRzadkosci(bonusyRzadkosci)
                        .maxUzycia(maxUzycia)
                        .cenaSklep(cena)
                        .build();

                plugin.getBaitRegistry().zarejestrujBait(bait);
                licznik++;

            } catch (Exception e) {
                plugin.getLogger().warning("Błąd podczas ładowania baitu: " + key);
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Załadowano " + licznik + " przynęt!");
    }

    /**
     * Ładuje wędki z konfiguracji
     * @deprecated Wędki są teraz ładowane automatycznie przez RodRegistry z nowego systemu tierów.
     * Ta metoda nie jest już używana i została zdezaktywowana.
     */
    @Deprecated
    private void zaladujWedki() {
        // Metoda została zdezaktywowana - wędki są teraz ładowane przez RodRegistry.zaladujDomyslneWedki()
        // Stary system wędek został zastąpiony nowym systemem tierów (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY)
        plugin.getLogger().info("Pomijam ładowanie wędek z konfiguracji - używam nowego systemu tierów");
    }

    // Gettery

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getFishConfig() {
        return fishConfig;
    }

    public FileConfiguration getBaitConfig() {
        return baitConfig;
    }

    public FileConfiguration getItemConfig() {
        return itemConfig;
    }

    public FileConfiguration getRodConfig() {
        return rodConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
}
