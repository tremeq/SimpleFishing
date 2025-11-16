package pl.tremeq.simplefishing;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tremeq.simplefishing.api.SimpleFishingAPI;
import pl.tremeq.simplefishing.api.bait.BaitRegistry;
import pl.tremeq.simplefishing.api.contest.ContestManager;
import pl.tremeq.simplefishing.api.fish.FishRegistry;
import pl.tremeq.simplefishing.api.item.ItemRegistry;
import pl.tremeq.simplefishing.api.gui.GuiManager;
import pl.tremeq.simplefishing.api.integration.SimpleFishingPlaceholder;
import pl.tremeq.simplefishing.api.player.PlayerDataManager;
import pl.tremeq.simplefishing.api.rod.RodManager;
import pl.tremeq.simplefishing.api.shop.ShopManager;
import pl.tremeq.simplefishing.commands.SimpleFishingCommand;
import pl.tremeq.simplefishing.commands.SimpleFishingTabCompleter;
import pl.tremeq.simplefishing.config.ConfigManager;
import pl.tremeq.simplefishing.data.PlayerDataFileManager;
import pl.tremeq.simplefishing.listeners.*;

/**
 * Główna klasa pluginu SimpleFishing
 * Plugin do zaawansowanego łowienia ryb w Minecraft
 *
 * @author tremeq
 * @version 1.0.0
 */
public class SimpleFishingPlugin extends JavaPlugin implements SimpleFishingAPI {

    private static SimpleFishingPlugin instance;

    // Główne komponenty
    private FishRegistry fishRegistry;
    private BaitRegistry baitRegistry;
    private ItemRegistry itemRegistry;
    private PlayerDataManager playerDataManager;
    private ContestManager contestManager;
    private RodManager rodManager;
    private ShopManager shopManager;
    private GuiManager guiManager;
    private ConfigManager configManager;
    private PlayerDataFileManager playerDataFileManager;

    // Integracje
    private Economy economy;
    private boolean vaultEnabled = false;
    private boolean placeholderAPIEnabled = false;
    private boolean citizensEnabled = false;

    @Override
    public void onEnable() {
        instance = this;

        // Logo w konsoli
        getLogger().info("========================================");
        getLogger().info("    SimpleFishing v1.0.0");
        getLogger().info("    Autor: tremeq");
        getLogger().info("========================================");

        try {
            // Inicjalizacja komponentów
            getLogger().info("Inicjalizacja komponentów...");
            inicjalizujKomponenty();

            // Ładowanie konfiguracji
            getLogger().info("Ładowanie konfiguracji...");
            try {
                configManager = new ConfigManager(this);
                configManager.zaladujKonfiguracje();
                getLogger().info("✓ Konfiguracja załadowana");
            } catch (Exception e) {
                getLogger().severe("✗ BŁĄD podczas ładowania konfiguracji: " + e.getMessage());
                e.printStackTrace();
                getLogger().warning("Używam domyślnych wartości konfiguracji");
            }

            // Inicjalizacja menedżera plików danych graczy
            getLogger().info("Inicjalizacja systemu danych graczy...");
            try {
                playerDataFileManager = new PlayerDataFileManager(this);
                getLogger().info("✓ System danych graczy zainicjalizowany");
            } catch (Exception e) {
                getLogger().severe("✗ BŁĄD podczas inicjalizacji systemu danych: " + e.getMessage());
                e.printStackTrace();
            }

            // Rejestracja komend
            getLogger().info("Rejestracja komend...");
            try {
                rejestracjaKomend();
            } catch (Exception e) {
                getLogger().severe("✗ BŁĄD podczas rejestracji komend: " + e.getMessage());
                e.printStackTrace();
            }

            // Rejestracja listenerów
            getLogger().info("Rejestracja listenerów...");
            try {
                rejestracjaListenerow();
            } catch (Exception e) {
                getLogger().severe("✗ BŁĄD podczas rejestracji listenerów: " + e.getMessage());
                e.printStackTrace();
            }

            // Integracje z innymi pluginami
            getLogger().info("Konfiguracja integracji...");
            try {
                setupIntegracje();
            } catch (Exception e) {
                getLogger().severe("✗ BŁĄD podczas konfiguracji integracji: " + e.getMessage());
                e.printStackTrace();
            }

            // Auto-save danych graczy co 5 minut
            getLogger().info("Konfiguracja auto-save...");
            try {
                setupAutoSave();
                getLogger().info("✓ Auto-save skonfigurowany (co 5 minut)");
            } catch (Exception e) {
                getLogger().severe("✗ BŁĄD podczas konfiguracji auto-save: " + e.getMessage());
                e.printStackTrace();
            }

            getLogger().info("========================================");
            getLogger().info("SimpleFishing został włączony pomyślnie!");
            getLogger().info("========================================");

        } catch (Exception e) {
            getLogger().severe("========================================");
            getLogger().severe("KRYTYCZNY BŁĄD podczas włączania pluginu!");
            getLogger().severe("Plugin może nie działać poprawnie!");
            getLogger().severe("Błąd: " + e.getMessage());
            getLogger().severe("========================================");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Zapisz wszystkie dane graczy przed wyłączeniem
        if (playerDataManager != null && playerDataFileManager != null) {
            getLogger().info("Zapisywanie danych wszystkich graczy...");
            int savedCount = 0;

            for (var entry : playerDataManager.getAllCachedData().entrySet()) {
                try {
                    playerDataFileManager.savePlayerData(entry.getValue());
                    savedCount++;
                } catch (Exception e) {
                    getLogger().severe("Błąd podczas zapisywania danych gracza " + entry.getKey() + ": " + e.getMessage());
                }
            }

            getLogger().info("Zapisano dane " + savedCount + " graczy.");
        }

        // Zamknięcie wszystkich GUI
        if (guiManager != null) {
            guiManager.zamknijWszystkie();
        }

        // Zakończenie aktywnych konkursów
        if (contestManager != null && contestManager.czyJestAktywnyKonkurs()) {
            contestManager.zakonczAktywnyKonkurs();
        }

        getLogger().info("SimpleFishing został wyłączony!");
    }

    /**
     * Inicjalizuje wszystkie komponenty pluginu
     */
    private void inicjalizujKomponenty() {
        int successCount = 0;
        int totalComponents = 8;

        try {
            this.fishRegistry = new FishRegistry();
            getLogger().info("✓ FishRegistry załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji FishRegistry: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.baitRegistry = new BaitRegistry();
            getLogger().info("✓ BaitRegistry załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji BaitRegistry: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.itemRegistry = new ItemRegistry();
            getLogger().info("✓ ItemRegistry załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji ItemRegistry: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.playerDataManager = new PlayerDataManager();
            getLogger().info("✓ PlayerDataManager załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji PlayerDataManager: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.contestManager = new ContestManager();
            getLogger().info("✓ ContestManager załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji ContestManager: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.rodManager = new RodManager(this);
            getLogger().info("✓ RodManager załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji RodManager: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.shopManager = new ShopManager();
            getLogger().info("✓ ShopManager załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji ShopManager: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            this.guiManager = new GuiManager();
            getLogger().info("✓ GuiManager załadowany");
            successCount++;
        } catch (Exception e) {
            getLogger().severe("✗ BŁĄD podczas inicjalizacji GuiManager: " + e.getMessage());
            e.printStackTrace();
        }

        getLogger().info("========================================");
        getLogger().info("Komponenty: " + successCount + "/" + totalComponents + " załadowane pomyślnie");

        if (successCount < totalComponents) {
            getLogger().warning("UWAGA: Niektóre komponenty nie zostały załadowane!");
            getLogger().warning("Plugin może działać niestabilnie!");
        } else {
            getLogger().info("Wszystkie komponenty załadowane poprawnie!");
        }
        getLogger().info("========================================");
    }

    /**
     * Rejestruje komendy pluginu
     */
    private void rejestracjaKomend() {
        getCommand("simplefishing").setExecutor(new SimpleFishingCommand(this));
        getCommand("simplefishing").setTabCompleter(new pl.tremeq.simplefishing.commands.SimpleFishingTabCompleter(this));
        getLogger().info("Komendy i tab completion zarejestrowane!");
    }

    /**
     * Rejestruje listenery eventów
     */
    private void rejestracjaListenerow() {
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("Listenery zarejestrowane!");
    }

    /**
     * Konfiguruje integracje z innymi pluginami
     */
    private void setupIntegracje() {
        // Vault
        if (setupVault()) {
            vaultEnabled = true;
            getLogger().info("Integracja z Vault włączona!");
        } else {
            getLogger().warning("Vault nie zostało znalezione! Funkcje ekonomii będą wyłączone.");
        }

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SimpleFishingPlaceholder(this).register();
            placeholderAPIEnabled = true;
            getLogger().info("Integracja z PlaceholderAPI włączona!");
        } else {
            getLogger().warning("PlaceholderAPI nie zostało znalezione!");
        }

        // Citizens
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            // Rejestracja trait dla Citizens
            citizensEnabled = true;
            getServer().getPluginManager().registerEvents(new CitizensListener(this), this);
            getLogger().info("Integracja z Citizens włączona!");
        } else {
            getLogger().warning("Citizens nie zostało znalezione!");
        }
    }

    /**
     * Konfiguruje auto-save danych graczy
     * Zapisuje dane wszystkich online graczy co 5 minut
     */
    private void setupAutoSave() {
        // Auto-save co 5 minut (6000 ticków)
        long intervalTicks = 6000L;

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (playerDataManager == null || playerDataFileManager == null) {
                return;
            }

            int savedCount = 0;
            for (var entry : playerDataManager.getAllCachedData().entrySet()) {
                try {
                    playerDataFileManager.savePlayerData(entry.getValue());
                    savedCount++;
                } catch (Exception e) {
                    getLogger().warning("Auto-save: Błąd podczas zapisywania danych gracza " + entry.getKey() + ": " + e.getMessage());
                }
            }

            if (savedCount > 0) {
                getLogger().info("Auto-save: Zapisano dane " + savedCount + " graczy.");
            }
        }, intervalTicks, intervalTicks);
    }

    /**
     * Konfiguruje Vault
     * @return true jeśli sukces
     */
    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    // Implementacja SimpleFishingAPI

    @Override
    public FishRegistry getFishRegistry() {
        return fishRegistry;
    }

    @Override
    public BaitRegistry getBaitRegistry() {
        return baitRegistry;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    @Override
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    @Override
    public ContestManager getContestManager() {
        return contestManager;
    }

    @Override
    public RodManager getRodManager() {
        return rodManager;
    }

    @Override
    public ShopManager getShopManager() {
        return shopManager;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    // Gettery

    public static SimpleFishingPlugin getInstance() {
        return instance;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    public boolean isCitizensEnabled() {
        return citizensEnabled;
    }

    public PlayerDataFileManager getPlayerDataFileManager() {
        return playerDataFileManager;
    }
}
