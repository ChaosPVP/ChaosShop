package org.chaospvp.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {
    private static final String INVENTORY_PREFIX = ChatColor.DARK_BLUE + "Sell Items";
    private static final String CHAT_PREFIX = ChatColor.translateAlternateColorCodes('&', "&8[&f&lChaos&4&lShop&8] &r");

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if (msg.equals("/sell") || msg.equals("/sellall")) {
            event.setCancelled(true);
            Inventory inventory = Bukkit.createInventory(event.getPlayer(),
                    54, INVENTORY_PREFIX);
            inventory.setItem(ShopUtils.CLOSE_INDEX, ShopUtils.generateCloseItem());
            inventory.setItem(ShopUtils.SELL_INDEX, ShopUtils.generateSellItem(0));
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
                if (slot == ShopUtils.CLOSE_INDEX) {
                    e.setCancelled(true);
                    p.closeInventory();
                } else if (slot == ShopUtils.SELL_INDEX) {
                    e.setCancelled(true);
                    int total = ShopUtils.updateInventory(inv, p);
                    if (total == 0) {
                        p.sendMessage(CHAT_PREFIX + ChatColor.YELLOW + "No items were sold.");
                    } else {
                        inv.clear();
                        ChaosShop.getInstance().addToBalance(p, total);
                        p.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Sold all items for " +
                                ChatColor.GOLD + "" + ChatColor.BOLD + "$" + total);
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
                if (i == ShopUtils.CLOSE_INDEX || i == ShopUtils.SELL_INDEX) continue;
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
                p.sendMessage(CHAT_PREFIX + ChatColor.YELLOW + "No items were sold. All items were returned.");
            }
        }
    }
}
