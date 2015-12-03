package org.chaospvp.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ShopListener implements Listener {
    private static final String INVENTORY_PREFIX = "Sell Items (shift-click to add)";
    private static String CHAT_PREFIX;
    private Set<UUID> skipPlayers = new HashSet<>();

    public ShopListener() {
        CHAT_PREFIX = ChatColor.translateAlternateColorCodes('&',
                ChaosShop.getInstance().getConfig().getString("prefix", "&8[&4&lChaos&f&lShop&8]") + " &r");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
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
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem != null) {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getSlot() != e.getRawSlot()) {
                InventoryView openInventory = p.getOpenInventory();
                if (openInventory != null) {
                    Inventory topInventory = openInventory.getTopInventory();
                    if (topInventory != null && topInventory.getName().equals(INVENTORY_PREFIX)) {
                        if (ChaosShop.getPriceFor(currentItem) == Integer.MIN_VALUE) {
                            e.setCancelled(true);
                        }
                    }
                }
            } else if (invName.equals(INVENTORY_PREFIX)) {
                int slot = e.getSlot();
                boolean shouldClose = false;
                if (slot == ShopUtils.CLOSE_INDEX) {
                    shouldClose = true;
                    e.setCancelled(true);
                } else if (slot == ShopUtils.SELL_INDEX) {
                    shouldClose = true;
                    e.setCancelled(true);
                    if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR &&
                            ChaosShop.getPriceFor(e.getCursor()) != Integer.MIN_VALUE) {
                        if (e.getClick() == ClickType.RIGHT) {
                            ItemStack realItem = e.getCursor().clone();
                            inv.addItem(realItem);
                            e.getCursor().setAmount(1);
                        } else {
                            inv.addItem(e.getCursor());
                        }
                        e.setCancelled(false);
                        e.setCurrentItem(null);
                    }
                    int total = ShopUtils.updateInventory(inv, p);
                    if (total == 0) {
                        p.sendMessage(CHAT_PREFIX + ChatColor.YELLOW + "No items were sold.");
                    } else {
                        inv.clear();
                        ChaosShop.getInstance().addToBalance(p, total);
                        p.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Sold all items for " +
                                ChatColor.GOLD + "" + ChatColor.BOLD + "$" + total);
                    }
                }
                if (shouldClose) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.closeInventory();
                        }
                    }.runTask(ChaosShop.getInstance());
                }
            }
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        final UUID uuid = p.getUniqueId();
        if (skipPlayers.contains(uuid)) {
            return;
        }
        Inventory inv = e.getInventory();
        String invName = inv.getName();
        if (invName.equals(INVENTORY_PREFIX)) {
            skipPlayers.add(uuid);
            new BukkitRunnable() {
                @Override
                public void run() {
                    skipPlayers.remove(uuid);
                }
            }.runTask(ChaosShop.getInstance());
            boolean didReturn = false;
            List<ItemStack> leftovers = new ArrayList<>();
            for (int i = 0; i < 54; i++) {
                if (i == ShopUtils.CLOSE_INDEX || i == ShopUtils.SELL_INDEX) continue;
                ItemStack is = inv.getItem(i);
                if (is == null || is.getType() == Material.AIR) continue;
                leftovers.add(is);
                didReturn = true;
            }
            ShopUtils.dropLeftovers(leftovers, p);
            if (didReturn) {
                p.sendMessage(CHAT_PREFIX + ChatColor.YELLOW + "No items were sold. All items were returned.");
            }
        }
    }
}
