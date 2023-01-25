package io.github.ititus.valve_tools.steam_web_api.common;

public enum FileType {

    Community("normal Workshop item that can be subscribed to"),
    Microtransaction("Workshop item that is meant to be voted on for the purpose of selling in-game"),
    Collection("a collection of Workshop or Greenlight items"),
    Art("artwork"),
    Video("external video"),
    Screenshot("screenshot"),
    Game("Greenlight game entry"),
    Software("Greenlight software entry"),
    Concept("Greenlight concept"),
    WebGuide("Steam web guide"),
    IntegratedGuide("application integrated guide"),
    Merch("Workshop merchandise meant to be voted on for the purpose of being sold"),
    ControllerBinding("Steam Controller bindings"),
    SteamworksAccessInvite("internal"),
    SteamVideo("Steam video"),
    GameManagedItem("managed completely by the game, not the user, and not shown on the web");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
