package me.kvdpxne.dm.api;

import java.util.Optional;

import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;

/**
 * Immutable definicja wymiaru.
 * {@code voidConfig} puste = generator vanilla; wypelnione = VoidChunkGenerator.
 */
public record DimensionDefinition(
  String name,
  World.Environment environment,
  WorldType worldType,
  long seed,
  Difficulty difficulty,
  boolean keepSpawnInMemory,
  boolean autoSave,
  Optional<VoidWorldConfig> voidConfig
) {

  public DimensionDefinition {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name cannot be null/blank");
    }
    voidConfig = voidConfig == null ? Optional.empty() : voidConfig;
  }

  public boolean isVoid() {
    return this.voidConfig.isPresent();
  }

  public static Builder builder(final String name) {
    return new Builder(name);
  }

  public static final class Builder {
    private final String name;
    private World.Environment environment = World.Environment.NORMAL;
    private WorldType worldType = WorldType.NORMAL;
    private long seed = 0L;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean keepSpawnInMemory = true;
    private boolean autoSave = true;
    private VoidWorldConfig voidConfig = null;

    private Builder(final String name) {
      this.name = name;
    }

    public Builder environment(final World.Environment v) {
      this.environment = v;
      return this;
    }

    public Builder worldType(final WorldType v) {
      this.worldType = v;
      return this;
    }

    public Builder seed(final long v) {
      this.seed = v;
      return this;
    }

    public Builder difficulty(final Difficulty v) {
      this.difficulty = v;
      return this;
    }

    public Builder keepSpawnInMemory(final boolean v) {
      this.keepSpawnInMemory = v;
      return this;
    }

    public Builder autoSave(final boolean v) {
      this.autoSave = v;
      return this;
    }

    public Builder voidConfig(final VoidWorldConfig v) {
      this.voidConfig = v;
      return this;
    }

    public DimensionDefinition build() {
      return new DimensionDefinition(
        this.name, this.environment, this.worldType, this.seed,
        this.difficulty, this.keepSpawnInMemory, this.autoSave,
        Optional.ofNullable(this.voidConfig));
    }
  }
}