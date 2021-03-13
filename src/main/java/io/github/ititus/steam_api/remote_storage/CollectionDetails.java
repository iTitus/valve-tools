package io.github.ititus.steam_api.remote_storage;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.ititus.steam_api.BaseResult;
import io.github.ititus.steam_api.Result;
import io.github.ititus.steam_api.json.EnumByOrdinal;

import java.util.List;

public class CollectionDetails extends BaseResult {

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
    public enum FileType {

        Community("normal Workshop item that can be subscribed to"),
        Microtransaction("Workshop item that is meant to be voted on for the purpose of selling in-game"),
        Collection("a collection of Workshop or Greenlight items"),
        Art("artwork"),
        Video("external video"),
        Screenshot("screenshot"),
        Game("Greenlight game entry"),
        Software("Greenlight software entry"),
        Concept("Greenlight concept"),
        WebGuide("Steam web guide"),
        IntegratedGuide("application integrated guide"),
        Merch("Workshop merchandise meant to be voted on for the purpose of being sold"),
        ControllerBinding("Steam Controller bindings"),
        SteamworksAccessInvite("internal"),
        SteamVideo("Steam video"),
        GameManagedItem("managed completely by the game, not the user, and not shown on the web");

        private final String description;

        FileType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class Child {

        @SerializedName("publishedfileid")
        private final long publishedFileId;

        @SerializedName("sortorder")
        private final int sortOrder;

        @SerializedName("filetype")
        @JsonAdapter(EnumByOrdinal.class)
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
