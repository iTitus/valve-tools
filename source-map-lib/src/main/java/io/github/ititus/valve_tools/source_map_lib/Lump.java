package io.github.ititus.valve_tools.source_map_lib;

import info.ata4.bspsrc.lib.BspFileReader;
import info.ata4.bspsrc.lib.lump.LumpType;
import info.ata4.bspsrc.lib.struct.BspData;

import java.util.*;
import java.util.function.Consumer;

import static info.ata4.bspsrc.lib.lump.LumpType.*;

public enum Lump {

    // LUMP_UNKNOWN
    ENTITIES(BspFileReader::loadEntities, data -> data.entities = null, LUMP_ENTITIES),
    PLANES(BspFileReader::loadPlanes, data -> data.planes = null, LUMP_PLANES),
    TEX_DATA(BspFileReader::loadTexData, data -> {
        data.texdatas = null;
        data.texnames = null;
    }, LUMP_TEXDATA, LUMP_TEXDATA_STRING_TABLE, LUMP_TEXDATA_STRING_DATA),
    VERTICES(BspFileReader::loadVertices, data -> data.verts = null, LUMP_VERTEXES),
    // LUMP_VISIBILITY
    NODES(BspFileReader::loadNodes, data -> data.nodes = null, LUMP_NODES),
    TEX_INFO(BspFileReader::loadTexInfo, data -> data.texinfos = null, LUMP_TEXINFO),
    FACES(BspFileReader::loadFaces, data -> data.faces = null, LUMP_FACES, LUMP_FACES_HDR),
    // LUMP_LIGHTING
    OCCLUDERS(BspFileReader::loadOccluders, data -> {
        data.occluderDatas = null;
        data.occluderPolyDatas = null;
        data.occluderVerts = null;
    }, LUMP_OCCLUSION),
    LEAVES(BspFileReader::loadLeaves, data -> data.leaves = null, LUMP_LEAFS),
    EDGES(BspFileReader::loadEdges, data -> data.edges = null, LUMP_EDGES),
    // LUMP_UNDEFINED
    SURFACE_EDGES(BspFileReader::loadSurfaceEdges, data -> data.surfEdges = null, LUMP_SURFEDGES),
    MODELS(BspFileReader::loadModels, data -> data.models = null, LUMP_MODELS),
    // LUMP_WORLDLIGHTS
    LEAF_FACES(BspFileReader::loadLeafFaces, data -> data.leafFaces = null, LUMP_LEAFFACES),
    LEAF_BRUSHES(BspFileReader::loadLeafBrushes, data -> data.leafBrushes = null, LUMP_LEAFBRUSHES),
    BRUSHES(BspFileReader::loadBrushes, data -> data.brushes = null, LUMP_BRUSHES),
    BRUSH_SIDES(BspFileReader::loadBrushSides, data -> data.brushSides = null, LUMP_BRUSHSIDES),
    // LUMP_AREAS
    AREAPORTALS(BspFileReader::loadAreaportals, data -> data.areaportals = null, LUMP_AREAPORTALS),
    // LUMP_PORTALS
    // LUMP_CLUSTERS
    // LUMP_PORTALVERTS
    // LUMP_CLUSTERPORTALS
    DISP_INFOS(BspFileReader::loadDispInfos, data -> data.dispinfos = null, LUMP_DISPINFO),
    ORIGINAL_FACES(BspFileReader::loadOriginalFaces, data -> data.origFaces = null, LUMP_ORIGINALFACES),
    // LUMP_UNUSED
    // LUMP_PHYSCOLLIDE
    // LUMP_VERTNORMALS
    // LUMP_VERTNORMALINDICES
    // LUMP_DISP_LIGHTMAP_ALPHAS
    DISP_VERTICES(BspFileReader::loadDispVertices, data -> data.dispverts = null, LUMP_DISP_VERTS),
    // LUMP_DISP_LIGHTMAP_SAMPLE_POSITIONS
    // LUMP_GAME_LUMP
    // LUMP_LEAFWATERDATA
    PRIMITIVES(BspFileReader::loadPrimitives, data -> data.prims = null, LUMP_PRIMITIVES),
    PRIM_INDICES(BspFileReader::loadPrimIndices, data -> data.primIndices = null, LUMP_PRIMINDICES),
    PRIM_VERTS(BspFileReader::loadPrimVerts, data -> data.primVerts = null, LUMP_PRIMVERTS),
    // LUMP_PAKFILE
    CLIP_PORTAL_VERTICES(BspFileReader::loadClipPortalVertices, data -> data.clipPortalVerts = null, LUMP_CLIPPORTALVERTS),
    CUBEMAPS(BspFileReader::loadCubemaps, data -> data.cubemaps = null, LUMP_CUBEMAPS),
    // LUMP_TEXDATA_STRING_DATA (included in TEX_DATA)
    // LUMP_TEXDATA_STRING_TABLE (included in TEX_DATA)
    OVERLAYS(BspFileReader::loadOverlays, data -> {
        data.overlays = null;
        data.overlayFades = null;
        data.overlaySysLevels = null;
    }, LUMP_OVERLAYS, LUMP_OVERLAY_FADES, LUMP_OVERLAY_SYSTEM_LEVELS),
    // LUMP_LEAFMINDISTTOWATER
    // LUMP_FACE_MACRO_TEXTURE_INFO
    DISP_TRIANGLE_TAGS(BspFileReader::loadDispTriangleTags, data -> data.disptris = null, LUMP_DISP_TRIS),
    // LUMP_PHYSCOLLIDESURFACE
    // LUMP_PROPCOLLISION
    // LUMP_PROPHULLS
    // LUMP_PROPHULLVERTS
    // LUMP_PROPTRIS
    // LUMP_PROP_BLOB
    // LUMP_PHYSLEVEL
    DISP_MULTI_BLEND(BspFileReader::loadDispMultiBlend, data -> data.dispmultiblend = null, LUMP_DISP_MULTIBLEND),
    // LUMP_FACEIDS
    // LUMP_UNUSED0
    // LUMP_UNUSED1
    // LUMP_UNUSED2
    // LUMP_UNUSED3
    // LUMP_PHYSDISP
    // LUMP_WATEROVERLAYS
    // LUMP_LEAF_AMBIENT_INDEX_HDR
    // LUMP_LEAF_AMBIENT_INDEX
    // LUMP_LIGHTING_HDR
    // LUMP_WORLDLIGHTS_HDR
    // LUMP_LEAF_AMBIENT_LIGHTING_HDR
    // LUMP_LEAF_AMBIENT_LIGHTING
    // LUMP_XZIPPAKFILE
    // LUMP_FACES_HDR (included in FACES)
    FLAGS(BspFileReader::loadFlags, data -> data.mapFlags = null, LUMP_MAP_FLAGS),
    // LUMP_OVERLAY_FADES (included in OVERLAYS)
    // LUMP_OVERLAY_SYSTEM_LEVELS (included in OVERLAYS)
    STATIC_PROPS(BspFileReader::loadStaticProps, data -> {
        data.staticPropName = null;
        data.staticProps = null;
        data.staticPropLeaf = null;
    }), // uses game lump sprp
    ;

    public static final List<Lump> ALL = List.of(values());

    private final Consumer<BspFileReader> loadFunction;
    private final Consumer<BspData> unloadFunction;
    private final Set<LumpType> types;

    Lump(Consumer<BspFileReader> loadFunction, Consumer<BspData> unloadFunction, LumpType... types) {
        this.loadFunction = loadFunction;
        this.unloadFunction = unloadFunction;
        this.types = toUnmodifiableSet(types);
    }

    @SafeVarargs
    private static <T extends Enum<T>> Set<T> toUnmodifiableSet(T... ts) {
        if (ts.length == 0) {
            return Set.of();
        }

        EnumSet<T> s = EnumSet.of(ts[0]);
        s.addAll(Arrays.asList(ts).subList(1, ts.length));
        return Collections.unmodifiableSet(s);
    }

    public Set<LumpType> getTypes() {
        return types;
    }

    public void load(BspFileReader reader) {
        loadFunction.accept(reader);
    }

    public void unload(BspData data) {
        unloadFunction.accept(data);
    }
}
