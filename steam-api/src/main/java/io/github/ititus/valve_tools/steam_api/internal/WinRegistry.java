package io.github.ititus.valve_tools.steam_api.internal;

public final class WinRegistry {

    private WinRegistry() {}

    public static String getValue(String key, String name) throws Exception {
        var pb = new ProcessBuilder("REG", "QUERY", key, "/v", name);
        var p = pb.start();
        var exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("exit code " + exitCode);
        }

        String line;
        try (var r = p.inputReader()) {
            line = r.lines().skip(2).findFirst().orElseThrow();
        }

        var split = line.split("    ", 4);
        return split[3];
    }
}
