package pl.tremeq.simplefishing.api.contest.reward;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Nagroda w postaci wykonania komendy
 *
 * @author tremeq
 * @version 1.0.0
 */
public class CommandReward extends ContestReward {

    private final String komenda;

    public CommandReward(int miejsce, String komenda, String opis) {
        super(miejsce, opis);
        this.komenda = komenda;
    }

    @Override
    public boolean dajNagrode(Player player) {
        String cmd = komenda.replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        return true;
    }

    public String getKomenda() {
        return komenda;
    }
}
