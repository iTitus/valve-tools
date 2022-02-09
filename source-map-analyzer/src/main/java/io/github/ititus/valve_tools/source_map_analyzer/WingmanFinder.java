package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.valve_tools.source_map_lib.MapDirectory;
import io.github.ititus.valve_tools.source_map_lib.MapInfo;
import io.github.ititus.valve_tools.source_map_lib.WorkshopData;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.PublishedFileDetails;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.RemoteStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class WingmanFinder {

    private WingmanFinder() {
    }

    public static void main(String[] args) {
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

    private static Map<Long, PublishedFileDetails> loadAllDetails(RemoteStorage remoteStorage, List<MapInfo> maps) {
        long[] ids = maps.stream()
                .filter(MapInfo::isWorkshopMap)
                .map(MapInfo::getWorkshopData)
                .mapToLong(WorkshopData::getWorkshopId)
                .toArray();

        List<PublishedFileDetails> details;
        try {
            details = remoteStorage.getPublishedFileDetails(ids);
        } catch (SteamWebApiException ignored) {
            details = new ArrayList<>();
            for (long id : ids) {
                try {
                    details.add(remoteStorage.getPublishedFileDetails(id));
                } catch (SteamWebApiException e) {
                    MapInfo map = maps.stream()
                            .filter(MapInfo::isWorkshopMap)
                            .filter(m -> m.getWorkshopData().getWorkshopId() == id)
                            .findAny().orElseThrow();
                    new RuntimeException("Could not load workshop item " + map.getName() + " (" + map.getWorkshopData().getWorkshopId() + ")", e).printStackTrace();
                    details.add(null);
                }
            }
        }

        Map<Long, PublishedFileDetails> detailMap = new HashMap<>();
        int i = 0;
        for (PublishedFileDetails detail : details) {
            detailMap.put(ids[i++], detail);
        }

        return detailMap;
    }

    private static void sortMaps(Map<Long, PublishedFileDetails> details, List<MapInfo> maps) {
        List<Pair<MapInfo, PublishedFileDetails>> wingmanWorkshopMaps = maps.stream()
                .filter(MapInfo::isWorkshopMap)
                .map(m -> Pair.of(m, details.get(m.getWorkshopData().getWorkshopId())))
                .filter(p -> p.a() != null && p.b() != null)
                .collect(Collectors.toList());

        System.out.println("\nTime Created:");
        printPairsSorted(wingmanWorkshopMaps, PublishedFileDetails::getTimeCreated, WingmanFinder::mapToString, true);

        System.out.println("\nTime Updated:");
        printPairsSorted(wingmanWorkshopMaps, PublishedFileDetails::getTimeUpdated, WingmanFinder::mapToString, true);

        System.out.println("\nSubscriptions:");
        printPairsSorted(wingmanWorkshopMaps, PublishedFileDetails::getSubscriptions, WingmanFinder::mapToString, true);

        System.out.println("\nFavorited:");
        printPairsSorted(wingmanWorkshopMaps, PublishedFileDetails::getFavorited, WingmanFinder::mapToString, true);

        System.out.println("\nViews:");
        printPairsSorted(wingmanWorkshopMaps, PublishedFileDetails::getViews, WingmanFinder::mapToString, true);
    }

    private static String mapToString(MapInfo m) {
        return m.getFullName() + (m.isWorkshopMap() ? " - " + m.getWorkshopData().getWorkshopUrl() : "");
    }

    private static <A, B, C extends Comparable<? super C>> void printPairsSorted(
            List<? extends Pair<? extends A, ? extends B>> workshopMaps,
            Function<? super B, ? extends C> sorter,
            Function<? super A, ? extends String> toString,
            boolean reversed
    ) {
        Comparator<? super Pair<? extends A, ? extends B>> c = Comparator.comparing(p -> sorter.apply(p.b()));
        if (reversed) {
            c = c.reversed();
        }

        workshopMaps.stream()
                .sorted(c)
                .map(p -> toString.apply(p.a()) + " - " + sorter.apply(p.b()))
                .forEachOrdered(System.out::println);
    }
}
