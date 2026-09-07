package com.lumi.customitems;

import com.lumi.customitems.command.CustomItemsCommand;
import com.lumi.customitems.item.ItemManager;
import com.lumi.customitems.listener.ItemListener;
import com.lumi.customitems.pack.ResourcePackGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomItemsPlugin extends JavaPlugin {
    private ItemManager itemManager;
    private ResourcePackGenerator packGenerator;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        itemManager = new ItemManager(this);
        itemManager.loadItems();

        packGenerator = new ResourcePackGenerator(this);

        CustomItemsCommand command = new CustomItemsCommand(this);
        getCommand("customitems").setExecutor(command);
        getCommand("customitems").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new ItemListener(this), this);

        if (getConfig().getBoolean("resource-pack.generate-on-start", true)) {
            packGenerator.generate();
        }

        getLogger().info("CustomItems habilitado con " + itemManager.size() + " items.");
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ResourcePackGenerator getPackGenerator() {
        return packGenerator;
    }

    public void reloadPlugin() {
        reloadConfig();
        itemManager.loadItems();
        packGenerator.generate();
    }
}

