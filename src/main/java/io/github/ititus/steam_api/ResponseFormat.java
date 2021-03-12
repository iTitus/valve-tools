package io.github.ititus.steam_api;

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
