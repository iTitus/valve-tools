package io.github.ititus.source_map_analyser;

import io.github.ititus.data.Lazy;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class WorkshopData implements Comparable<WorkshopData> {

    private final String mapName;
    private final long workshopId;
    private final Lazy<URL> workshopUrl;

    private WorkshopData(String mapName, long workshopId) {
        this.mapName = mapName;
        this.workshopId = workshopId;
        this.workshopUrl = Lazy.of(() -> {
            try {
                return new URL("https://steamcommunity.com/sharedfiles/filedetails/?id=" + workshopId);
            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static WorkshopData read(String name) {
        if (!name.startsWith("workshop/")) {
            if (name.indexOf('/') >= 0) {
                throw new IllegalArgumentException("non workshop map contains '/");
            }

            return null;
        }

        int start = "workshop/".length();
        int end = name.indexOf('/', start);
        if (end <= start) {
            throw new IllegalArgumentException("could not find workshop id");
        }

        long id = Long.parseLong(name, start, end, 10);
        String mapName = name.substring(end + 1);

        return new WorkshopData(mapName, id);
    }

    public String getMapName() {
        return mapName;
    }

    public long getWorkshopId() {
        return workshopId;
    }

    public URL getWorkshopUrl() {
        return workshopUrl.get();
    }

    @Override
    public int compareTo(WorkshopData o) {
        return Long.compare(workshopId, o.workshopId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkshopData that = (WorkshopData) o;
        return workshopId == that.workshopId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(workshopId);
    }

    @Override
    public String toString() {
        return "WorkshopData{" +
                "mapName='" + mapName + '\'' +
                ", workshopId=" + workshopId +
                '}';
    }
}
