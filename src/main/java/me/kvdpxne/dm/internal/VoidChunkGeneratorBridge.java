package me.kvdpxne.dm.internal;

import java.util.Optional;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.VoidChunkGenApi;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Most miedzy DimensionManager a VoidChunkGenerator.
 * Odpowiedzialnosc: cala wiedza o VCG w jednym miejscu (SRP).
 */
public final class VoidChunkGeneratorBridge {

  private static final String PLUGIN_NAME = "VoidChunkGenerator";

  private final Logger logger;

  public VoidChunkGeneratorBridge(final Logger logger) {
    this.logger = logger;
  }

  /**
   * Czy VCG jest zaladowany i zarejestrowal API?
   */
  public boolean isAvailable() {
    return this.plugin() != null && this.api() != null;
  }

  /**
   * Rejestruje konfiguracje void dla swiata i zwraca instancje generatora.
   * Musi byc wywolane PRZED {@code Bukkit.createWorld}.
   */
  public Optional<ChunkGenerator> prepareGenerator(final String worldName,
                                                   final VoidWorldConfig config) {
    final var plugin = this.plugin();
    final var api = this.api();

    if (plugin == null || api == null) {
      this.logger.warning("VoidChunkGenerator nie jest dostepny - pomijam generacje void.");
      return Optional.empty();
    }

    api.registerWorldConfig(worldName, config);
    return Optional.ofNullable(plugin.getDefaultWorldGenerator(worldName, null));
  }

  /**
   * Zastosowuje ustawienia VCG (pogoda, cykl dnia, spawn flags) na swiecie.
   */
  public void applyWorldSettings(final World world) {
    final var api = this.api();
    if (api != null) {
      api.applyWorldSettings(world);
    }
  }

  /**
   * Usuwa nadpisanie konfiguracji dla swiata (przy delete).
   */
  public void unregister(final String worldName) {
    final var api = this.api();
    if (api != null) {
      api.unregisterWorldConfig(worldName);
    }
  }

  private JavaPlugin plugin() {
    final var plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
    return plugin instanceof JavaPlugin javaPlugin ? javaPlugin : null;
  }

  private VoidChunkGenApi api() {
    return Bukkit.getServicesManager().load(VoidChunkGenApi.class);
  }
}