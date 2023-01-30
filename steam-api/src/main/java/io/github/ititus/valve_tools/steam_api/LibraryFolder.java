package io.github.ititus.valve_tools.steam_api;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.kv.KeyValues;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LibraryFolder {

    private final Path path;
    private final Map<Integer, SteamAppInfo> apps;

    private LibraryFolder(Path path, Map<Integer, SteamAppInfo> apps) {
        this.path = path;
        this.apps = apps;
    }

    static LibraryFolder from(KeyValues kv) {
        var path = PathUtil.resolveRealDir(Path.of(kv.getChild("path").orElseThrow().asPrimitive().asString()));
        var apps = kv.getChild("apps").orElseThrow().asKeyValues().getChildren().keySet().stream()
                .map(Integer::parseUnsignedInt)
                .collect(Collectors.toUnmodifiableMap(Function.identity(), appId -> {
                    var manifestFile = PathUtil.resolveRealFile(path.resolve(Path.of("steamapps", "appmanifest_" + appId + ".acf")));

                    KeyValues manifest;
                    try {
                        manifest = KeyValues.parseText(manifestFile, KeyValues.Settings.simple());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }

                    return SteamAppInfo.from(path, appId, manifest.asKeyValues().getChild("AppState").orElseThrow().asKeyValues());
                }));
        return new LibraryFolder(path, apps);
    }

    public Path getPath() {
        return path;
    }

    public Map<Integer, SteamAppInfo> getApps() {
        return apps;
    }

    public Optional<Path> getInstallDir(int appId) {
        return Optional.ofNullable(apps.get(appId)).map(SteamAppInfo::getInstallDir);
    }
}
