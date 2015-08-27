package org.chaospvp.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

public class ShopInventoryHandler implements Listener {
    private static final String INVENTORY_PREFIX = ChatColor.DARK_BLUE + "Sell Shop";
    private static final String SELL_ALL_DISPLAYNAME = ChatColor.GREEN + "" + ChatColor.BOLD + "Sell All";

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        System.out.println(message);
        if (event.getMessage().contains("fake-shop")) {
            Inventory inventory = Bukkit.createInventory(event.getPlayer(),
                    54, INVENTORY_PREFIX);
            for (int i = 45; i < 53; i++) {
                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
            }
            inventory.setItem(53, generateSellItem());
            event.getPlayer().openInventory(inventory);
        }
    }

    private ItemStack generateSellItem() {
        ItemStack sellAllItem = new ItemStack(Material.PAPER);
            ItemMeta meta = sellAllItem.getItemMeta();
        meta.setDisplayName(SELL_ALL_DISPLAYNAME);
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "$4800"));
        sellAllItem.setItemMeta(meta);
        return sellAllItem;
    }

    private boolean isSellAllItem(ItemStack is) {
        if (is == null || is.getType() != Material.PAPER || is.getAmount() != 1 || !is.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = is.getItemMeta();
        return !(!meta.hasDisplayName() || !meta.hasLore())
                && meta.getDisplayName().endsWith(SELL_ALL_DISPLAYNAME)
                && meta.getLore().get(0).startsWith(ChatColor.GRAY + "$");
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        final Player p = (Player) e.getWhoClicked();
        String invName = inv.getName();
        if (e.getCurrentItem() != null) {
            ItemStack curr = e.getCurrentItem();
            if (ChatColor.stripColor(invName).startsWith(INVENTORY_PREFIX)) {
                if (e.getClick().equals(ClickType.RIGHT)) {
                    e.setCancelled(true);
                    return;
                }

                if (isSellAllItem(curr)) {
                    e.setCancelled(true);
                    int leftover = 0;
                    long balance = 0L;
                    int total = 0;
                    int success = 0;
                    List<ItemStack> leftOvers = new ArrayList<>();
                    for (ItemStack is : inv.getContents()) {
                        if (is != null && !is.getType().equals(Material.AIR)
                                && !isSellAllItem(is)) {
                            int price = 420; // get price here

                            if (price == -1) {
                                leftover += is.getAmount();
                                total += is.getAmount();
                                leftOvers.add(is);
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
                        for (ItemStack i : leftOvers) {
                            if (i != null) {
                                p.getInventory().addItem(i);
                            }
                        }
                    }

                    if (total == 0) {
                        p.sendMessage(ChatColor.RED + "Place items in before clicking to sell them!");
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

    private int getBalance(Iterable<ItemStack> contents) {
        return 0;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        String invName = inv.getName();
        if (ChatColor.stripColor(invName).startsWith(INVENTORY_PREFIX)) {
            boolean hasLeftover = false;
            for (ItemStack is : inv.getContents()) {
                if (is != null && !is.getType().equals(Material.AIR) && !isSellAllItem(is)) {
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
