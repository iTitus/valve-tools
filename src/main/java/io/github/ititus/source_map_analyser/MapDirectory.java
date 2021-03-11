package io.github.ititus.source_map_analyser;

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

    public MapDirectory(Path mapDir) {
        try {
            this.mapDir = Objects.requireNonNull(mapDir).toRealPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
