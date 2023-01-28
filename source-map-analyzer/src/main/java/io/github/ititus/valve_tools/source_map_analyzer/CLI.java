package io.github.ititus.valve_tools.source_map_analyzer;

import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {CommandLine.HelpCommand.class, ListAllMaps.class, WingmanFinder.class, Vpk.class}
)
public final class CLI {

    private CLI() {}

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static void main(String[] args) {
        System.exit(new CommandLine(new CLI()).execute(args));
    }
}
