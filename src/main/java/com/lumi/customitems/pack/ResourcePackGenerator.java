package com.lumi.customitems.pack;

import com.lumi.customitems.CustomItemsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ResourcePackGenerator {
    private final CustomItemsPlugin plugin;

    public ResourcePackGenerator(CustomItemsPlugin plugin) {
        this.plugin = plugin;
    }

    public void generate() {
        File base = new File(plugin.getDataFolder(), "resourcepack");
        File assets = new File(base, "assets/" + plugin.getConfig().getString("resource-pack.namespace", "customitems"));
        File textures = new File(assets, "textures/item");
        textures.mkdirs();

        try {
            File itemsDir = new File(plugin.getDataFolder(), "items");
            File[] configs = itemsDir.listFiles((d, n) -> n.endsWith(".yml"));
            if (configs == null) return;

            Map<String, Object> overrides = new LinkedHashMap<>();
            for (File file : configs) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                int cmd = yml.getInt("custom-model-data", 0);
                String id = yml.getString("id");
                if (cmd > 0 && id != null) overrides.put(String.valueOf(cmd), "customitems:item/" + id);
            }

            File models = new File(assets, "models/item");
            models.mkdirs();

            for (Map.Entry<String, Object> e : overrides.entrySet()) {
                String cmd = e.getKey();
                String modelPath = (String)e.getValue();
                String id = modelPath.substring(modelPath.lastIndexOf('/') + 1);

                String json = "{\n" +
                        "  \"parent\": \"minecraft:item/handheld\",\n" +
                        "  \"textures\": { \"layer0\": \"" + modelPath + "\" }\n" +
                        "}\n";
                Files.writeString(new File(models, id + ".json").toPath(), json);
            }

            File readme = new File(base, "README.txt");
            Files.writeString(readme.toPath(),
                    "CustomItems resource pack generado.\n" +
                    "Coloca tus PNG en assets/" +
                    plugin.getConfig().getString("resource-pack.namespace", "customitems") +
                    "/textures/item/ y genera de nuevo con /customitems pack.\n");

        } catch (IOException ex) {
            plugin.getLogger().severe("No se pudo generar el resource pack: " + ex.getMessage());
        }
    }
}
