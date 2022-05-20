package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.CollectionDetails;
import io.github.ititus.valve_tools.steam_web_api.remote_storage.PublishedFileDetails;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class ListAllMaps {

    private ListAllMaps() {}

    public static void main(String[] args) {
        var remoteStorage = SteamWebApi.create().remoteStorage();
        CollectionDetails result;
        try {
            result = remoteStorage.getCollectionDetails(2803872184L);
        } catch (Exception e) {
            throw new RuntimeException("could not load collection details", e);
        }

        Map<Long, PublishedFileDetails> allDetails = Util.loadAllDetails(remoteStorage, result.getChildren().stream().mapToLong(CollectionDetails.Child::getPublishedFileId).toArray());
        AtomicInteger counter = new AtomicInteger();

        List<Pair<Long, PublishedFileDetails>> workshopMapFileDetails = allDetails.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .filter(p -> p.a() != null && p.b() != null)
                .toList();

        // Util.printMapsSorted(workshopMapFileDetails, (id, pfd) -> pfd.getTimeUpdated(), (id, pfd) -> counter.getAndIncrement() + ": workshop/" + id + "/" + PathUtil.getNameWithoutExtension(Path.of(pfd.getFileName())) + " - " + pfd.getTitle() + " - " + "https://steamcommunity.com/sharedfiles/filedetails/?id=" + pfd.getPublishedFileId(, t -> DateTimeFormatter.ISO_DATE.format(LocalDate.ofInstant(t, ZoneOffset.UTC)), true);
        Util.printMapsSorted(workshopMapFileDetails, (id, pfd) -> pfd.getSubscriptions(), (id, pfd) -> counter.getAndIncrement() + ": workshop/" + pfd.getPublishedFileId() + "/" + PathUtil.getNameWithoutExtension(Path.of(pfd.getFileName())) + " - " + pfd.getTitle() + " - " + "https://steamcommunity.com/sharedfiles/filedetails/?id=" + pfd.getPublishedFileId() + " - " + pfd.getTags(), Objects::toString, true);
    }
}
