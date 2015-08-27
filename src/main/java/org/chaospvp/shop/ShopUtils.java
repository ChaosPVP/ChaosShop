package org.chaospvp.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShopUtils {
    private static final String SELL_ALL_DISPLAYNAME = ChatColor.GREEN + "" + ChatColor.BOLD + "Sell All";
    private static final String CLOSE_DISPLAYNAME = ChatColor.RED + "" + ChatColor.BOLD + "Close";

    public static ItemStack generateCloseItem() {
        ItemStack closeItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6);
        ItemMeta meta = closeItem.getItemMeta();
        meta.setDisplayName(CLOSE_DISPLAYNAME);
        meta.setLore(Collections.singletonList(
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Closes inventory and gives back items"));
        closeItem.setItemMeta(meta);
        return closeItem;
    }

    public static ItemStack generateSellItem(int price) {
        ItemStack sellAllItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta meta = sellAllItem.getItemMeta();
        meta.setDisplayName(SELL_ALL_DISPLAYNAME);
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "$" + price));
        sellAllItem.setItemMeta(meta);
        return sellAllItem;
    }
}
