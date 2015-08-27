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
            event.getPlayer().openInventory(inventory);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        final Player p = (Player) e.getWhoClicked();
        String invName = inv.getName();
        if (e.getCurrentItem() != null) {
            if (invName.startsWith(INVENTORY_PREFIX)) {
                int slot = e.getSlot();
                if (slot == 45) {
                    e.setCancelled(true);
                    p.closeInventory();
                } else if (slot == 53) {
                    e.setCancelled(true);
                    int leftover = 0;
                    long balance = 0L;
                    int total = 0;
                    int success = 0;
                    List<ItemStack> leftovers = new ArrayList<>();
                    for (int i = 0; i < 54; i++) {
                        if (i == 45 || i == 53) {
                            continue;
                        }
                        ItemStack is = inv.getItem(i);
                        if (is != null && !is.getType().equals(Material.AIR)) {
                            int price = 420; // get price here

                            if (price == -1) {
                                leftover += is.getAmount();
                                total += is.getAmount();
                                leftovers.add(is);
                                inv.remove(is);
                            } else {
                                int add = price * is.getAmount();

                                balance += (long) add;
                                total += is.getAmount();
                                success += is.getAmount();
                                inv.remove(is);
                            }
                        }
                    }

                    if (leftover > 0) {
                        p.sendMessage(ChatColor.GOLD + String.valueOf(leftover)
                                + ChatColor.RED + " items could not be sold they were returned to your inventory!");
                        for (ItemStack i : leftovers) {
                            if (i != null) {
                                p.getInventory().addItem(i);
                            }
                        }
                    }

                    if (total == 0) {
                        p.sendMessage(ChatColor.RED + "Inventory was empty, no items were sold.");
                    }

                    if (success > 0) {
                        p.sendMessage(ChatColor.GOLD + String.valueOf(success) + ChatColor.GREEN
                                + " items sold for " + ChatColor.GOLD + balance + ChatColor.GREEN + "$");
                    }

                    // TODO: give player money
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
        if (invName.startsWith(INVENTORY_PREFIX)) {
            boolean hasLeftover = false;
            for (int i = 0; i < 54; i++) {
                if (i == 45 || i == 53) {
                    continue;
                }
                ItemStack is = inv.getItem(i);
                if (is != null && !is.getType().equals(Material.AIR)) {
                    p.getInventory().addItem(is);
                    hasLeftover = true;
                }
            }
            if (hasLeftover) {
                p.sendMessage(ChatColor.RED + "You left some items in the shop, they have been returned to your inventory!");
            }
        }
    }
}
