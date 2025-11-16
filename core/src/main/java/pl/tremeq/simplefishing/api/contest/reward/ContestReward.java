package pl.tremeq.simplefishing.api.contest.reward;

import org.bukkit.entity.Player;

/**
 * Reprezentuje nagrodę w konkursie
 * Może być pieniędzmi, komendą lub przedmiotem
 *
 * @author tremeq
 * @version 1.0.0
 */
public abstract class ContestReward {

    protected final int miejsce; // Dla którego miejsca nagroda (1, 2, 3...)
    protected final String opis;

    public ContestReward(int miejsce, String opis) {
        this.miejsce = miejsce;
        this.opis = opis;
    }

    /**
     * Daje nagrodę graczowi
     * @param player Gracz otrzymujący nagrodę
     * @return true jeśli udało się dać nagrodę
     */
    public abstract boolean dajNagrode(Player player);

    /**
     * Pobiera opis nagrody
     * @return Opis nagrody
     */
    public String getOpis() {
        return opis;
    }

    /**
     * Pobiera miejsce dla którego jest nagroda
     * @return Numer miejsca
     */
    public int getMiejsce() {
        return miejsce;
    }
}
