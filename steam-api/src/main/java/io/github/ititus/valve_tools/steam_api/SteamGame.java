package io.github.ititus.valve_tools.steam_api;

public enum SteamGame {

    COUNTERSTRIKE_GLOBAL_OFFENSIVE("Counter-Strike Global Offensive");

    private final String installationDir;

    SteamGame(String installationDir) {
        this.installationDir = installationDir;
    }

    public String getInstallationDir() {
        return installationDir;
    }
}
