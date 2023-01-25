package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.common.Child;
import io.github.ititus.valve_tools.steam_web_api.published_file_service.PublishedFileDetails;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@CommandLine.Command(name = "collection")
public final class ListAllMaps implements Callable<Integer> {

    @CommandLine.Option(names = "--key")
    private String apiKey;

    @CommandLine.Parameters(index = "0")
    private long collectionId;

    private ListAllMaps() {}

    @Override
    public Integer call() {
        var builder = SteamWebApi.builder();
        if (apiKey != null) {
            builder.apiKey(apiKey);
        }
        var publishedFileService = builder.build().publishedFileService();

        PublishedFileDetails result;
        try {
            result = publishedFileService.getDetails(true, true, true, true, true, false, true, true, null, -1, null, false, PublishedFileDetails.Revision.Default, true, collectionId);
        } catch (Exception e) {
            throw new RuntimeException("could not load collection details", e);
        }

        System.out.println("Collection has " + result.getChildren().size() + " maps");

        Map<Long, PublishedFileDetails> allDetails = Util.loadAllDetails(publishedFileService, result.getChildren().stream().mapToLong(Child::getPublishedFileId).toArray());
        AtomicInteger counter = new AtomicInteger();

        List<Pair<Long, PublishedFileDetails>> workshopMapFileDetails = allDetails.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .filter(p -> p.a() != null && p.b() != null)
                .toList();

        System.out.println();
        // Util.printMapsSorted(workshopMapFileDetails, (id, pfd) -> pfd.getTimeUpdated(), (id, pfd) -> counter.getAndIncrement() + ": workshop/" + pfd.getPublishedFileId() + "/" + PathUtil.getNameWithoutExtension(Path.of(pfd.getFileName())) + " - " + pfd.getTitle() + " - " + "https://steamcommunity.com/sharedfiles/filedetails/?id=" + pfd.getPublishedFileId() + " - " + pfd.getTags().stream().map(PublishedFileDetails.Tag::getTag).collect(Collectors.joining(", ", "[", "]")), t -> DateTimeFormatter.ISO_DATE.format(LocalDate.ofInstant(t, ZoneOffset.UTC)), true);
        Util.printMapsSorted(workshopMapFileDetails, (id, pfd) -> pfd.getSubscriptions(), (id, pfd) -> counter.getAndIncrement() + ": workshop/" + pfd.getPublishedFileId() + "/" + PathUtil.getNameWithoutExtension(Path.of(pfd.getFileName())) + " - " + pfd.getTitle() + " - " + "https://steamcommunity.com/sharedfiles/filedetails/?id=" + pfd.getPublishedFileId() + " - " + pfd.getTags().stream().map(PublishedFileDetails.Tag::getTag).collect(Collectors.joining(", ", "[", "]")), Objects::toString, true);

        return CommandLine.ExitCode.OK;
    }
}
