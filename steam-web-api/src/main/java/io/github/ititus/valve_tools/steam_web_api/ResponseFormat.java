package io.github.ititus.valve_tools.steam_web_api;

public enum ResponseFormat {

    JSON("json"),
    XML("xml"),
    VDF("vdf");

    private final String name;

    ResponseFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
