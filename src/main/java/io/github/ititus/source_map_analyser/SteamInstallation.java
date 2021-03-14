package io.github.ititus.source_map_analyser;

import io.github.ititus.util.SystemUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.ititus.util.SystemUtil.Os.*;

public final class SteamInstallation {

    private static final Map<SystemUtil.Os, List<Path>> PATH_CANDIDATES = Map.of(
            WINDOWS, List.of(
                    Path.of("C:", "Program Files (x86)", "Steam"),
                    Path.of("C:", "Program Files", "Steam")
            ),
            MAC, List.of(
                    Path.of("~", "Library", "Application Support", "Steam")
            ),
            UNIX, List.of(
                    Path.of("~", ".local", "share", "Steam")
            )
    );

    private final Path steamDir;

    private SteamInstallation(Path steamDir) {
        this.steamDir = steamDir;
    }

    private static Optional<Path> testCandidates(List<Path> candidates) {
        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    public static SteamInstallation find() {
        List<Path> candidates = PATH_CANDIDATES.get(SystemUtil.currentOs());
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No known candidate directories for steam installation");
        }

        return testCandidates(candidates)
                .map(SteamInstallation::new)
                .orElseThrow(() -> new RuntimeException("No steam installation found"));
    }

    public static SteamInstallation of(Path steamDir) {
        if (steamDir == null || !Files.exists(steamDir)) {
            throw new IllegalArgumentException("Given path is not a valid directory");
        }

        return new SteamInstallation(steamDir);
    }

    public Path getSteamDir() {
        return steamDir;
    }

    public Path getGameDir(String gameName) {
        return steamDir.resolve(Path.of("steamapps", "common", gameName));
    }
}
