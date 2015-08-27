package org.chaospvp.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class ShopTask extends BukkitRunnable {
    private final Inventory inv;
    private final Player viewer;

    private ItemStack[] prevContents;

    public ShopTask(Inventory inv, Player viewer) {
        this.inv = inv;
        this.viewer = viewer;
    }

    @Override
    public void run() {
        if (!inv.getViewers().contains(viewer)) {
            cancel();
            return;
        }
        ItemStack[] currContents = inv.getContents();
        if (prevContents == null || !Arrays.equals(currContents, prevContents)) {
            prevContents = Arrays.copyOf(currContents, currContents.length);
            for (int i = 0; i < 54; i++) {
                if (i == 45 || i == 53) {
                    continue;
                }

            }
        }
    }
}
