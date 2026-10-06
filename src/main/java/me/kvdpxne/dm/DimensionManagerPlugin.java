package me.kvdpxne.dm;

import java.io.File;

import me.kvdpxne.dm.api.DimensionManagerApi;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.kvdpxne.dm.internal.DimensionManagerApiImpl;
import me.kvdpxne.dm.internal.DimensionRegistry;
import me.kvdpxne.dm.internal.TeleportService;
import me.kvdpxne.dm.internal.VoidChunkGeneratorBridge;
import me.kvdpxne.dm.internal.WorldCreatorService;
import me.kvdpxne.dm.internal.WorldDeleterService;
import me.kvdpxne.dm.internal.command.DimensionCommand;
import me.kvdpxne.dm.internal.command.DimensionTabCompleter;
import me.kvdpxne.dm.internal.persistence.DimensionPersistence;
import me.kvdpxne.dm.internal.persistence.YamlDimensionPersistence;

/**
 * Klasa glowna.
 *
 * Odpowiedzialnosc: cykl zycia pluginu + bootstrap komponentow.
 * Logika zyje w serwisach w package'u internal.
 */
public final class DimensionManagerPlugin extends JavaPlugin {

  private DimensionManagerApiImpl apiImpl;
  private DimensionManagerApi api;
  private DimensionRegistry registry;
  private VoidChunkGeneratorBridge vcgBridge;

  @Override
  public void onLoad() {
    this.saveDefaultConfig();

    // Bridge do VoidChunkGenerator - sprawdzamy dostepnosc raz, ale API
    // i tak pytamy dynamicznie (VCG moze byc softdepend).
    this.vcgBridge = new VoidChunkGeneratorBridge(this.getLogger());

    // Persystencja
    final var persistenceFile = new File(this.getDataFolder(), "dimensions.yml");
    final DimensionPersistence persistence =
      new YamlDimensionPersistence(persistenceFile, this.getLogger());

    // Komponenty (SRP)
    this.registry = new DimensionRegistry();
    final var creator = new WorldCreatorService(this.vcgBridge, this.getLogger());
    final var deleter = new WorldDeleterService(this.vcgBridge, this.getLogger());
    final var teleporter = new TeleportService();

    this.apiImpl = new DimensionManagerApiImpl(
      this.registry, creator, deleter, teleporter, persistence);
    this.api = this.apiImpl;

    // Zaladuj zapisane wymiary do rejestru (sam rejestr - bez tworzenia swiatow).
    this.apiImpl.loadFromPersistence();

    this.getLogger().info("onLoad OK (VCG dostepne: " + this.vcgBridge.isAvailable() + ").");
  }

  @Override
  public void onEnable() {
    // Rejestracja API
    this.getServer().getServicesManager().register(
      DimensionManagerApi.class, this.api, this, ServicePriority.Normal);

    // Komendy
    final var dimCmd = this.getCommand("dimension");
    if (dimCmd != null) {
      dimCmd.setExecutor(new DimensionCommand(this.api, this.vcgBridge.isAvailable()));
      dimCmd.setTabCompleter(new DimensionTabCompleter(this.api));
    }

    // Wczytaj zapisane swiaty (jesli folder istnieje, createWorld je zaladuje).
    if (this.getConfig().getBoolean("loadDimensionsOnStartup", true)) {
      this.loadPersistedWorlds();
    }

    this.getLogger().info("DimensionManager wlaczony (API v"
      + DimensionManagerApi.API_VERSION + ").");
  }

  @Override
  public void onDisable() {
    if (this.apiImpl != null) {
      this.apiImpl.persist();
    }
    this.getServer().getServicesManager().unregisterAll(this);
    this.getLogger().info("DimensionManager wylaczony.");
  }

  /**
   * Ladowanie swiatow zapisanych w dimensions.yml.
   * Bukkit.createWorld na istniejacym folderze po prostu go zaladuje.
   */
  private void loadPersistedWorlds() {
    for (final var name : this.registry.names()) {
      if (Bukkit.getWorld(name) != null) continue;
      this.registry.find(name).ifPresent(def ->
        this.apiImpl.createWorld(def).ifPresent(w ->
          this.getLogger().info("Zaladowano wymiar: " + name)));
    }
  }

  private Object registry() {
    // Na potrzeby loadPersistedWorlds - mozna to uproscic trzymajac
    // DimensionRegistry jako pole klasy. Zostawione dla przejrzystosci.
    return null;
  }
}