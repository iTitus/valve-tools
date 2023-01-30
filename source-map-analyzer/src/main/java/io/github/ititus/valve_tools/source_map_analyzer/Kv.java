package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.kv.KeyValues;
import io.github.ititus.valve_tools.steam_api.SteamInstallation;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@SuppressWarnings("unused")
@CommandLine.Command(name = "kv", mixinStandardHelpOptions = true)
class Kv implements Callable<Integer> {

    private Kv() {
    }

    @Override
    public Integer call() throws Exception {
        var steamDir = SteamInstallation.find().getSteamDir();
        var file = PathUtil.resolveRealFile(steamDir.resolve("steamapps/libraryfolders.vdf"));
        var kv = KeyValues.parseText(file, KeyValues.Settings.simple());
        System.out.println(kv);
        return CommandLine.ExitCode.OK;
    }
}
