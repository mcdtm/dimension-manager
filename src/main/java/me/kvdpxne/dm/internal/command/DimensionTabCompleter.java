package me.kvdpxne.dm.internal.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.kvdpxne.dm.api.DimensionManagerApi;

public final class DimensionTabCompleter implements TabCompleter {

    private final DimensionManagerApi api;

    public DimensionTabCompleter(final DimensionManagerApi api) {
        this.api = api;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command,
                                      final String alias, final String[] args) {
        final List<String> result = new ArrayList<>();

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0],
                    List.of("create", "delete", "list", "tp", "info"), result);
        }

        final var sub = args[0].toLowerCase();

        if (args.length == 2) {
            return switch (sub) {
                case "delete", "tp", "info" -> StringUtil.copyPartialMatches(
                        args[1], this.api.listManagedWorlds(), result);
                case "create" -> Collections.emptyList();
                default -> Collections.emptyList();
            };
        }

        if (args.length == 3 && "tp".equals(sub)) {
            final List<String> players = new ArrayList<>();
//            for (final Player p : Bukkit.getOnlinePlayers()) players.add(p.getName());
            return StringUtil.copyPartialMatches(args[2], players, result);
        }

        if ("create".equals(sub)) {
            return StringUtil.copyPartialMatches(args[args.length - 1],
                    List.of("--void", "--biome", "--env"), result);
        }

        return Collections.emptyList();
    }
}