package io.github.ititus.valve_tools.source_map_lib;

import io.github.ititus.commons.data.Lazy;
import io.github.ititus.commons.io.FileExtensionFilter;
import io.github.ititus.commons.io.PathFilter;
import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.steam_api.SteamInstallation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class MapDirectory {

    private static final PathFilter FILTER = new FileExtensionFilter("bsp");

    private final Path mapDir;
    private final Lazy<List<MapInfo>> maps;

    private MapDirectory(Path mapDir) {
        this.mapDir = PathUtil.resolveRealDir(Objects.requireNonNull(mapDir));
        this.maps = Lazy.of(() -> {
            try (Stream<Path> stream = Files.walk(mapDir)) {
                return stream
                        .filter(Files::isRegularFile)
                        .filter(FILTER)
                        .map(MapInfo::of)
                        .sorted()
                        .toList();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static MapDirectory of(Path mapDir) {
        return new MapDirectory(mapDir);
    }

    public static MapDirectory csgo() {
        return of(
                SteamInstallation.find()
                        .getInstallationDir(730)
                        .orElseThrow()
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
