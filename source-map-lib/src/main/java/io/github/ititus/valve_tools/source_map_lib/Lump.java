package io.github.ititus.valve_tools.source_map_lib;

import info.ata4.bsplib.BspFileReader;
import info.ata4.bsplib.struct.BspData;

import java.util.function.Consumer;

public enum Lump {

    ENTITIES(BspFileReader::loadEntities, data -> data.entities = null),
    VERTICES(BspFileReader::loadVertices, data -> data.verts = null),
    EDGES(BspFileReader::loadEdges, data -> data.edges = null),
    FACES(BspFileReader::loadFaces, data -> data.faces = null),
    ORIGINAL_FACES(BspFileReader::loadOriginalFaces, data -> data.origFaces = null),
    MODELS(BspFileReader::loadModels, data -> data.models = null),
    SURFACE_EDGES(BspFileReader::loadSurfaceEdges, data -> data.surfEdges = null),
    OCCLUDERS(BspFileReader::loadOccluders, data -> {
        data.occluderDatas = null;
        data.occluderPolyDatas = null;
        data.occluderVerts = null;
    }),
    TEX_INFO(BspFileReader::loadTexInfo, data -> data.texinfos = null),
    TEX_DATA(BspFileReader::loadTexData, data -> {
        data.texdatas = null;
        data.texnames = null;
    }),
    STATIC_PROPS(BspFileReader::loadStaticProps, data -> {
        data.staticProps = null;
        data.staticPropName = null;
        data.staticPropLeaf = null;
    }),
    CUBEMAPS(BspFileReader::loadCubemaps, data -> data.cubemaps = null),
    PLANES(BspFileReader::loadPlanes, data -> data.planes = null),
    BRUSHES(BspFileReader::loadBrushes, data -> data.brushes = null),
    BRUSH_SIDES(BspFileReader::loadBrushSides, data -> data.brushSides = null),
    AREAPORTALS(BspFileReader::loadAreaportals, data -> data.areaportals = null),
    PORTAL_VERTICES(BspFileReader::loadClipPortalVertices, data -> data.clipPortalVerts = null),
    DISP_INFOS(BspFileReader::loadDispInfos, data -> data.dispinfos = null),
    DISP_VERTICES(BspFileReader::loadDispVertices, data -> data.dispverts = null),
    DISP_TRIANGLE_TAGS(BspFileReader::loadDispTriangleTags, data -> data.disptris = null),
    DISP_MULTI_BLEND(BspFileReader::loadDispMultiBlend, data -> data.dispmultiblend = null),
    NODES(BspFileReader::loadNodes, data -> data.nodes = null),
    LEAVES(BspFileReader::loadLeaves, data -> data.leaves = null),
    LEAF_FACES(BspFileReader::loadLeafFaces, data -> data.leafFaces = null),
    LEAF_BRUSHES(BspFileReader::loadLeafBrushes, data -> data.leafBrushes = null),
    OVERLAYS(BspFileReader::loadOverlays, data -> {
        data.overlays = null;
        data.overlayFades = null;
        data.overlaySysLevels = null;
    }),
    FLAGS(BspFileReader::loadFlags, data -> data.mapFlags = null),
    PRIMITIVES(BspFileReader::loadPrimitives, data -> data.prims = null),
    PRIM_INDICES(BspFileReader::loadPrimIndices, data -> data.primIndices = null),
    PRIM_VERTS(BspFileReader::loadPrimVerts, data -> data.primVerts = null);

    public static final Lump[] ALL = values();

    private final Consumer<BspFileReader> loadFunction;
    private final Consumer<BspData> unloadFunction;

    Lump(Consumer<BspFileReader> loadFunction, Consumer<BspData> unloadFunction) {
        this.loadFunction = loadFunction;
        this.unloadFunction = unloadFunction;
    }

    public void load(BspFileReader reader) {
        loadFunction.accept(reader);
    }

    public void unload(BspData data) {
        unloadFunction.accept(data);
    }
}
