package org.chaospvp.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChaosShop extends JavaPlugin {
    private static ChaosShop instance;
    private ItemPricing pricing;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        pricing = new ItemPricing(getConfig());
        economy = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
    }

    public static ChaosShop getInstance() {
        return instance;
    }

    public ItemPricing getPricing() {
        return pricing;
    }

    public void addToBalance(Player p, int toAdd) {
        economy.depositPlayer(p, toAdd);
    }
}
