package me.kvdpxne.dm.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Odpowiedzialnosc: teleportacja graczy do wymiarow.
 */
public final class TeleportService {

  public boolean teleport(final Player player, final String worldName) {
    final var world = Bukkit.getWorld(worldName);
    if (world == null) return false;
    return player.teleport(world.getSpawnLocation());
  }
}