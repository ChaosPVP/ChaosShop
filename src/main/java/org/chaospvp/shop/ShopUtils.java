package org.chaospvp.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShopUtils {
    private static final String SELL_ALL_DISPLAYNAME = ChatColor.GREEN + "" + ChatColor.BOLD + "Sell All";
    private static final String CLOSE_DISPLAYNAME = ChatColor.RED + "" + ChatColor.BOLD + "Close";

    public static final int CLOSE_INDEX = 45;
    public static final int SELL_INDEX = 53;

    public static ItemStack generateCloseItem() {
        ItemStack closeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6);
        ItemMeta meta = closeItem.getItemMeta();
        meta.setDisplayName(CLOSE_DISPLAYNAME);
        meta.setLore(Collections.singletonList(
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Returns items back to your inventory"));
        closeItem.setItemMeta(meta);
        return closeItem;
    }

    public static ItemStack generateSellItem(int price) {
        ItemStack sellAllItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta meta = sellAllItem.getItemMeta();
        meta.setDisplayName(SELL_ALL_DISPLAYNAME);
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Value: "
                + ChatColor.YELLOW + "" + ChatColor.ITALIC + "$" + price));
        sellAllItem.setItemMeta(meta);
        return sellAllItem;
    }

    public static int updateInventory(Inventory inv, Player owner) {
        int totalPrice = 0;
        List<ItemStack> leftovers = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            if (i == CLOSE_INDEX || i == SELL_INDEX) continue;
            ItemStack is = inv.getItem(i);
            if (is == null || is.getType() == Material.AIR) continue;
            int price = ChaosShop.getInstance().getPricing().getPriceFor(is);
            if (price == Integer.MIN_VALUE) {
                leftovers.add(is);
                inv.remove(is);
            } else {
                totalPrice += price;
            }
        }
        inv.setItem(SELL_INDEX, generateSellItem(totalPrice));
        owner.updateInventory();
        for (ItemStack leftover : leftovers) {
            if (checkInventory(owner, leftover)) {
                owner.getInventory().addItem(leftover);
            } else {
                owner.getWorld().dropItem(owner.getLocation(), leftover);
            }
        }

        return totalPrice;
    }

    public static boolean checkInventory(Player p, ItemStack item) {
        if (p.getInventory().firstEmpty() >= 0 && item.getAmount() <= item.getMaxStackSize()) {
            return true;
        }
        Map<Integer, ? extends ItemStack> items = p.getInventory().all(item.getType());
        int amount = item.getAmount();
        for (ItemStack i : items.values()) {
            amount -= i.getMaxStackSize() - i.getAmount();
        }
        return amount <= 0; // more than 0 means there are items that can't be placed
    }

}
