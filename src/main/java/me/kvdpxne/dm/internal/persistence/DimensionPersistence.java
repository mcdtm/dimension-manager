package me.kvdpxne.dm.internal.persistence;

import java.util.Map;

import me.kvdpxne.dm.api.DimensionDefinition;

public interface DimensionPersistence {
  Map<String, DimensionDefinition> load();

  void save(Map<String, DimensionDefinition> definitions);
}