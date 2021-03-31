package io.github.ititus.valve_tools.steam_api;

import java.util.HashMap;
import java.util.Map;

public enum SteamApp {

    COUNTERSTRIKE(10, "Counter-Strike", "Half-Life"),
    TEAM_FORTRESS_CLASSIC(20, "Team Fortress Classic", "Half-Life"),
    DAY_OF_DEFEAT(30, "Day of Defeat", "Half-Life"),
    DEATHMATCH_CLASSIC(40, "Deathmatch Classic", "Half-Life"),
    HALFLIFE_OPPOSING_FORCE(50, "Half-Life: Opposing Force", "Half-Life"),
    RICOCHET(60, "Ricochet", "Half-Life"),
    HALFLIFE(70, "Half-Life"),
    COUNTERSTRIKE_CONDITION_ZERO(80, "Counter-Strike: Condition Zero", "Half-Life"),
    CODENAME_GORDON(92, "Codename Gordon"),
    COUNTERSTRIKE_CONDITION_ZERO_DELETED_SCENES(100, "Counter-Strike: Condition Zero Deleted Scenes", "Half-Life"),
    HALFLIFE_BLUE_SHIFT(130, "Half-Life: Blue Shift", "Half-Life"),
    SOURCE_SDK(211, "Source SDK", "SourceSDK"),
    SOURCE_SDK_BASE_2006(215, "Source SDK Base 2006", "Source SDK Base"),
    SOURCE_SDK_BASE_2007(218, "Source SDK Base 2006", "Source SDK Base 2007"),
    HALFLIFE_2_DEMO(219, "Half-Life 2: Demo", "Half-Life 2"),
    HALFLIFE_2(220, "Half-Life 2"),
    COUNTERSTRIKE_SOURCE(240, "Counter-Strike: Source", "Counter-Strike Source"),
    HALFLIFE_SOURCE(280, "Half-Life: Source", "Half-Life 2"),
    DAY_OF_DEFEAT_SOURCE(300, "Day of Defeat: Source", "Day of Defeat Source"),
    HALFLIFE_2_DEATHMATCH(320, "Half-Life 2: Deathmatch", "Half-Life 2 Deathmatch"),
    HALFLIFE_2_LOST_COAST(340, "Half-Life 2: Lost Coast", "Half-Life 2"),
    HALFLIFE_DEATHMATCH_SOURCE(360, "Half-Life Deathmatch: Source", "Half-Life 1 Source Deathmatch"),
    HALFLIFE_2_EPISODE_ONE(380, "Half-Life 2: Episode One", "Half-Life 2"),
    PORTAL(400, "Portal"),
    PORTAL_FIRST_SLICE(410, "Portal: First Slice", "Portal"),
    HALFLIFE_2_EPISODE_TWO(420, "Half-Life 2: Episode Two", "Half-Life 2"),
    TEAM_FORTRESS_2(440, "Team Fortress 2"),
    SPACEWAR(480, "Spacewar"),
    LEFT_4_DEAD(500, "Left 4 Dead", "left 4 dead"),
    LEFT_4_DEAD_AUTHORING_TOOLS(513, "Left 4 Dead Authoring Tools", "left 4 dead"),
    LEFT_4_DEAD_DEMO(530, "Left 4 Dead Demo"),
    LEFT_4_DEAD_2(550, "Left 4 Dead 2"),
    LEFT_4_DEAD_2_AUTHORING_TOOLS(563, "Left 4 Dead 2 Authoring Tools", "Left 4 Dead 2"),
    DOTA_2(570, "Dota 2", "dota 2 beta"),
    LEFT_4_DEAD_2_DEMO(590, "Left 4 Dead 2 Demo", "Left 4 Dead 2"),
    PORTAL_2(620, "Portal 2"),
    PORTAL_2_AUTHORING_TOOLS(629, "Portal 2 Authoring Tools", "Portal 2"),
    ALIEN_SWARM(630, "Alien Swarm"),
    PORTAL_2_PUBLISHING_TOOL(644, "Portal 2 Publishing Tool", "Portal 2 Publishing Tool"),
    COUNTERSTRIKE_GLOBAL_OFFENSIVE(730, "Counter-Strike: Global Offensive", "Counter-Strike Global Offensive"),
    PORTAL_2_BETA(841, "Portal 2 Beta", "ValveTestApp841"),
    SOURCE_FILMMAKER(1840, "Source Filmmaker", "SourceFilmmaker"),
    GARRYS_MOD(4000, "Garry's Mod", "GarrysMod"),
    MAC_PORTAL(52003, "Mac Portal", "mac_portal"),
    DOTA_2_TEST(205790, "Dota 2 Test", "dota 2 test"),
    LEFT_4_DEAD_2_DEDICATED_SERVER(222860, "Left 4 Dead 2 Dedicated Server"),
    LEFT_4_DEAD_2_BETA(223530, "Left 4 Dead 2 Beta"),
    LEFT_4_DEAD_2_BETA_AUTHORING_TOOLS(223540, "Left 4 Dead 2 Beta Authoring Tools", "Left 4 Dead 2 Beta"),
    LEFT_4_DEAD_2_BETA_WIN32_DEDICATED_SERVER(227040, "Left 4 Dead 2 Beta - Win32 Dedicated Server", "Left 4 Dead 2 Beta"),
    LEFT_4_DEAD_2_BETA_LINUX_DEDICATED_SERVER(227050, "Left 4 Dead 2 Beta - Linux Dedicated Server", "Left 4 Dead 2 Beta"),
    STEAM_VR(250820, "SteamVR"),
    HALFLIFE_SDK(254430, "Half-Life SDK"),
    VR_MONITOR(366490, "VRMonitor", "SteamVR"),
    THE_LAB(450390, "The Lab"),
    DESTINATIONS(453170, "Destinations"),
    DESTINATIONS_WORKSHOP_TOOLS(469960, "Destinations - Workshop Tools", "Destinations"),
    HALFLIFE_ALYX(546560, "Half-Life: Alyx", "Half-Life Alyx"),
    ARTIFACT_CLASSIC(583950, "Artifact Classic", "Artifact"),
    HALFLIFE_OWNERSHIP(635640, "Half-Life Ownership"),
    APERTURE_HAND_LAB(868020, "Aperture Hand Lab", "Knux"),
    MOONDUST_KNUCKLES_TECH_DEMO(887260, "Moondust: Knuckles Tech Demos", "SteamVR Knuckles Tech Demos"),
    DOTA_UNDERLORDS(1046930, "Dota Underlords", "Underlords"),
    ARTIFACT_FOUNDRY(1269260, "Artifact Foundry", "Artifact 2.0");

    private static final Map<Integer, SteamApp> VALUES;

    static {
        Map<Integer, SteamApp> values = new HashMap<>();
        for (SteamApp app : values()) {
            if (values.put(app.id, app) != null) {
                throw new IllegalStateException("duplicate id");
            }
        }

        VALUES = Map.copyOf(values);
    }

    private final int id;
    private final String name;
    private final String installDir;

    SteamApp(int id, String name) {
        this(id, name, name);
    }

    SteamApp(int id, String name, String installDir) {
        this.id = id;
        this.name = name;
        this.installDir = installDir;
    }

    public static SteamApp findById(int id) {
        return VALUES.get(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstallDir() {
        return installDir;
    }
}
