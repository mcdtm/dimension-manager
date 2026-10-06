package me.kvdpxne.dm.internal.command;

import java.util.Arrays;

import me.kvdpxne.dm.api.DimensionManagerApi;
import me.kvdpxne.vcg.api.BorderPattern;
import me.kvdpxne.vcg.api.PlatformConfig;
import me.kvdpxne.vcg.api.SpawnOffset;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kvdpxne.dm.api.DimensionDefinition;

/**
 * /dimension create <name> [--void] [--biome BIOME] [--env NORMAL|NETHER|THE_END]
 * /dimension delete <name>
 * /dimension list
 * /dimension tp <name> [player]
 * /dimension info <name>
 */
public final class DimensionCommand implements CommandExecutor {

    private static final String PREFIX = ChatColor.DARK_GRAY + "["
            + ChatColor.GOLD + "Dimension" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    private final DimensionManagerApi api;
    private final boolean voidAvailable;

    public DimensionCommand(final DimensionManagerApi api, final boolean voidAvailable) {
        this.api = api;
        this.voidAvailable = voidAvailable;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command,
                             final String label, final String[] args) {
        if (args.length == 0) {
            this.usage(sender);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "create" -> this.handleCreate(sender, args);
            case "delete" -> this.handleDelete(sender, args);
            case "list" -> this.handleList(sender);
            case "tp", "teleport" -> this.handleTp(sender, args);
            case "info" -> this.handleInfo(sender, args);
            default -> {
                this.usage(sender);
                yield true;
            }
        };
    }

    // ============================================================
    //  create
    // ============================================================
    private boolean handleCreate(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Uzycie: /dimension create <name> [--void] [--biome BIOME] [--env NORMAL|NETHER|THE_END]");
            return true;
        }

        final var name = args[1];
        if (this.api.isManaged(name) || Bukkit.getWorld(name) != null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Swiat " + name + " juz istnieje.");
            return true;
        }

        var builder = DimensionDefinition.builder(name);
        var useVoid = false;
        var biomeName = "OCEAN";
        var envName = "NORMAL";

        for (var i = 2; i < args.length; i++) {
            final var arg = args[i];
            switch (arg) {
                case "--void" -> useVoid = true;
                case "--biome" -> {
                    if (i + 1 < args.length) biomeName = args[++i];
                }
                case "--env" -> {
                    if (i + 1 < args.length) envName = args[++i];
                }
                default -> { /* ignoruj */ }
            }
        }

        if (useVoid) {
            if (!this.voidAvailable) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "VoidChunkGenerator nie jest zaladowany - nie mozna utworzyc void swiata.");
                return true;
            }
            builder.voidConfig(this.buildDefaultVoidConfig(biomeName));
        }

        try {
            builder.environment(World.Environment.valueOf(envName.toUpperCase()));
        } catch (final IllegalArgumentException ex) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Nieznane srodowisko: " + envName);
            return true;
        }

        final var definition = builder.build();
        final var worldOpt = this.api.createWorld(definition);

        if (worldOpt.isEmpty()) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Nie udalo sie utworzyc swiata.");
            return true;
        }

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Utworzono swiat: " + ChatColor.YELLOW
                + name + (useVoid ? " (void)" : ""));
        return true;
    }

    private VoidWorldConfig buildDefaultVoidConfig(final String biomeName) {
        Biome biome;
        try {
            biome = Biome.valueOf(biomeName.toUpperCase());
        } catch (final IllegalArgumentException ex) {
            biome = Biome.OCEAN;
        }

        return new VoidWorldConfig(
                biome,
                false, false, false, false, false,
                java.util.Optional.of(new PlatformConfig(
                        64, 1, org.bukkit.Material.STONE,
                        org.bukkit.Material.BEDROCK, BorderPattern.SPAWN_MARKER)),
                java.util.Optional.of(SpawnOffset.of(0.5, 65.0, 0.5))
        );
    }

    // ============================================================
    //  delete
    // ============================================================
    private boolean handleDelete(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Uzycie: /dimension delete <name>");
            return true;
        }
        final var name = args[1];
        if (!this.api.isManaged(name)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Swiat " + name + " nie jest zarzadzany.");
            return true;
        }
        if (this.api.deleteWorld(name)) {
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Usunieto swiat: " + ChatColor.YELLOW + name);
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "Nie udalo sie usunac swiata.");
        }
        return true;
    }

    // ============================================================
    //  list
    // ============================================================
    private boolean handleList(final CommandSender sender) {
        final var names = this.api.listManagedWorlds();
        if (names.isEmpty()) {
            sender.sendMessage(PREFIX + ChatColor.GRAY + "Brak zarzadzanych swiatow.");
            return true;
        }
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Zarzadzane swiaty (" + names.size() + "):");
        for (final var n : names) {
            final var loaded = Bukkit.getWorld(n) != null;
            sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + n
                    + ChatColor.DARK_GRAY + (loaded ? " [loaded]" : " [unloaded]"));
        }
        return true;
    }

    // ============================================================
    //  tp
    // ============================================================
    private boolean handleTp(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Uzycie: /dimension tp <name> [player]");
            return true;
        }
        final var name = args[1];
        if (!this.api.isManaged(name)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Swiat " + name + " nie jest zarzadzany.");
            return true;
        }

        final Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(PREFIX + ChatColor.RED + "Gracz " + args[2] + " nie jest online.");
                return true;
            }
        } else if (sender instanceof Player p) {
            target = p;
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "Z konsoli: /dimension tp <name> <player>");
            return true;
        }

        if (this.api.teleport(target, name)) {
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Teleportowano " + target.getName()
                    + " do " + ChatColor.YELLOW + name);
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "Nie udalo sie teleportowac.");
        }
        return true;
    }

    // ============================================================
    //  info
    // ============================================================
    private boolean handleInfo(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Uzycie: /dimension info <name>");
            return true;
        }
        final var name = args[1];
        final var world = Bukkit.getWorld(name);
        if (world == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Swiat " + name + " nie jest zaladowany.");
            return true;
        }
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Informacje o " + ChatColor.YELLOW + name);
        sender.sendMessage(ChatColor.GRAY + " - Environment: " + ChatColor.WHITE + world.getEnvironment());
        sender.sendMessage(ChatColor.GRAY + " - Difficulty: " + ChatColor.WHITE + world.getDifficulty());
        sender.sendMessage(ChatColor.GRAY + " - Seed: " + ChatColor.WHITE + world.getSeed());
        sender.sendMessage(ChatColor.GRAY + " - Spawn: " + ChatColor.WHITE
                + world.getSpawnLocation().getBlockX() + ", "
                + world.getSpawnLocation().getBlockY() + ", "
                + world.getSpawnLocation().getBlockZ());
        sender.sendMessage(ChatColor.GRAY + " - AutoSave: " + ChatColor.WHITE + world.isAutoSave());
        sender.sendMessage(ChatColor.GRAY + " - KeepSpawnInMemory: " + ChatColor.WHITE + world.getKeepSpawnInMemory());
        return true;
    }

    private void usage(final CommandSender sender) {
        sender.sendMessage(PREFIX + ChatColor.GOLD + "DimensionManager");
        sender.sendMessage(ChatColor.GRAY + " /dimension create <name> [--void] [--biome X] [--env Y]");
        sender.sendMessage(ChatColor.GRAY + " /dimension delete <name>");
        sender.sendMessage(ChatColor.GRAY + " /dimension list");
        sender.sendMessage(ChatColor.GRAY + " /dimension tp <name> [player]");
        sender.sendMessage(ChatColor.GRAY + " /dimension info <name>");
    }
}