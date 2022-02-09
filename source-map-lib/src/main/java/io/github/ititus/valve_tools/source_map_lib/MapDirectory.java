package io.github.ititus.valve_tools.source_map_lib;

import io.github.ititus.commons.data.Lazy;
import io.github.ititus.commons.io.FileExtensionFilter;
import io.github.ititus.commons.io.PathFilter;
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

    private static final PathFilter FILTER = new FileExtensionFilter("bsp");

    private final Path mapDir;
    private final Lazy<List<MapInfo>> maps;

    private MapDirectory(Path mapDir) {
        this.mapDir = mapDir;
        this.maps = Lazy.of(() -> {
            try (Stream<Path> stream = Files.walk(mapDir)) {
                return stream
                        .filter(Files::isRegularFile)
                        .filter(FILTER)
                        .map(MapInfo::of)
                        .sorted()
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
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
                        .resolve("csgo/maps")
        );
    }

    public Path getMapDir() {
        return mapDir;
    }

    public List<MapInfo> findMaps() {
        return maps.get();
    }

    public MapInfo resolve(String path) {
        return MapInfo.of(mapDir.resolve(path));
    }
}
