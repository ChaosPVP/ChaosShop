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
        ItemStack closeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
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
            int price = ChaosShop.getPriceFor(is);
            if (price == Integer.MIN_VALUE) {
                leftovers.add(is);
                inv.remove(is);
            } else {
                totalPrice += price;
            }
        }
        inv.setItem(SELL_INDEX, generateSellItem(totalPrice));
        dropLeftovers(leftovers, owner);
        owner.updateInventory();
        return totalPrice;
    }

    public static void dropLeftovers(List<ItemStack> leftovers, Player p) {
        Map<Integer, ItemStack> toDrop = p.getInventory()
                .addItem(leftovers.toArray(new ItemStack[leftovers.size()]));
        for (Map.Entry<Integer, ItemStack> entry : toDrop.entrySet()) {
            p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
        }
    }
}
