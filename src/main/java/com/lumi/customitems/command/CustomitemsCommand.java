package com.lumi.customitems.command;

import com.lumi.customitems.CustomItemsPlugin;
import com.lumi.customitems.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomItemsCommand implements CommandExecutor, TabCompleter {
    private final CustomItemsPlugin plugin;

    public CustomItemsCommand(CustomItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "give" -> give(sender, args);
            case "list" -> list(sender);
            case "reload" -> {
                if (!sender.hasPermission("customitems.admin")) {
                    sender.sendMessage(ChatColor.RED + "Sin permiso.");
                    return true;
                }
                plugin.reloadPlugin();
                sender.sendMessage(ChatColor.GREEN + "CustomItems recargado.");
            }
            case "pack" -> {
                if (!sender.hasPermission("customitems.admin")) {
                    sender.sendMessage(ChatColor.RED + "Sin permiso.");
                    return true;
                }
                plugin.getPackGenerator().generate();
                sender.sendMessage(ChatColor.GREEN + "Resource pack generado.");
            }
            case "editor" -> editor(sender);
            default -> help(sender);
        }
        return true;
    }

    private void give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("customitems.give")) {
            sender.sendMessage(ChatColor.RED + "Sin permiso.");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "/customitems give <jugador> <id> [cantidad]");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
            return;
        }

        String id = args.length >= 3 ? args[2] : "";
        int amount = args.length >= 4 ? parseAmount(args[3]) : 1;

        ItemStack stack = plugin.getItemManager().create(id, amount);
        if (stack == null) {
            sender.sendMessage(ChatColor.RED + "Item desconocido: " + id);
            return;
        }

        target.getInventory().addItem(stack);
        sender.sendMessage(ChatColor.GREEN + "Entregado " + id + " x" + amount + " a " + target.getName());
    }

    private int parseAmount(String s) {
        try { return Math.max(1, Math.min(64, Integer.parseInt(s))); }
        catch (NumberFormatException e) { return 1; }
    }

    private void list(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Items registrados:");
        for (CustomItem item : plugin.getItemManager().getItems()) {
            sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + item.id());
        }
    }

    private void editor(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return;
        }
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Editor visual: base preparada. Usa /customitems list para ver IDs.");
        player.sendMessage(ChatColor.GRAY + "El editor GUI completo se puede ampliar sobre este núcleo.");
    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "CustomItems");
        sender.sendMessage(ChatColor.GRAY + "/customitems give <jugador> <id> [cantidad]");
        sender.sendMessage(ChatColor.GRAY + "/customitems list");
        sender.sendMessage(ChatColor.GRAY + "/customitems reload");
        sender.sendMessage(ChatColor.GRAY + "/customitems pack");
        sender.sendMessage(ChatColor.GRAY + "/customitems editor");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("give", "list", "reload", "pack", "editor");
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return plugin.getItemManager().getItems().stream().map(CustomItem::id).toList();
        }
        return new ArrayList<>();
    }
}
