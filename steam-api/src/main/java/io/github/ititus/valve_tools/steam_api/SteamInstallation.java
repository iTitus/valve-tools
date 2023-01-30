package io.github.ititus.valve_tools.steam_api;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.commons.system.OS;
import io.github.ititus.valve_tools.steam_api.internal.WinRegistry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

public final class SteamInstallation {

    private static final Map<OS, List<Callable<Path>>> PATH_CANDIDATES = Map.of(
            OS.WINDOWS, List.of(
                    () -> Path.of(WinRegistry.getValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath")),
                    () -> Path.of(WinRegistry.getValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Valve\\Steam", "InstallPath"))
            ),
            OS.MAC, List.of(
                    () -> Path.of(System.getProperty("user.home"), "Library/Application Support/Steam")
            ),
            OS.UNIX, List.of(
                    () -> Path.of(System.getProperty("user.home"), ".var/app/com.valvesoftware.Steam"),
                    () -> Path.of(System.getProperty("user.home"), ".steam/steam")
            )
    );

    private final Path steamDir;

    private SteamInstallation(Path steamDir) {
        this.steamDir = steamDir;
        // TODO: find libraries from vdf file
    }

    private static Optional<SteamInstallation> testCandidates(List<Callable<Path>> candidates) {
        for (var candidate : candidates) {
            try {
                return Optional.of(new SteamInstallation(PathUtil.resolveRealDir(candidate.call())));
            } catch (Exception ignored) {
            }
        }

        return Optional.empty();
    }

    public static SteamInstallation find() {
        var candidates = PATH_CANDIDATES.get(OS.current());
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
