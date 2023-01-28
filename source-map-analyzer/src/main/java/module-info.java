module io.github.ititus.valve_tools.source_map_analyzer {
    requires io.github.ititus.valve_tools.steam_api;
    requires io.github.ititus.valve_tools.steam_web_api;
    requires io.github.ititus.valve_tools.source_map_lib;
    requires io.github.ititus.valve_tools.vpk;
    requires io.github.ititus.commons;
    requires info.picocli;

    exports io.github.ititus.valve_tools.source_map_analyzer;
    opens io.github.ititus.valve_tools.source_map_analyzer to info.picocli;
}
