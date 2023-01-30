package io.github.ititus.valve_tools.steam_api;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.commons.system.OS;
import io.github.ititus.valve_tools.kv.KeyValues;
import io.github.ititus.valve_tools.steam_api.internal.WinRegistry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private static SteamInstallation instance;

    private final Path steamDir;
    private final List<LibraryFolder> libraryFolders;

    private SteamInstallation(Path steamDir) throws IOException {
        Objects.requireNonNull(steamDir, "steamDir");
        this.steamDir = PathUtil.resolveRealDir(steamDir);

        var libraryFoldersFile = PathUtil.resolveRealFile(steamDir.resolve("steamapps/libraryfolders.vdf"));
        var libraryFolders = KeyValues.parseText(libraryFoldersFile, KeyValues.Settings.simple());
        this.libraryFolders = libraryFolders.getChild("libraryfolders").orElseThrow().asKeyValues().getChildren().values().stream()
                .map(kv -> LibraryFolder.from(kv.asKeyValues()))
                .toList();
    }

    private static Optional<SteamInstallation> testCandidates(List<Callable<Path>> candidates) throws IOException {
        for (var candidate : candidates) {
            Path dir;
            try {
                dir = candidate.call();
                if (!Files.isDirectory(dir)) {
                    continue;
                }
            } catch (Exception ignored) {
                continue;
            }

            return Optional.of(new SteamInstallation(dir));
        }

        return Optional.empty();
    }

    public static synchronized SteamInstallation find() {
        if (instance != null) {
            return instance;
        }

        var candidates = PATH_CANDIDATES.get(OS.current());
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No known candidate directories for steam installation on this OS");
        }

        try {
            return instance = testCandidates(candidates).orElseThrow(() -> new RuntimeException("No steam installation found"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static SteamInstallation of(Path steamDir) {
        if (steamDir == null || !Files.isDirectory(steamDir)) {
            throw new IllegalArgumentException("Given path is not a valid directory");
        }

        try {
            return new SteamInstallation(steamDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Path getSteamDir() {
        return steamDir;
    }

    public List<LibraryFolder> getLibraryFolders() {
        return libraryFolders;
    }

    public Optional<Path> getInstallationDir(int appId) {
        for (var libraryFolder : libraryFolders) {
            var installDir = libraryFolder.getInstallDir(appId);
            if (installDir.isPresent()) {
                return installDir;
            }
        }

        return Optional.empty();
    }
}
