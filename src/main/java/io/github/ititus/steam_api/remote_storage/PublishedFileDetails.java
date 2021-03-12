package io.github.ititus.steam_api.remote_storage;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.ititus.steam_api.AbstractResult;
import io.github.ititus.steam_api.Result;
import io.github.ititus.steam_api.json.CBoolean;
import io.github.ititus.steam_api.json.IdAdapter;
import io.github.ititus.steam_api.json.UnixTime;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * struct RemoteStorageGetPublishedFileDetailsResult_t
 */
public class PublishedFileDetails extends AbstractResult {

/*
{
	enum { k_iCallback = k_iClientRemoteStorageCallbacks + 18 };
	int32 m_nPreviewFileSize;		// Size of the preview file
	EWorkshopFileType m_eFileType;	// Type of the file
	bool m_bAcceptedForUse;			// developer has specifically flagged this item as accepted in the Workshop
};*/

    /**
     * PublishedFileId_t m_nPublishedFileId;
     */
    @SerializedName("publishedfileid")
    private final long publishedFileId;

    /**
     * uint64 m_ulSteamIDOwner; // Steam ID of the user who created this content.
     */
    private final long creator;

    /**
     * AppId_t m_nCreatorAppID; // ID of the app that created this file.
     */
    @SerializedName("creator_app_id")
    private final int creatorAppId;

    /**
     * AppId_t m_nConsumerAppID; // ID of the app that will consume this file.
     */
    @SerializedName("consumer_app_id")
    private final int consumerAppId;

    /**
     * char m_pchFileName[k_cchFilenameMax]; // The name of the primary file
     */
    @SerializedName("filename")
    private final String fileName;

    /**
     * int32 m_nFileSize; // Size of the primary file
     */
    @SerializedName("file_size")
    private final int fileSize;

    /**
     * char m_rgchURL[k_cchPublishedFileURLMax]; // URL (for a video or a website)
     */
    @SerializedName("file_url")
    private final String fileUrl;

    /**
     * UGCHandle_t m_hFile; // The handle of the primary file
     */
    @SerializedName("hcontent_file")
    private final long hcontentFile;

    @SerializedName("preview_url")
    private final String previewUrl;

    /**
     * UGCHandle_t m_hPreviewFile; // The handle of the preview file
     */
    @SerializedName("hcontent_preview")
    private final long hcontentPreview;

    /**
     * char m_rgchTitle[k_cchPublishedDocumentTitleMax]; // title of document
     */
    private final String title;

    /**
     * char m_rgchDescription[k_cchPublishedDocumentDescriptionMax]; // description of document
     */
    private final String description;

    /**
     * uint32 m_rtimeCreated; // time when the published file was created
     */
    @SerializedName("time_created")
    @JsonAdapter(UnixTime.Seconds.class)
    private final Instant timeCreated;

    /**
     * uint32 m_rtimeUpdated; // time when the published file was last updated
     */
    @SerializedName("time_updated")
    @JsonAdapter(UnixTime.Seconds.class)
    private final Instant timeUpdated;

    /**
     * ERemoteStoragePublishedFileVisibility m_eVisibility;
     */
    @JsonAdapter(Visibility.ById.class)
    private final Visibility visibility;

    /**
     * bool m_bBanned;
     */
    @JsonAdapter(CBoolean.class)
    private final boolean banned;

    @SerializedName("ban_reason")
    private final String banReason;

    private final int subscriptions;

    private final int favorited;

    @SerializedName("lifetime_subscriptions")
    private final int lifetimeSubscriptions;

    @SerializedName("lifetime_favorited")
    private final int lifetimeFavorited;

    private final int views;

    /**
     * char m_rgchTags[k_cchTagListMax]; // comma separated list of all tags associated with this file <br>
     * bool m_bTagsTruncated; // whether the list of tags was too long to be returned in the provided buffer
     */
    @JsonAdapter(TagAdapter.class)
    private final Set<String> tags;

    public PublishedFileDetails(Result result, long publishedFileId, long creator, int creatorAppId,
                                int consumerAppId, String fileName, int fileSize, String fileUrl, long hcontentFile,
                                String previewUrl, long hcontentPreview, String title, String description,
                                Instant timeCreated, Instant timeUpdated, Visibility visibility, boolean banned,
                                String banReason, int subscriptions, int favorited, int lifetimeSubscriptions,
                                int lifetimeFavorited, int views, Set<String> tags) {
        super(result);
        this.publishedFileId = publishedFileId;
        this.creator = creator;
        this.creatorAppId = creatorAppId;
        this.consumerAppId = consumerAppId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.hcontentFile = hcontentFile;
        this.previewUrl = previewUrl;
        this.hcontentPreview = hcontentPreview;
        this.title = title;
        this.description = description;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.visibility = visibility;
        this.banned = banned;
        this.banReason = banReason;
        this.subscriptions = subscriptions;
        this.favorited = favorited;
        this.lifetimeSubscriptions = lifetimeSubscriptions;
        this.lifetimeFavorited = lifetimeFavorited;
        this.views = views;
        this.tags = tags;
    }

    public long getPublishedFileId() {
        return publishedFileId;
    }

    public long getCreator() {
        return creator;
    }

    public int getCreatorAppId() {
        return creatorAppId;
    }

    public int getConsumerAppId() {
        return consumerAppId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public long getHcontentFile() {
        return hcontentFile;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public long getHcontentPreview() {
        return hcontentPreview;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public Instant getTimeUpdated() {
        return timeUpdated;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public boolean getBanned() {
        return banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public int getSubscriptions() {
        return subscriptions;
    }

    public int getFavorited() {
        return favorited;
    }

    public int getLifetimeSubscriptions() {
        return lifetimeSubscriptions;
    }

    public int getLifetimeFavorited() {
        return lifetimeFavorited;
    }

    public int getViews() {
        return views;
    }

    public Set<String> getTags() {
        return tags;
    }

    public enum Visibility implements IdAdapter.HasId {

        Public(0),
        FriendsOnly(1),
        Private(2),
        Unlisted(3);

        private static final Visibility[] VALUES = values();

        Visibility(int id) {
            if (id != ordinal()) {
                throw new IllegalArgumentException("id must be equal to ordinal");
            }
        }

        public static Visibility findById(int id) {
            if (id < 0 || id >= VALUES.length) {
                throw new NoSuchElementException("Visibility with the given id does not exist");
            }

            return VALUES[id];
        }

        @Override
        public int getId() {
            return ordinal();
        }

        public static class ById extends IdAdapter<Visibility> {

            public ById() {
                super(Visibility::findById);
            }
        }
    }

    public static class TagAdapter extends TypeAdapter<Set<String>> {

        @Override
        public void write(JsonWriter out, Set<String> value) throws IOException {
            out.beginArray();

            for (String tag : value) {
                out.beginObject();
                out.name("tag");
                out.value(tag);
                out.endObject();
            }

            out.endArray();
        }

        @Override
        public Set<String> read(JsonReader in) throws IOException {
            Set<String> tags = new HashSet<>();
            in.beginArray();

            while (in.hasNext()) {
                in.beginObject();
                if (!"tag".equals(in.nextName())) {
                    throw new IllegalStateException("expected 'tag'");
                }

                tags.add(in.nextString());
                in.endObject();
            }

            in.endArray();
            return tags;
        }
    }
}
