package me.kvdpxne.dm.internal;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import me.kvdpxne.dm.api.DimensionDefinition;

/**
 * Odpowiedzialnosc: tworzenie swiatow na podstawie DimensionDefinition.
 */
public final class WorldCreatorService {

  private final VoidChunkGeneratorBridge vcgBridge;
  private final Logger logger;

  public WorldCreatorService(final VoidChunkGeneratorBridge vcgBridge, final Logger logger) {
    this.vcgBridge = vcgBridge;
    this.logger = logger;
  }

  public Optional<World> create(final DimensionDefinition definition) {
    if (Bukkit.getWorld(definition.name()) != null) {
      this.logger.warning("Swiat '" + definition.name() + "' juz istnieje.");
      return Optional.empty();
    }

    try {
      final var creator = new WorldCreator(definition.name())
        .environment(definition.environment())
        .type(definition.worldType())
        .seed(definition.seed())
        .generateStructures(true);

      if (definition.isVoid()) {
        final var generatorOpt = this.vcgBridge.prepareGenerator(
          definition.name(), definition.voidConfig().orElseThrow());
        if (generatorOpt.isEmpty()) {
          this.logger.warning("Nie mozna utworzyc void swiata '"
            + definition.name() + "' - brak generatora.");
          return Optional.empty();
        }
        creator.generator(generatorOpt.get());
      }

      final var world = Bukkit.createWorld(creator);
      if (world == null) {
        this.logger.warning("Bukkit.createWorld zwrocil null dla '"
          + definition.name() + "'.");
        return Optional.empty();
      }

      this.applySettings(world, definition);

      if (definition.isVoid()) {
        this.vcgBridge.applyWorldSettings(world);
      }

      this.logger.info("Utworzono swiat: " + definition.name());
      return Optional.of(world);

    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE,
        "Blad tworzenia swiata '" + definition.name() + "'", ex);
      return Optional.empty();
    }
  }

  private void applySettings(final World world, final DimensionDefinition definition) {
    world.setDifficulty(definition.difficulty());
    world.setKeepSpawnInMemory(definition.keepSpawnInMemory());
    world.setAutoSave(definition.autoSave());
  }
}