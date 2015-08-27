package org.chaospvp.shop;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChaosShop extends JavaPlugin {
    private static ChaosShop instance;
    private ItemPricing pricing;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        pricing = new ItemPricing(getConfig());
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
    }

    public static ChaosShop getInstance() {
        return instance;
    }

    public ItemPricing getPricing() {
        return pricing;
    }

    public void addToBalance(Player p, int toAdd) {

    }
}
