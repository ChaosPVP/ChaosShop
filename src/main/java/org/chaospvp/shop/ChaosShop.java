package org.chaospvp.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            reloadConfig();
            pricing = new ItemPricing(getConfig());
            sender.sendMessage(ChatColor.GREEN + "Reloaded pricing data.");
            return true;
        }
        return false;
    }

    public static ChaosShop getInstance() {
        return instance;
    }

    public static int getPriceFor(ItemStack is) {
        return getInstance().pricing.getPriceFor(is);
    }

    public void addToBalance(Player p, int toAdd) {
        economy.depositPlayer(p, toAdd);
    }
}
