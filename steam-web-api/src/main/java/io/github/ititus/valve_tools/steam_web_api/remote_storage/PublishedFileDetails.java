package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.ititus.valve_tools.steam_web_api.BaseResult;
import io.github.ititus.valve_tools.steam_web_api.json.CBoolean;
import io.github.ititus.valve_tools.steam_web_api.json.EnumByOrdinal;
import io.github.ititus.valve_tools.steam_web_api.json.UnixTime;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * struct RemoteStorageGetPublishedFileDetailsResult_t
 */
public class PublishedFileDetails extends BaseResult {

/*
{
	enum { k_iCallback = k_iClientRemoteStorageCallbacks + 18 };
	int32 m_nPreviewFileSize;		// Size of the preview file
	EWorkshopFileType m_eFileType;	// Type of the file
	bool m_bAcceptedForUse;			// developer has specifically flagged this item as accepted in the Workshop
};
*/

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
    @JsonAdapter(EnumByOrdinal.class)
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
    private final Set<Tag> tags;

    @SuppressWarnings("unused")
    private PublishedFileDetails() {
        super(null);
        this.publishedFileId = 0;
        this.creator = 0;
        this.creatorAppId = 0;
        this.consumerAppId = 0;
        this.fileName = null;
        this.fileSize = 0;
        this.fileUrl = null;
        this.hcontentFile = 0;
        this.previewUrl = null;
        this.hcontentPreview = 0;
        this.title = null;
        this.description = null;
        this.timeCreated = null;
        this.timeUpdated = null;
        this.visibility = null;
        this.banned = false;
        this.banReason = null;
        this.subscriptions = 0;
        this.favorited = 0;
        this.lifetimeSubscriptions = 0;
        this.lifetimeFavorited = 0;
        this.views = 0;
        this.tags = null;
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

    public Set<Tag> getTags() {
        return tags;
    }

    public enum Visibility {

        Public,
        FriendsOnly,
        Private,
        Unlisted

    }

    public static final class Tag {

        private final String tag;

        @SuppressWarnings("unused")
        private Tag() {
            this.tag = null;
        }

        public Tag(String tag) {
            this.tag = Objects.requireNonNull(tag);
        }

        public String getTag() {
            return tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Tag tag = (Tag) o;
            return this.tag.equals(tag.tag);
        }

        @Override
        public int hashCode() {
            return tag.hashCode();
        }

        @Override
        public String toString() {
            return tag;
        }
    }
}
