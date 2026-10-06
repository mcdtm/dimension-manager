package me.kvdpxne.dm.api;

import java.util.List;
import java.util.Optional;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Publiczne API DimensionManager.
 */
public interface DimensionManagerApi {

    int API_VERSION = 1;

    /**
     * Tworzy swiat na podstawie definicji.
     * Jesli definicja ma voidConfig, wymaga zaladowanego VoidChunkGenerator.
     */
    Optional<World> createWorld(DimensionDefinition definition);

    /** Usuwa swiat (unload + kasacja folderu). */
    boolean deleteWorld(String worldName);

    /** Sprawdza, czy DM zarzadza danym swiatem. */
    boolean isManaged(String worldName);

    /** Lista zarzadzanych swiatow. */
    List<String> listManagedWorlds();

    Optional<World> findWorld(String worldName);

    /** Teleportuje gracza na spawn danego swiata. */
    boolean teleport(Player player, String worldName);

    /** Rejestruje juz istniejacy swiat jako zarzadzany (bez tworzenia). */
    void adoptWorld(String worldName, DimensionDefinition definition);
}