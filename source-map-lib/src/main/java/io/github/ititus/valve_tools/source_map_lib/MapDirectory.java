package io.github.ititus.valve_tools.source_map_lib;

import io.github.ititus.valve_tools.steam_api.SteamApp;
import io.github.ititus.valve_tools.steam_api.SteamInstallation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MapDirectory {

    private final Path mapDir;
    private List<MapInfo> maps;

    private MapDirectory(Path mapDir) {
        this.mapDir = mapDir;
    }

    public static MapDirectory of(Path mapDir) {
        try {
            return new MapDirectory(Objects.requireNonNull(mapDir).toRealPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static MapDirectory csgo() {
        return of(
                SteamInstallation.find()
                        .getAppDir(SteamApp.COUNTERSTRIKE_GLOBAL_OFFENSIVE)
                        .resolve(Path.of("csgo", "maps"))
        );
    }

    public Path getMapDir() {
        return mapDir;
    }

    public List<MapInfo> findMaps() {
        if (maps == null) {
            try (Stream<Path> stream = Files.walk(mapDir)) {
                maps = stream
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".bsp"))
                        .map(MapInfo::of)
                        .sorted()
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        return maps;
    }

    public Path resolve(String path) {
        return mapDir.resolve(path);
    }
}
