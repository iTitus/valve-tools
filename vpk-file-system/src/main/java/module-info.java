import io.github.ititus.valve_tools.vpk.VpkFileSystemProvider;

module io.github.ititus.valve_tools.vpk {
    requires io.github.ititus.commons;

    exports io.github.ititus.valve_tools.vpk;

    provides java.nio.file.spi.FileSystemProvider with VpkFileSystemProvider;
}
