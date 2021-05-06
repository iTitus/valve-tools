package io.github.ititus.valve_tools.steam_api;

import io.github.ititus.system.OS;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class SteamInstallation {

    private static final Map<OS, List<Supplier<Path>>> PATH_CANDIDATES = Map.of(
            OS.WINDOWS, List.of(
                    () -> Path.of("C:/Program Files (x86)/Steam"),
                    () -> Path.of("C:/Program Files/Steam")
            ),
            OS.MAC, List.of(
                    () -> Path.of("~/Library/Application Support/Steam")
            ),
            OS.UNIX, List.of(
                    () -> Path.of("~/.local/share/Steam")
            )
    );

    private final Path steamDir;

    private SteamInstallation(Path steamDir) {
        this.steamDir = steamDir;
    }

    private static Optional<SteamInstallation> testCandidates(List<Supplier<Path>> candidates) {
        for (Supplier<Path> candidate : candidates) {
            try {
                Path p = candidate.get();
                if (Files.isDirectory(p)) {
                    return Optional.of(new SteamInstallation(p.toRealPath()));
                }
            } catch (Exception ignored) {
            }
        }

        return Optional.empty();
    }

    public static SteamInstallation find() {
        List<Supplier<Path>> candidates = PATH_CANDIDATES.get(OS.current());
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No known candidate directories for steam installation on this OS");
        }

        return testCandidates(candidates).orElseThrow(() -> new RuntimeException("No steam installation found"));
    }

    public static SteamInstallation of(Path steamDir) {
        if (steamDir == null || !Files.isDirectory(steamDir)) {
            throw new IllegalArgumentException("Given path is not a valid directory");
        }

        try {
            return new SteamInstallation(steamDir.toRealPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Path getSteamDir() {
        return steamDir;
    }

    public Path getAppDir(SteamApp app) {
        return getAppDir(app.getInstallDir());
    }

    public Path getAppDir(String installDir) {
        return steamDir.resolve(Path.of("steamapps/common", installDir));
    }
}
