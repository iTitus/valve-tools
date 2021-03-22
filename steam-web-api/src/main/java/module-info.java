module io.github.ititus.valve_tools.steam_web_api {
    requires java.net.http;
    requires jdk.crypto.ec; // this is required to talk to servers with an elliptic curve certificate
    requires jdk.crypto.cryptoki;
    requires jdk.crypto.mscapi;
    requires io.github.ititus.commons;
    requires com.google.gson;

    exports io.github.ititus.valve_tools.steam_web_api;
    exports io.github.ititus.valve_tools.steam_web_api.exception;
    exports io.github.ititus.valve_tools.steam_web_api.remote_storage;

    opens io.github.ititus.valve_tools.steam_web_api to com.google.gson;
    opens io.github.ititus.valve_tools.steam_web_api.json to com.google.gson;
    opens io.github.ititus.valve_tools.steam_web_api.remote_storage to com.google.gson;
}
