package io.github.ititus.steam_api.remote_storage;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.ititus.steam_api.AbstractResult;
import io.github.ititus.steam_api.Result;
import io.github.ititus.steam_api.json.IdAdapter;

import java.util.List;
import java.util.NoSuchElementException;

public class CollectionDetails extends AbstractResult {

    @SerializedName("publishedfileid")
    private final long publishedFileId;

    private final List<Child> children;

    public CollectionDetails(Result result, long publishedFileId, List<Child> children) {
        super(result);
        this.publishedFileId = publishedFileId;
        this.children = children;
    }

    public long getPublishedFileId() {
        return publishedFileId;
    }

    public List<Child> getChildren() {
        return children;
    }

    /**
     * enum EWorkshopFileType
     */
    public enum FileType implements IdAdapter.HasId {

        Community(0, "normal Workshop item that can be subscribed to"),
        Microtransaction(1, "Workshop item that is meant to be voted on for the purpose of selling in-game"),
        Collection(2, "a collection of Workshop or Greenlight items"),
        Art(3, "artwork"),
        Video(4, "external video"),
        Screenshot(5, "screenshot"),
        Game(6, "Greenlight game entry"),
        Software(7, "Greenlight software entry"),
        Concept(8, "Greenlight concept"),
        WebGuide(9, "Steam web guide"),
        IntegratedGuide(10, "application integrated guide"),
        Merch(11, "Workshop merchandise meant to be voted on for the purpose of being sold"),
        ControllerBinding(12, "Steam Controller bindings"),
        SteamworksAccessInvite(13, "internal"),
        SteamVideo(14, "Steam video"),
        GameManagedItem(15, "managed completely by the game, not the user, and not shown on the web");

        private static final FileType[] VALUES = values();

        private final String description;

        FileType(int id, String description) {
            this.description = description;
            if (id != ordinal()) {
                throw new IllegalArgumentException("id must be equal to ordinal");
            }
        }

        public static FileType findById(int id) {
            if (id < 0 || id >= VALUES.length) {
                throw new NoSuchElementException("FileType with the given id does not exist");
            }

            return VALUES[id];
        }

        @Override
        public int getId() {
            return ordinal();
        }

        public String getDescription() {
            return description;
        }

        public static class ById extends IdAdapter<FileType> {

            public ById() {
                super(FileType::findById);
            }
        }
    }

    public static class Child {

        @SerializedName("publishedfileid")
        private final long publishedFileId;

        @SerializedName("sortorder")
        private final int sortOrder;

        @SerializedName("filetype")
        @JsonAdapter(FileType.ById.class)
        private final FileType fileType;

        public Child(long publishedFileId, int sortOrder, FileType fileType) {
            this.publishedFileId = publishedFileId;
            this.sortOrder = sortOrder;
            this.fileType = fileType;
        }

        public long getPublishedFileId() {
            return publishedFileId;
        }

        public int getSortOrder() {
            return sortOrder;
        }

        public FileType getFileType() {
            return fileType;
        }
    }
}
