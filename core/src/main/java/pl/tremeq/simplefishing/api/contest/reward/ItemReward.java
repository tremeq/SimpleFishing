package pl.tremeq.simplefishing.api.contest.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Nagroda w postaci przedmiotu
 *
 * @author tremeq
 * @version 1.0.0
 */
public class ItemReward extends ContestReward {

    private final ItemStack item;

    public ItemReward(int miejsce, ItemStack item) {
        super(miejsce, item.getType().name() + " x" + item.getAmount());
        this.item = item;
    }

    @Override
    public boolean dajNagrode(Player player) {
        player.getInventory().addItem(item.clone());
        return true;
    }

    public ItemStack getItem() {
        return item.clone();
    }
}
