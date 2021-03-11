package io.github.ititus.source_map_analyser;

import io.github.ititus.data.Lazy;

import java.util.Locale;

public final class SystemUtil {

    private static final Lazy<Os> OS = Lazy.of(() -> {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            return Os.WINDOWS;
        } else if (osName.contains("mac")) {
            return Os.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return Os.UNIX;
        } else if (osName.contains("solaris") || osName.contains("sunos")) {
            return Os.SOLARIS;
        }

        return Os.UNKNOWN;
    });

    private SystemUtil() {
    }

    public static Os currentOs() {
        return OS.get();
    }

    public enum Os {

        WINDOWS,
        MAC,
        UNIX,
        SOLARIS,
        UNKNOWN

    }
}
