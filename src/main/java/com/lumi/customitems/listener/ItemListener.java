package com.lumi.customitems.listener;

import com.lumi.customitems.CustomItemsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemListener implements Listener {
    private final CustomItemsPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public ItemListener(CustomItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getAction().isRightClick()) return;

        String id = plugin.getItemManager().getId(event.getItem());
        if (id == null) return;

        if (!id.equalsIgnoreCase("ruby_sword")) return;

        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        long until = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (until > now) {
            long left = (until - now + 999) / 1000;
            player.sendMessage(ChatColor.RED + "Cooldown: " + left + "s");
            return;
        }

        cooldowns.put(player.getUniqueId(), now + 10_000);
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 40, 0.5, 0.8, 0.5, 0.03);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1.2f);
        player.sendMessage(ChatColor.RED + "¡La Espada de Rubí libera su poder!");
    }
}
