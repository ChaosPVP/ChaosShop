package org.chaospvp.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopListener implements Listener {
    private static final String INVENTORY_PREFIX = ChatColor.DARK_BLUE + "Sell Shop";

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equals("/sell")) {
            event.setCancelled(true);
            Inventory inventory = Bukkit.createInventory(event.getPlayer(),
                    54, INVENTORY_PREFIX);
            inventory.setItem(45, ShopUtils.generateCloseItem());
            inventory.setItem(53, ShopUtils.generateSellItem(0));
            new ShopTask(inventory, event.getPlayer()).runTaskTimer(ChaosShop.getInstance(), 5, 5);
            event.getPlayer().openInventory(inventory);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        final Player p = (Player) e.getWhoClicked();
        String invName = inv.getName();
        if (e.getCurrentItem() != null) {
            if (invName.equals(INVENTORY_PREFIX)) {
                int slot = e.getSlot();
                if (slot == 45) {
                    e.setCancelled(true);
                    p.closeInventory();
                } else if (slot == 53) {
                    e.setCancelled(true);
                    int total = ShopUtils.updateInventory(inv, p);
                    if (total == 0) {
                        p.sendMessage(ChatColor.RED + "No items were sold.");
                    } else {
                        inv.clear();
                        ChaosShop.getInstance().addToBalance(p, total);
                        p.sendMessage(ChatColor.GREEN + "Sold all items for " + ChatColor.YELLOW + "$" + total);
                    }
                    p.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        String invName = inv.getName();
        if (invName.equals(INVENTORY_PREFIX)) {
            boolean didReturn = false;
            for (int i = 0; i < 54; i++) {
                if (i == 45 || i == 53) continue;
                ItemStack is = inv.getItem(i);
                if (is == null || is.getType() == Material.AIR) continue;
                if (ShopUtils.checkInventory(p, is)) {
                    p.getInventory().addItem(is);
                } else {
                    p.getWorld().dropItem(p.getLocation(), is);
                }
                didReturn = true;
            }
            if (didReturn) {
                p.sendMessage(ChatColor.RED + "No items were sold. All items were returned.");
            }
        }
    }
}
