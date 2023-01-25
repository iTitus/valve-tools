package io.github.ititus.valve_tools.source_map_lib;

import info.ata4.bsplib.BspFile;
import info.ata4.bsplib.BspFileReader;
import info.ata4.bsplib.app.SourceAppId;
import info.ata4.bsplib.entity.Entity;
import info.ata4.bsplib.struct.BspData;
import io.github.ititus.commons.io.PathUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public final class MapInfo implements Comparable<MapInfo> {

    private final Path path;
    private final String name;
    private final WorkshopData workshopData;
    private final Set<Lump> loadedLumps;
    private final BspData data;
    private int appId;

    private MapInfo(Path path, String name) {
        this.path = path;
        this.name = name;
        this.workshopData = WorkshopData.read(name);
        this.loadedLumps = EnumSet.noneOf(Lump.class);
        this.data = new BspData();
        this.appId = SourceAppId.UNKNOWN;
    }

    public static MapInfo of(Path path) {
        Path real = PathUtil.resolveRealFile(path);
        String name = PathUtil.getNameWithoutExtension(real);

        int count = real.getNameCount();
        if (count >= 3) {
            String parent1 = real.getName(count - 2).toString();
            String parent2 = real.getName(count - 3).toString();
            if ("workshop".equals(parent2) && parent1.chars().allMatch(c -> '0' <= c && c <= '9')) {
                name = "workshop/" + parent1 + "/" + name;
            }
        }

        return new MapInfo(path, name);
    }

    public static MapInfo of(Path path, String name) {
        return new MapInfo(PathUtil.resolveRealFile(path), Objects.requireNonNull(name));
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

    public int getAppId() {
        return appId;
    }

    public BspData getData() {
        return data;
    }

    public boolean isWingmanOnlyMap() {
        return countBombsites() == 1 && hasWingmanBuyzonesForBothTeams() && !isYpracMap();
    }

    public boolean isNormalMapWithWingmanSupport() {
        return isWingmanCompatible() && !isWingmanOnlyMap();
    }

    public boolean hasEntityNamedForWingman() {
        return hasEntity(e -> e.getTargetName() != null && (e.getTargetName().contains("2v2") || e.getTargetName().contains("wingman")));
    }

    public boolean isWingmanCompatible() {
        return (hasStandardWingmanActivationScript() || hasEntityNamedForWingman() || isWingmanOnlyMap()) && !isYpracMap();
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

    private boolean isYpracMap() {
        return hasEntity(e -> e.getTargetName() != null && e.getTargetName().contains("yprac"));
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
        return hasEntity(e -> Objects.equals(e.getClassName(), className));
    }

    public void unload() {
        for (Iterator<Lump> it = loadedLumps.iterator(); it.hasNext(); ) {
            Lump l = it.next();
            l.unload(data);
            if (l == Lump.ENTITIES) {
                appId = SourceAppId.UNKNOWN;
            }

            it.remove();
        }
    }

    public void reload() {
        if (loadedLumps.isEmpty()) {
            return;
        }

        Set<Lump> lumps = EnumSet.copyOf(loadedLumps);
        unload();
        load(lumps);
    }

    public void loadAll() {
        load(Lump.ALL);
    }

    public void load(Lump additionalLump) {
        load(List.of(additionalLump));
    }

    public void load(Lump... additionalLumps) {
        load(Arrays.asList(additionalLumps));
    }

    public void load(Collection<Lump> additionalLumps) {
        if (additionalLumps.isEmpty()) {
            return;
        }

        BspFileReader reader = null;
        try {
            for (Lump lump : additionalLumps) {
                if (lump == null || loadedLumps.contains(lump)) {
                    continue;
                } else if (reader == null) {
                    reader = new BspFileReader(new BspFile(path), data);
                }

                lump.load(reader);
                if (lump == Lump.ENTITIES) {
                    appId = reader.getBspFile().getAppId();
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
