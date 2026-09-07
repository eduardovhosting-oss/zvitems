package com.lumi.customitems.item;

import com.lumi.customitems.CustomItemsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class ItemManager {
    private final CustomItemsPlugin plugin;
    private final Map<String, CustomItem> items = new HashMap<>();
    private final NamespacedKey idKey;

    public ItemManager(CustomItemsPlugin plugin) {
        this.plugin = plugin;
        this.idKey = new NamespacedKey(plugin, "custom_item_id");
    }

    public void loadItems() {
        items.clear();
        File dir = new File(plugin.getDataFolder(), "items");
        if (!dir.exists()) dir.mkdirs();

        if (dir.listFiles() == null) return;

        for (File file : Objects.requireNonNull(dir.listFiles((d, n) -> n.endsWith(".yml")))) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            String id = yml.getString("id");
            if (id == null || id.isBlank()) continue;

            Map<String, Double> attributes = new HashMap<>();
            if (yml.isConfigurationSection("attributes")) {
                for (String key : yml.getConfigurationSection("attributes").getKeys(false)) {
                    attributes.put(key, yml.getDouble("attributes." + key));
                }
            }

            Map<String, Integer> enchants = new HashMap<>();
            if (yml.isConfigurationSection("enchants")) {
                for (String key : yml.getConfigurationSection("enchants").getKeys(false)) {
                    enchants.put(key, yml.getInt("enchants." + key));
                }
            }

            Map<String, Object> abilities = yml.isConfigurationSection("abilities")
                    ? yml.getConfigurationSection("abilities").getValues(true)
                    : Map.of();

            items.put(id.toLowerCase(Locale.ROOT), new CustomItem(
                    id.toLowerCase(Locale.ROOT),
                    yml.getString("material", "STONE"),
                    color(yml.getString("name", id)),
                    yml.getStringList("lore").stream().map(this::color).toList(),
                    yml.getInt("custom-model-data", 0),
                    yml.getBoolean("unbreakable", false),
                    yml.getBoolean("glow", false),
                    attributes,
                    enchants,
                    abilities
            ));
        }
    }

    public ItemStack create(String id, int amount) {
        CustomItem item = items.get(id.toLowerCase(Locale.ROOT));
        if (item == null) return null;

        Material material = Material.matchMaterial(item.material());
        if (material == null) material = Material.STONE;

        ItemStack stack = new ItemStack(material, Math.max(1, Math.min(amount, 64)));
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(item.name());
        meta.setLore(item.lore());
        meta.setUnbreakable(item.unbreakable());

        if (item.customModelData() > 0) {
            meta.setCustomModelData(item.customModelData());
        }

        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, item.id());

        if (item.glow()) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        for (Map.Entry<String, Integer> e : item.enchants().entrySet()) {
            org.bukkit.enchantments.Enchantment ench =
                    org.bukkit.Registry.ENCHANTMENT.get(org.bukkit.NamespacedKey.minecraft(e.getKey().toLowerCase(Locale.ROOT)));
            if (ench != null) meta.addEnchant(ench, e.getValue(), true);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public String getId(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        return stack.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
    }

    public Collection<CustomItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public int size() {
        return items.size();
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}

