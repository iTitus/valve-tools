package io.github.ititus.source_map_analyser;

import info.ata4.bsplib.BspFile;
import info.ata4.bsplib.BspFileReader;
import info.ata4.bsplib.app.SourceApp;
import info.ata4.bsplib.entity.Entity;
import info.ata4.bsplib.struct.BspData;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public final class MapInfo implements Comparable<MapInfo> {

    private final Path path;
    private final String name;
    private final WorkshopData workshopData;
    private final Set<Lump> loadedLumps;
    private final BspData data;
    private SourceApp app;
    private Set<String> entityClasses;

    private MapInfo(Path path, String name) {
        this.path = path;
        this.name = name;
        this.workshopData = WorkshopData.read(name);
        this.loadedLumps = EnumSet.noneOf(Lump.class);
        this.data = new BspData();
        this.app = SourceApp.UNKNOWN;
        this.entityClasses = null;
    }

    public static MapInfo of(Path path) {
        Path real;
        try {
            real = Objects.requireNonNull(path).toRealPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("given path is not a file");
        }

        String name = real.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            name = name.substring(0, lastDot);
        }

        int count = real.getNameCount();
        if (count >= 3) {
            String parent1 = real.getName(count - 2).toString();
            String parent2 = real.getName(count - 3).toString();
            if ("workshop".equals(parent2)) {
                name = "workshop/" + parent1 + "/" + name;
            }
        }

        return new MapInfo(path, name);
    }

    public static MapInfo of(Path path, String name) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(name);

        Path real;
        try {
            real = path.toRealPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("given path is not a file");
        }

        return new MapInfo(real, name);
    }

    public Path getPath() {
        return path;
    }

    public String getFullName() {
        return name;
    }

    public boolean isWorkshopMap() {
        return workshopData != null;
    }

    public String getName() {
        return isWorkshopMap() ? workshopData.getMapName() : name;
    }

    public WorkshopData getWorkshopData() {
        return workshopData;
    }

    public SourceApp getApp() {
        return app;
    }

    public Set<String> getEntityClasses() {
        return entityClasses;
    }

    public BspData getData() {
        return data;
    }

    public boolean isWingmanOnlyMap() {
        return countBombsites() == 1 && hasWingmanBuyzonesForBothTeams();
    }

    public boolean isNormalMapWithWingmanSupport() {
        return isWingmanCompatible() && !isWingmanOnlyMap();
    }

    public boolean hasEntityNamedForWingman() {
        return hasEntity(e -> e.getTargetName() != null
                && (e.getTargetName().contains("2v2") || e.getTargetName().contains("wingman")));
    }

    public boolean isWingmanCompatible() {
        return hasStandardWingmanActivationScript() || hasEntityNamedForWingman() || isWingmanOnlyMap();
    }

    private long countBombsites() {
        return countEntities(e -> "func_bomb_target".equals(e.getClassName()));
    }

    private boolean hasStandardWingmanActivationScript() {
        return hasEntity(e -> {
            if (!"logic_script".equals(e.getClassName())) {
                return false;
            }

            String vscripts = e.getValue("vscripts");
            return vscripts != null && vscripts.contains("2v2_enable.nut");
        });
    }

    private boolean hasWingmanBuyzonesT() {
        return hasEntity(e -> {
            if (!"func_buyzone".equals(e.getClassName())) {
                return false;
            } else if (e.getTargetName() != null && !"buyzone.2v2".equals(e.getTargetName())) {
                return false;
            }

            return "2".equals(e.getValue("TeamNum"));
        });
    }

    private boolean hasWingmanBuyzonesCT() {
        return hasEntity(e -> {
            if (!"func_buyzone".equals(e.getClassName())) {
                return false;
            } else if (e.getTargetName() != null && !"buyzone.2v2".equals(e.getTargetName())) {
                return false;
            }

            return "3".equals(e.getValue("TeamNum"));
        });
    }

    private boolean hasWingmanBuyzonesForBothTeams() {
        return hasWingmanBuyzonesT() && hasWingmanBuyzonesCT();
    }

    public long countEntities(Predicate<Entity> predicate) {
        load(Lump.ENTITIES);
        return data.entities.stream()
                .filter(predicate)
                .count();
    }

    public boolean hasEntity(Predicate<Entity> predicate) {
        load(Lump.ENTITIES);
        return data.entities.stream()
                .anyMatch(predicate);
    }

    public boolean containsEntity(String className) {
        load(Lump.ENTITIES);
        return entityClasses.contains(className);
    }

    public void unload() {
        for (Iterator<Lump> it = loadedLumps.iterator(); it.hasNext(); ) {
            Lump l = it.next();
            l.unload(data);
            if (l == Lump.ENTITIES) {
                app = SourceApp.UNKNOWN;
                entityClasses = null;
            }

            it.remove();
        }
    }

    public void reload() {
        if (loadedLumps.isEmpty()) {
            return;
        }

        Lump[] lumps = loadedLumps.toArray(Lump[]::new);
        unload();
        load(lumps);
    }

    public void loadAll() {
        load(Lump.ALL);
    }

    public void load(Lump... additionalModes) {
        if (additionalModes.length == 0) {
            return;
        }

        BspFileReader reader = null;
        try {
            for (Lump lump : additionalModes) {
                if (lump == null || loadedLumps.contains(lump)) {
                    continue;
                } else if (reader == null) {
                    reader = new BspFileReader(new BspFile(path), data);
                }

                lump.load(reader);
                if (lump == Lump.ENTITIES) {
                    app = reader.getBspFile().getSourceApp();
                    entityClasses = reader.getEntityClassSet();
                }

                loadedLumps.add(lump);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MapInfo that = (MapInfo) o;
        return path.equals(that.path);
    }

    @Override
    public int compareTo(MapInfo o) {
        boolean w1 = isWorkshopMap();
        boolean w2 = o.isWorkshopMap();
        if (w1 ^ w2) {
            return w1 ? 1 : -1;
        }

        return getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "MapInfo{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                (isWorkshopMap() ? ", workshopData=" + workshopData : "") +
                '}';
    }
}
