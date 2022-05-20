package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.valve_tools.source_map_lib.MapInfo;
import io.github.ititus.valve_tools.source_map_lib.WorkshopData;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.PublishedFileDetails;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.RemoteStorage;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Util {

    private Util() {}

    public static Map<Long, PublishedFileDetails> loadAllDetails(RemoteStorage remoteStorage, List<MapInfo> maps) {
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
                    new RuntimeException("Could not load workshop item " + map.getFullName() + " (" + map.getWorkshopData().getWorkshopId() + ")", e).printStackTrace();
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

    public static Map<Long, PublishedFileDetails> loadAllDetails(RemoteStorage remoteStorage, long... mapIds) {
        List<PublishedFileDetails> details;
        try {
            details = remoteStorage.getPublishedFileDetails(mapIds);
        } catch (SteamWebApiException ignored) {
            details = new ArrayList<>();
            for (long id : mapIds) {
                try {
                    details.add(remoteStorage.getPublishedFileDetails(id));
                } catch (SteamWebApiException e) {
                    new RuntimeException("Could not load workshop item " + id, e).printStackTrace();
                    details.add(null);
                }
            }
        }

        Map<Long, PublishedFileDetails> detailMap = new HashMap<>();
        int i = 0;
        for (PublishedFileDetails detail : details) {
            detailMap.put(mapIds[i++], detail);
        }

        return detailMap;
    }

    public static String mapToString(MapInfo m) {
        return m.getFullName() + (m.isWorkshopMap() ? " - " + m.getWorkshopData().getWorkshopUrl() : "");
    }

    public static <C extends Comparable<? super C>> void printMapsSorted(
            List<Pair<MapInfo, PublishedFileDetails>> list,
            BiFunction<MapInfo, PublishedFileDetails, ? extends C> sortKeyExtractor,
            boolean reversed
    ) {
        printMapsSorted(list, sortKeyExtractor, Objects::toString, reversed);
    }

    public static <C extends Comparable<? super C>> void printMapsSorted(
            List<Pair<MapInfo, PublishedFileDetails>> workshopMaps,
            BiFunction<MapInfo, PublishedFileDetails, ? extends C> sortKeyExtractor,
            Function<? super C, ? extends String> sortKeyToString,
            boolean reversed
    ) {
        printMapsSorted(workshopMaps, sortKeyExtractor, (mi, pfd) -> mapToString(mi) + " - " + pfd.getTitle(), sortKeyToString, reversed);
    }

    public static <A, B, C extends Comparable<? super C>> void printMapsSorted(
            List<Pair<A, B>> workshopMaps,
            BiFunction<A, B, ? extends C> sortKeyExtractor,
            BiFunction<? super A, ? super B, ? extends String> mapToString,
            Function<? super C, ? extends String> sortKeyToString,
            boolean reversed
    ) {
        Comparator<Pair<A, B>> c = Comparator.comparing(p -> sortKeyExtractor.apply(p.a(), p.b()));
        if (reversed) {
            c = c.reversed();
        }

        workshopMaps.stream()
                .sorted(c)
                .map(p -> mapToString.apply(p.a(), p.b()) + " - " + sortKeyToString.apply(sortKeyExtractor.apply(p.a(), p.b())))
                .forEachOrdered(System.out::println);
    }
}
