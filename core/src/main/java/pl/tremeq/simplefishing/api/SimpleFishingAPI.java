package pl.tremeq.simplefishing.api;

import pl.tremeq.simplefishing.api.fish.Fish;
import pl.tremeq.simplefishing.api.fish.FishRegistry;
import pl.tremeq.simplefishing.api.bait.Bait;
import pl.tremeq.simplefishing.api.bait.BaitRegistry;
import pl.tremeq.simplefishing.api.item.ItemRegistry;
import pl.tremeq.simplefishing.api.contest.ContestManager;
import pl.tremeq.simplefishing.api.player.PlayerDataManager;
import pl.tremeq.simplefishing.api.rod.RodManager;
import pl.tremeq.simplefishing.api.rod.RodRegistry;
import pl.tremeq.simplefishing.api.shop.ShopManager;

/**
 * Główne API pluginu SimpleFishing
 * Umożliwia dostęp do wszystkich systemów pluginu
 *
 * @author tremeq
 * @version 1.0.0
 */
public interface SimpleFishingAPI {

    /**
     * Pobiera rejestr wszystkich ryb
     * @return FishRegistry zawierający wszystkie zarejestrowane ryby
     */
    FishRegistry getFishRegistry();

    /**
     * Pobiera rejestr wszystkich przynęt (baitów)
     * @return BaitRegistry zawierający wszystkie zarejestrowane przynęty
     */
    BaitRegistry getBaitRegistry();

    /**
     * Pobiera rejestr wszystkich przedmiotów do wyłowienia
     * @return ItemRegistry zawierający wszystkie zarejestrowane przedmioty
     */
    ItemRegistry getItemRegistry();

    /**
     * Pobiera menedżera danych graczy
     * @return PlayerDataManager zarządzający statystykami graczy
     */
    PlayerDataManager getPlayerDataManager();

    /**
     * Pobiera menedżera konkursów łowienia
     * @return ContestManager zarządzający aktywny konkursami
     */
    ContestManager getContestManager();

    /**
     * Pobiera menedżera wędek
     * @return RodManager zarządzający customowymi wędkami
     */
    RodManager getRodManager();

    /**
     * Pobiera rejestr wędek
     * @return RodRegistry zawierający wszystkie dostępne wędki
     */
    RodRegistry getRodRegistry();

    /**
     * Pobiera menedżera sklepu
     * @return ShopManager zarządzający sprzedażą ryb
     */
    ShopManager getShopManager();

    /**
     * Pobiera wersję API
     * @return String z wersją API
     */
    String getVersion();

}
