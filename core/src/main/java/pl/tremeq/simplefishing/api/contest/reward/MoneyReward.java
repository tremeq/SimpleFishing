package pl.tremeq.simplefishing.api.contest.reward;

import org.bukkit.entity.Player;

/**
 * Nagroda pieniężna (wymaga Vault)
 *
 * @author tremeq
 * @version 1.0.0
 */
public class MoneyReward extends ContestReward {

    private final double kwota;

    public MoneyReward(int miejsce, double kwota) {
        super(miejsce, kwota + " monet");
        this.kwota = kwota;
    }

    @Override
    public boolean dajNagrode(Player player) {
        // Implementacja z Vault będzie w głównym pluginie
        return false;
    }

    public double getKwota() {
        return kwota;
    }
}
