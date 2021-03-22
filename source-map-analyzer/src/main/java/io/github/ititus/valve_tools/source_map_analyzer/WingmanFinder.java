package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.data.pair.Pair;
import io.github.ititus.valve_tools.source_map_lib.MapDirectory;
import io.github.ititus.valve_tools.source_map_lib.MapInfo;
import io.github.ititus.valve_tools.source_map_lib.WorkshopData;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.PublishedFileDetails;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.RemoteStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class WingmanFinder {

    private WingmanFinder() {
    }

    public static void main(String[] args) throws Exception {
        MapDirectory mapDir = MapDirectory.csgo();
        List<MapInfo> maps = mapDir.findMaps();
        System.out.println("Found " + maps.size() + " maps");

        RemoteStorage remoteStorage = SteamWebApi.create().remoteStorage();
        Map<Long, PublishedFileDetails> allDetails = loadAllDetails(remoteStorage, maps);

        System.out.println();
        List<MapInfo> wingmanMaps = maps.stream()
                .filter(MapInfo::isWingmanOnlyMap)
                .collect(Collectors.toList());
        System.out.println(wingmanMaps.size() + " of those are Wingman-only maps:");
        wingmanMaps.stream()
                .map(WingmanFinder::mapToString)
                .forEachOrdered(System.out::println);

        sortMaps(allDetails, wingmanMaps);

        System.out.println();
        List<MapInfo> wingmanCompatibleMaps = maps.stream()
                .filter(MapInfo::isNormalMapWithWingmanSupport)
                .collect(Collectors.toList());
        System.out.println(wingmanCompatibleMaps.size() + " of those are Wingman compatible:");
        wingmanCompatibleMaps.stream()
                .map(WingmanFinder::mapToString)
                .forEachOrdered(System.out::println);

        sortMaps(allDetails, wingmanCompatibleMaps);
    }

    private static Map<Long, PublishedFileDetails> loadAllDetails(RemoteStorage remoteStorage, List<MapInfo> maps) throws SteamWebApiException {
        long[] ids = maps.stream()
                .filter(MapInfo::isWorkshopMap)
                .map(MapInfo::getWorkshopData)
                .mapToLong(WorkshopData::getWorkshopId)
                .toArray();

        List<PublishedFileDetails> details = remoteStorage.getPublishedFileDetails(ids);

        Map<Long, PublishedFileDetails> detailMap = new HashMap<>();
        for (int i = 0; i < ids.length; i++) {
            detailMap.put(ids[i], details.get(i));
        }

        return detailMap;
    }

    private static void sortMaps(Map<Long, PublishedFileDetails> details, List<MapInfo> maps) {
        List<Pair<MapInfo, PublishedFileDetails>> wingmanWorkshopMaps = maps.stream()
                .filter(MapInfo::isWorkshopMap)
                .map(m -> Pair.of(m, details.get(m.getWorkshopData().getWorkshopId())))
                .collect(Collectors.toList());

        System.out.println("\nTime Created:");
        printSorted(wingmanWorkshopMaps, PublishedFileDetails::getTimeCreated, true);

        System.out.println("\nTime Updated:");
        printSorted(wingmanWorkshopMaps, PublishedFileDetails::getTimeUpdated, true);

        System.out.println("\nSubscriptions:");
        printSorted(wingmanWorkshopMaps, PublishedFileDetails::getSubscriptions, true);

        System.out.println("\nFavorited:");
        printSorted(wingmanWorkshopMaps, PublishedFileDetails::getFavorited, true);

        System.out.println("\nViews:");
        printSorted(wingmanWorkshopMaps, PublishedFileDetails::getViews, true);
    }

    private static <T extends Comparable<? super T>> void printSorted(List<Pair<MapInfo, PublishedFileDetails>> workshopMaps, Function<PublishedFileDetails, ? extends T> sorter, boolean reversed) {
        Comparator<Pair<MapInfo, PublishedFileDetails>> c = Comparator.comparing(p -> sorter.apply(p.b()));
        if (reversed) {
            c = c.reversed();
        }

        workshopMaps.stream()
                .sorted(c)
                .map(p -> mapToString(p.a()) + " - " + sorter.apply(p.b()))
                .forEachOrdered(System.out::println);
    }

    private static String mapToString(MapInfo m) {
        return m.getFullName() + (m.isWorkshopMap() ? " - " + m.getWorkshopData().getWorkshopUrl() : "");
    }
}
