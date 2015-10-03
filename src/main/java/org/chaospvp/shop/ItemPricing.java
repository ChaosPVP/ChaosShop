package org.chaospvp.shop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemPricing {
    private Map<Material, Double> typePricing;

    public ItemPricing(FileConfiguration config) {
        typePricing = new HashMap<>();
        ConfigurationSection cs = config.getConfigurationSection("prices");
        for (String key : cs.getKeys(false)) {
            try {
                Material type = Material.valueOf(key.toUpperCase());
                typePricing.put(type, cs.getDouble(key));
            } catch (Throwable t) {
                ChaosShop.getInstance().getLogger().severe("Error loading material: " + key);
            }
        }
    }

    public int getPriceFor(ItemStack is) {
        Material type = is.getType();
        if (!typePricing.containsKey(type)) {
            return Integer.MIN_VALUE;
        }
        return (int) Math.floor(typePricing.get(type) * (double) is.getAmount());
    }
}
