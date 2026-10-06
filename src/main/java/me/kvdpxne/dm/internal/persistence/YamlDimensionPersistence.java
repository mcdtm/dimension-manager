package me.kvdpxne.dm.internal.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.BorderPattern;
import me.kvdpxne.vcg.api.PlatformConfig;
import me.kvdpxne.vcg.api.SpawnOffset;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kvdpxne.dm.api.DimensionDefinition;

public final class YamlDimensionPersistence implements DimensionPersistence {

  private static final String ROOT = "dimensions";

  private final File file;
  private final Logger logger;

  public YamlDimensionPersistence(final File file, final Logger logger) {
    this.file = file;
    this.logger = logger;
  }

  @Override
  public Map<String, DimensionDefinition> load() {
    if (!this.file.exists()) return Collections.emptyMap();

    final var yaml = YamlConfiguration.loadConfiguration(this.file);
    final var section = yaml.getConfigurationSection(ROOT);
    if (section == null) return Collections.emptyMap();

    final var result = new HashMap<String, DimensionDefinition>();
    for (final var worldName : section.getKeys(false)) {
      try {
        final var cs = section.getConfigurationSection(worldName);
        if (cs == null) continue;
        result.put(worldName, this.deserialize(worldName, cs));
      } catch (final Exception ex) {
        this.logger.log(Level.WARNING,
          "Nie udalo sie wczytac definicji '" + worldName + "'", ex);
      }
    }
    return result;
  }

  @Override
  public void save(final Map<String, DimensionDefinition> definitions) {
    final var yaml = new YamlConfiguration();

    for (final var entry : definitions.entrySet()) {
      final var section = yaml.createSection(ROOT + "." + entry.getKey());
      this.serialize(section, entry.getValue());
    }

    try {
      final var parent = this.file.getParentFile();
      if (parent != null && !parent.exists()) parent.mkdirs();
      yaml.save(this.file);
    } catch (final IOException ex) {
      this.logger.log(Level.SEVERE, "Blad zapisu " + this.file.getName(), ex);
    }
  }

  private void serialize(final ConfigurationSection s, final DimensionDefinition d) {
    s.set("environment", d.environment().name());
    s.set("worldType", d.worldType().name());
    s.set("seed", d.seed());
    s.set("difficulty", d.difficulty().name());
    s.set("keepSpawnInMemory", d.keepSpawnInMemory());
    s.set("autoSave", d.autoSave());

    d.voidConfig().ifPresent(v -> {
      final var vs = s.createSection("void");
      vs.set("biome", v.biome().name());
      vs.set("allowPrecipitation", v.allowPrecipitation());
      vs.set("allowPassiveMobs", v.allowPassiveMobs());
      vs.set("allowHostileMobs", v.allowHostileMobs());
      vs.set("allowNeutralMobs", v.allowNeutralMobs());
      vs.set("enableDayNightCycle", v.enableDayNightCycle());

      v.platform().ifPresent(p -> {
        final var ps = vs.createSection("platform");
        ps.set("y", p.y());
        ps.set("chunkRadius", p.chunkRadius());
        ps.set("mainBlock", p.mainBlock().name());
        ps.set("borderBlock", p.borderBlock().name());
        ps.set("pattern", p.pattern().name());
      });

      v.spawnLocation().ifPresent(sp -> {
        final var ss = vs.createSection("spawn");
        ss.set("x", sp.x());
        ss.set("y", sp.y());
        ss.set("z", sp.z());
        ss.set("yaw", (double) sp.yaw());
        ss.set("pitch", (double) sp.pitch());
      });
    });
  }

  private DimensionDefinition deserialize(final String name, final ConfigurationSection s) {
    final var builder = DimensionDefinition.builder(name)
      .environment(World.Environment.valueOf(s.getString("environment", "NORMAL")))
      .worldType(WorldType.valueOf(s.getString("worldType", "NORMAL")))
      .seed(s.getLong("seed", 0L))
      .difficulty(Difficulty.valueOf(s.getString("difficulty", "NORMAL")))
      .keepSpawnInMemory(s.getBoolean("keepSpawnInMemory", true))
      .autoSave(s.getBoolean("autoSave", true));

    final var vs = s.getConfigurationSection("void");
    if (vs != null) {
      builder.voidConfig(this.deserializeVoid(vs));
    }

    return builder.build();
  }

  private VoidWorldConfig deserializeVoid(final ConfigurationSection vs) {
    final var biome = Biome.valueOf(vs.getString("biome", "OCEAN"));

    Optional<PlatformConfig> platform = Optional.empty();
    final var ps = vs.getConfigurationSection("platform");
    if (ps != null) {
      platform = Optional.of(new PlatformConfig(
        ps.getInt("y", 64),
        ps.getInt("chunkRadius", 1),
        Material.valueOf(ps.getString("mainBlock", "STONE")),
        Material.valueOf(ps.getString("borderBlock", "BEDROCK")),
        BorderPattern.valueOf(ps.getString("pattern", "SPAWN_MARKER"))));
    }

    Optional<SpawnOffset> spawn = Optional.empty();
    final var ss = vs.getConfigurationSection("spawn");
    if (ss != null) {
      spawn = Optional.of(new SpawnOffset(
        ss.getDouble("x"), ss.getDouble("y"), ss.getDouble("z"),
        (float) ss.getDouble("yaw"), (float) ss.getDouble("pitch")));
    }

    return new VoidWorldConfig(
      biome,
      vs.getBoolean("allowPrecipitation", false),
      vs.getBoolean("allowPassiveMobs", false),
      vs.getBoolean("allowHostileMobs", false),
      vs.getBoolean("allowNeutralMobs", false),
      vs.getBoolean("enableDayNightCycle", false),
      platform,
      spawn);
  }
}