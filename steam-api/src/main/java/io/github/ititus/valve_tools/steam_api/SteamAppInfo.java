package io.github.ititus.valve_tools.steam_api;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.kv.KeyValues;

import java.nio.file.Path;

public final class SteamAppInfo {

    private final int appId;
    private final String name;
    private final Path installDir;

    private SteamAppInfo(int appId, String name, Path installDir) {
        this.appId = appId;
        this.name = name;
        this.installDir = installDir;
    }

    static SteamAppInfo from(Path libraryPath, int appId, KeyValues kv) {
        if (kv.getChild("appid").orElseThrow().asPrimitive().asUInt() != appId) {
            throw new IllegalArgumentException("appId mismatch");
        }

        var name = kv.getChild("name").orElseThrow().asPrimitive().asString();
        var installDir = PathUtil.resolveRealDir(libraryPath.resolve(Path.of("steamapps", "common", kv.getChild("installdir").orElseThrow().asPrimitive().asString())));
        return new SteamAppInfo(appId, name, installDir);
    }

    public int getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public Path getInstallDir() {
        return installDir;
    }
}
