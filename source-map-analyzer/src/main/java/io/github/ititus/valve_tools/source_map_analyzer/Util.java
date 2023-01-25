package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.valve_tools.source_map_lib.MapInfo;
import io.github.ititus.valve_tools.source_map_lib.WorkshopData;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;
import io.github.ititus.valve_tools.steam_web_api.published_file_service.PublishedFileService;
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

        Map<Long, PublishedFileDetails> details = new LinkedHashMap<>();
        if (ids.length > 0) {
            try {
                for (var detail : remoteStorage.getPublishedFileDetails(ids)) {
                    details.put(detail.getPublishedFileId(), detail);
                }
            } catch (SteamWebApiException e1) {
                var ex = new RuntimeException("could not load all published file details", e1);
                for (var map : maps) {
                    if (map.isWorkshopMap()) {
                        var id = map.getWorkshopData().getWorkshopId();
                        try {
                            details.put(id, remoteStorage.getPublishedFileDetails(id));
                        } catch (SteamWebApiException e2) {
                            ex.addSuppressed(new RuntimeException("could not load published file details for map '" + map.getFullName() + "' with id '" + id + "'", e2));
                            details.put(id, null);
                        }
                    }
                }

                ex.printStackTrace();
            }
        }

        return details;
    }

    public static Map<Long, io.github.ititus.valve_tools.steam_web_api.published_file_service.PublishedFileDetails> loadAllDetails(PublishedFileService publishedFileService, long... ids) {
        Map<Long, io.github.ititus.valve_tools.steam_web_api.published_file_service.PublishedFileDetails> details = new LinkedHashMap<>();
        if (ids.length > 0) {
            try {
                for (var detail : publishedFileService.getDetails(true, true, true, true, true, false, true, true, null, -1, null, false, io.github.ititus.valve_tools.steam_web_api.published_file_service.PublishedFileDetails.Revision.Default, true, ids)) {
                    details.put(detail.getPublishedFileId(), detail);
                }
            } catch (SteamWebApiException e1) {
                var ex = new RuntimeException("could not load all published file details", e1);
                for (var id : ids) {
                    try {
                        details.put(id, publishedFileService.getDetails(true, true, true, true, true, false, true, true, null, -1, null, false, io.github.ititus.valve_tools.steam_web_api.published_file_service.PublishedFileDetails.Revision.Default, true, id));
                    } catch (SteamWebApiException e2) {
                        ex.addSuppressed(new RuntimeException("could not load published file details for id '" + id + "'", e2));
                        details.put(id, null);
                    }
                }

                ex.printStackTrace();
            }
        }

        return details;
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
