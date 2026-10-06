package me.kvdpxne.dm.internal;

import java.util.List;
import java.util.Optional;

import me.kvdpxne.dm.api.DimensionManagerApi;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.kvdpxne.dm.api.DimensionDefinition;
import me.kvdpxne.dm.internal.persistence.DimensionPersistence;

/**
 * Implementacja API. Deleguje do wyspecjalizowanych serwisow.
 */
public final class DimensionManagerApiImpl implements DimensionManagerApi {

  private final DimensionRegistry registry;
  private final WorldCreatorService creator;
  private final WorldDeleterService deleter;
  private final TeleportService teleporter;
  private final DimensionPersistence persistence;

  public DimensionManagerApiImpl(final DimensionRegistry registry,
                                 final WorldCreatorService creator,
                                 final WorldDeleterService deleter,
                                 final TeleportService teleporter,
                                 final DimensionPersistence persistence) {
    this.registry = registry;
    this.creator = creator;
    this.deleter = deleter;
    this.teleporter = teleporter;
    this.persistence = persistence;
  }

  @Override
  public Optional<World> createWorld(final DimensionDefinition definition) {
    final var world = this.creator.create(definition);
    world.ifPresent(w -> {
      this.registry.register(definition);
      this.persist();
    });
    return world;
  }

  @Override
  public boolean deleteWorld(final String worldName) {
    final var definition = this.registry.find(worldName);
    if (definition.isEmpty()) {
      return false;
    }
    if (!this.deleter.delete(definition.get())) {
      return false;
    }
    this.registry.remove(worldName);
    this.persist();
    return true;
  }

  @Override
  public boolean isManaged(final String worldName) {
    return this.registry.contains(worldName);
  }

  @Override
  public List<String> listManagedWorlds() {
    return this.registry.names();
  }

  @Override
  public Optional<World> findWorld(final String worldName) {
    return this.registry.find(worldName).map(d -> org.bukkit.Bukkit.getWorld(d.name()));
  }

  @Override
  public boolean teleport(final Player player, final String worldName) {
    if (!this.registry.contains(worldName)) {
      return false;
    }
    return this.teleporter.teleport(player, worldName);
  }

  @Override
  public void adoptWorld(final String worldName, final DimensionDefinition definition) {
    this.registry.register(definition);
    this.persist();
  }

  public void loadFromPersistence() {
    this.registry.replaceAll(this.persistence.load());
  }

  public void persist() {
    this.persistence.save(this.registry.snapshot());
  }
}