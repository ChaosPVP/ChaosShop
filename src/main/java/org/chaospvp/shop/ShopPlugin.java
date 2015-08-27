package org.chaospvp.shop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ShopPlugin extends JavaPlugin {
    private Map<Material, Integer> typePricing;
    private static ShopPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        // saveDefaultConfig();
        // loadPricingConfig();
        getServer().getPluginManager().registerEvents(new ShopInventoryHandler(), this);
    }

    private void loadPricingConfig() {
        typePricing = new HashMap<>();
        FileConfiguration config = getConfig();
        ConfigurationSection cs = config.getConfigurationSection("prices");
        for (String key : cs.getKeys(false)) {
            try {
                Material type = Material.valueOf(key);
                typePricing.put(type, cs.getInt(key));
            } catch (Throwable t) {
                getLogger().info("Error loading material: " + key);
            }
        }
    }

    public static ShopPlugin getInstance() {
        return instance;
    }

    public Map<Material, Integer> getTypePricing() {
        return typePricing;
    }
}
