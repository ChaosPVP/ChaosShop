package org.chaospvp.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopTask extends BukkitRunnable {
    private final Inventory inv;
    private final Player owner;

    public ShopTask(Inventory inv, Player owner) {
        this.inv = inv;
        this.owner = owner;
    }

    @Override
    public void run() {
        if (!inv.getViewers().contains(owner)) {
            cancel();
            return;
        }
        ShopUtils.updateInventory(inv, owner);
    }
}
