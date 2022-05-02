package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.valve_tools.source_map_lib.MapDirectory;
import io.github.ititus.valve_tools.source_map_lib.MapInfo;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.PublishedFileDetails;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.RemoteStorage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ListAllMaps {

    private ListAllMaps() {}

    public static void main(String[] args) {
        var mapDir = MapDirectory.csgo();
        var maps = mapDir.findMaps().stream()
                .filter(m -> !m.getName().startsWith("aim_"))
                .filter(m -> !m.getName().startsWith("yprac_"))
                .filter(m -> !m.getName().startsWith("awp_"))
                .filter(m -> !m.getName().startsWith("training_"))
                .filter(m -> !m.getName().startsWith("fps_"))
                .filter(m -> !m.getName().startsWith("csgohub_"))
                .filter(m -> !m.getName().startsWith("recoil_"))
                .filter(m -> !m.getName().startsWith("surf_"))
                .filter(m -> !m.getName().startsWith("bot_"))
                .filter(m -> !m.getName().startsWith("duel_"))
                .filter(m -> !m.getName().startsWith("dz_"))
                .filter(m -> !m.getName().startsWith("gd_"))
                .filter(m -> !m.getName().startsWith("ar_"))
                .filter(m -> !m.getName().startsWith("lobby_"))
                .filter(m -> !Set.of("testmap", "tmawp", "biocenter2", "1v1v1v1_nuke", "training1").contains(m.getName()))
                .sorted()
                .toList();
        System.out.println("Found " + maps.size() + " maps");

        RemoteStorage remoteStorage = SteamWebApi.create().remoteStorage();
        Map<Long, PublishedFileDetails> allDetails = Util.loadAllDetails(remoteStorage, maps);

        var builtinMaps = maps.stream()
                .filter(m -> !m.isWorkshopMap())
                .toList();
        System.out.println(builtinMaps.size() + " builtin maps");

        var workshopMaps = maps.stream()
                .filter(MapInfo::isWorkshopMap)
                .toList();
        System.out.println(workshopMaps.size() + " workshop maps");

        AtomicInteger counter = new AtomicInteger();

        System.out.println();
        builtinMaps.stream()
                .map(Util::mapToString)
                .forEachOrdered(m -> System.out.println(counter.getAndIncrement() + ": " + m));

        System.out.println();
        List<Pair<MapInfo, PublishedFileDetails>> workshopMapFileDetails = workshopMaps.stream()
                .map(m -> Pair.of(m, allDetails.get(m.getWorkshopData().getWorkshopId())))
                .filter(p -> p.a() != null && p.b() != null)
                .toList();

        // Util.printMapsSorted(workshopMapFileDetails, (mi, pfd) -> pfd.getTimeUpdated(), (mi, pfd) -> counter.getAndIncrement() + ": " + Util.mapToString(mi) + " - " + pfd.getTitle(), t -> DateTimeFormatter.ISO_DATE.format(LocalDate.ofInstant(t, ZoneOffset.UTC)), true);
        Util.printMapsSorted(workshopMapFileDetails, (mi, pfd) -> pfd.getSubscriptions(), (mi, pfd) -> counter.getAndIncrement() + ": " + Util.mapToString(mi) + " - " + pfd.getTitle(), Objects::toString, true);
    }
}
