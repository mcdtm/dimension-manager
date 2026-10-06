package me.kvdpxne.dm.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.kvdpxne.dm.api.DimensionDefinition;

/**
 * Odpowiedzialnosc: usuwanie swiatow (unload + kasacja folderu).
 */
public final class WorldDeleterService {

  private final VoidChunkGeneratorBridge vcgBridge;
  private final Logger logger;

  public WorldDeleterService(final VoidChunkGeneratorBridge vcgBridge, final Logger logger) {
    this.vcgBridge = vcgBridge;
    this.logger = logger;
  }

  public boolean delete(final DimensionDefinition definition) {
    final var worldName = definition.name();

    // 1) Wyrzuc graczy i unload
    final var world = Bukkit.getWorld(worldName);
    if (world != null) {
      this.evictPlayers(world);
      if (!Bukkit.unloadWorld(world, false)) {
        this.logger.warning("Nie udalo sie rozladowac swiata: " + worldName);
        return false;
      }
    }

    // 2) Usun nadpisanie VCG (jesli istnieje)
    if (definition.isVoid()) {
      this.vcgBridge.unregister(worldName);
    }

    // 3) Usun foldery swiata (glowny + _nether + _the_end, jesli istnieja)
    final var container = Bukkit.getWorldContainer().toPath();
    this.deleteIfExists(container.resolve(worldName));
    this.deleteIfExists(container.resolve(worldName + "_nether"));
    this.deleteIfExists(container.resolve(worldName + "_the_end"));

    this.logger.info("Usunieto swiat: " + worldName);
    return true;
  }

  private void evictPlayers(final World world) {
    final var fallback = Bukkit.getWorlds().stream()
      .filter(w -> !w.equals(world))
      .findFirst()
      .orElse(null);
    if (fallback == null) return;

    for (final var player : world.getPlayers()) {
      player.teleport(fallback.getSpawnLocation());
    }
  }

  private void deleteIfExists(final Path path) {
    if (!Files.exists(path)) return;
    try (final var stream = Files.walk(path)) {
      stream.sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
    } catch (final IOException ex) {
      this.logger.log(Level.SEVERE, "Blad kasacji: " + path, ex);
    }
  }
}