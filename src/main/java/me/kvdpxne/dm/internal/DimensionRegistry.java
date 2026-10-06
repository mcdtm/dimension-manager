package me.kvdpxne.dm.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import me.kvdpxne.dm.api.DimensionDefinition;

/**
 * In-memory rejestr zarzadzanych wymiarow.
 * Wątkowo bezpieczny.
 */
public final class DimensionRegistry {

  private final ConcurrentHashMap<String, DimensionDefinition> definitions;

  public DimensionRegistry() {
    this.definitions = new ConcurrentHashMap<>();
  }

  public void register(final DimensionDefinition definition) {
    this.definitions.put(definition.name(), definition);
  }

  public Optional<DimensionDefinition> remove(final String name) {
    return Optional.ofNullable(this.definitions.remove(name));
  }

  public Optional<DimensionDefinition> find(final String name) {
    return Optional.ofNullable(this.definitions.get(name));
  }

  public boolean contains(final String name) {
    return this.definitions.containsKey(name);
  }

  public List<String> names() {
    return List.copyOf(this.definitions.keySet());
  }

  public Map<String, DimensionDefinition> snapshot() {
    return Map.copyOf(this.definitions);
  }

  public void replaceAll(final Map<String, DimensionDefinition> source) {
    this.definitions.clear();
    this.definitions.putAll(source);
  }
}