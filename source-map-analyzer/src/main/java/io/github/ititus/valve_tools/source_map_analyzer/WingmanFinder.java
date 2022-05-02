package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.valve_tools.source_map_lib.MapDirectory;
import io.github.ititus.valve_tools.source_map_lib.MapInfo;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.PublishedFileDetails;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.RemoteStorage;

import java.util.List;
import java.util.Map;

public final class WingmanFinder {

    private WingmanFinder() {
    }

    public static void main(String[] args) {
        MapDirectory mapDir = MapDirectory.csgo();
        List<MapInfo> maps = mapDir.findMaps();
        System.out.println("Found " + maps.size() + " maps");

        RemoteStorage remoteStorage = SteamWebApi.create().remoteStorage();
        Map<Long, PublishedFileDetails> allDetails = Util.loadAllDetails(remoteStorage, maps);

        System.out.println();
        List<MapInfo> wingmanMaps = maps.stream()
                .filter(MapInfo::isWingmanOnlyMap)
                .toList();
        System.out.println(wingmanMaps.size() + " of those are Wingman-only maps:");
        wingmanMaps.stream()
                .map(Util::mapToString)
                .forEachOrdered(System.out::println);

        sortAndPrintWorkshopMaps(allDetails, wingmanMaps);

        System.out.println();
        List<MapInfo> wingmanCompatibleMaps = maps.stream()
                .filter(MapInfo::isNormalMapWithWingmanSupport)
                .toList();
        System.out.println(wingmanCompatibleMaps.size() + " of those are Wingman compatible:");
        wingmanCompatibleMaps.stream()
                .map(Util::mapToString)
                .forEachOrdered(System.out::println);

        sortAndPrintWorkshopMaps(allDetails, wingmanCompatibleMaps);
    }

    private static void sortAndPrintWorkshopMaps(Map<Long, PublishedFileDetails> details, List<MapInfo> maps) {
        List<Pair<MapInfo, PublishedFileDetails>> wingmanWorkshopMaps = maps.stream()
                .filter(MapInfo::isWorkshopMap)
                .map(m -> Pair.of(m, details.get(m.getWorkshopData().getWorkshopId())))
                .filter(p -> p.a() != null && p.b() != null)
                .toList();

        System.out.println("\nTime Created:");
        Util.printMapsSorted(wingmanWorkshopMaps, (mi, pfd) -> pfd.getTimeCreated(), true);

        System.out.println("\nTime Updated:");
        Util.printMapsSorted(wingmanWorkshopMaps, (mi, pfd) -> pfd.getTimeUpdated(), true);

        System.out.println("\nSubscriptions:");
        Util.printMapsSorted(wingmanWorkshopMaps, (mi, pfd) -> pfd.getSubscriptions(), true);

        System.out.println("\nFavorited:");
        Util.printMapsSorted(wingmanWorkshopMaps, (mi, pfd) -> pfd.getFavorited(), true);

        System.out.println("\nViews:");
        Util.printMapsSorted(wingmanWorkshopMaps, (mi, pfd) -> pfd.getViews(), true);
    }
}
