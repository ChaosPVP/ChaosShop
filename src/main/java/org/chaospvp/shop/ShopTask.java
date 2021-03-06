package org.chaospvp.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class ShopTask extends BukkitRunnable {
    private final Inventory inv;
    private final Player owner;

    private ItemStack[] prevContents;

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
        ItemStack[] currContents = inv.getContents();
        if (prevContents == null || !Arrays.equals(currContents, prevContents)) {
            ShopUtils.updateInventory(inv, owner);
            prevContents = Arrays.copyOf(inv.getContents(), inv.getContents().length);
        }
    }
}
