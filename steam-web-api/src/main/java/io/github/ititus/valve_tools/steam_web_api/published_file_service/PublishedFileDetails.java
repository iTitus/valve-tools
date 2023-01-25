package io.github.ititus.valve_tools.steam_web_api.published_file_service;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.ititus.valve_tools.steam_web_api.common.BaseResult;
import io.github.ititus.valve_tools.steam_web_api.common.Child;
import io.github.ititus.valve_tools.steam_web_api.common.FileType;
import io.github.ititus.valve_tools.steam_web_api.common.Visibility;
import io.github.ititus.valve_tools.steam_web_api.json.*;

import java.time.Instant;
import java.util.List;

public final class PublishedFileDetails extends BaseResult {

    public static final int NOT_SCANNED = 0;
    public static final int RESET = 1;
    public static final int NEEDS_CHECKING = 2;
    public static final int VERY_UNLIKELY = 5;
    public static final int UNLIKELY = 30;
    public static final int POSSIBLE = 50;
    public static final int LIKELY = 75;
    public static final int VERY_LIKELY = 100;

    @SerializedName("publishedfileid")
    @JsonAdapter(UnsignedLong.class)
    private long publishedFileId;
    @JsonAdapter(UnsignedLong.class)
    private long creator;
    @SerializedName(value = "creator_app_id", alternate = "creator_appid")
    @JsonAdapter(UnsignedInt.class)
    private int creatorAppId;
    @SerializedName(value = "consumer_app_id", alternate = "consumer_appid")
    @JsonAdapter(UnsignedInt.class)
    private int consumerAppId;
    @SerializedName(value = "consumer_shortcutid")
    @JsonAdapter(UnsignedInt.class)
    private int consumerShortcutId;
    @SerializedName("filename")
    private String fileName;
    @SerializedName("file_size")
    @JsonAdapter(UnsignedLong.class)
    private long fileSize;
    @SerializedName("preview_file_size")
    @JsonAdapter(UnsignedLong.class)
    private long previewFileSize;
    @SerializedName("file_url")
    private String fileUrl;
    @SerializedName("preview_url")
    private String previewUrl;
    private String url;
    @SerializedName("youtubevideoid")
    private String youtubeVideoId;
    @SerializedName("hcontent_file")
    private long fileUGCId;
    @SerializedName("hcontent_preview")
    private long previewUGCId;
    private String title;
    @SerializedName(value = "file_description", alternate = "short_description")
    private String fileDescription;
    @SerializedName("time_created")
    @JsonAdapter(UnixTime.Seconds.class)
    private Instant timeCreated;
    @SerializedName("time_updated")
    @JsonAdapter(UnixTime.Seconds.class)
    private Instant timeUpdated;
    @JsonAdapter(EnumByOrdinal.class)
    private Visibility visibility;
    @JsonAdapter(UnsignedInt.class)
    private int flags;
    @SerializedName("workshop_file")
    private boolean workshopFile;
    @SerializedName("workshop_accepted")
    private boolean workshopAccepted;
    @SerializedName("show_subscribe_all")
    private boolean showSubscribeAll;
    @SerializedName("num_comments_developer")
    private int numCommentsDeveloper;
    @SerializedName("num_comments_public")
    private int numCommentsPublic;
    private boolean banned;
    @SerializedName("ban_reason")
    private String banReason;
    @JsonAdapter(UnsignedLong.class)
    private long banner;
    @SerializedName("can_be_deleted")
    private boolean canBeDeleted;
    @SerializedName("incompatible")
    private boolean incompatible;
    private String appName;
    @SerializedName("file_type")
    @JsonAdapter(EnumByOrdinal.class)
    private FileType fileType;
    @SerializedName("can_subscribe")
    private boolean canSubscribe;
    @JsonAdapter(UnsignedInt.class)
    private int subscriptions;
    @JsonAdapter(UnsignedInt.class)
    private int favorited;
    @JsonAdapter(UnsignedInt.class)
    private int followers;
    @SerializedName("lifetime_subscriptions")
    @JsonAdapter(UnsignedInt.class)
    private int lifetimeSubscriptions;
    @SerializedName("lifetime_favorited")
    @JsonAdapter(UnsignedInt.class)
    private int lifetimeFavorited;
    @SerializedName("lifetime_followers")
    @JsonAdapter(UnsignedInt.class)
    private int lifetimeFollowers;
    @SerializedName("lifetime_playtime")
    @JsonAdapter(UnsignedLong.class)
    private long lifetimePlaytime;
    @SerializedName("lifetime_playtime_sessions")
    @JsonAdapter(UnsignedLong.class)
    private long lifetimePlaytimeSessions;
    @JsonAdapter(UnsignedInt.class)
    private int views;
    @SerializedName("image_width")
    @JsonAdapter(UnsignedInt.class)
    private int imageWidth;
    @SerializedName("image_height")
    @JsonAdapter(UnsignedInt.class)
    private int imageHeight;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("spoiler_tag")
    private boolean spoilerTag;
    @SerializedName("shortcutid")
    @JsonAdapter(UnsignedInt.class)
    private int shortcutId;
    @SerializedName("shortcutname")
    private String shortcutName;
    @SerializedName("num_children")
    private int numChildren;
    @SerializedName("num_reports")
    private int numReports;
    private List<Preview> previews;
    private List<Tag> tags;
    private List<Child> children;
    @SerializedName("kvtags")
    private List<KVTag> kvTags;
    @SerializedName("vote_data")
    private VoteData voteData;
    @SerializedName("playtime_stats")
    private PlaytimeStats playtimeStats;
    @SerializedName("time_subscribed")
    @JsonAdapter(UnsignedLong.class)
    private long timeSubscribed;
    @SerializedName("for_sale_data")
    private ForSaleData forSaleData;
    private String metadata;
    private int language;
    @SerializedName("maybe_inappropriate_sex")
    private boolean maybeInappropriateSex;
    @SerializedName("maybe_inappropriate_violence")
    private boolean maybeInappropriateViolence;
    @SerializedName("revision_change_number")
    @JsonAdapter(UnsignedLong.class)
    private long revisionChangeNumber;
    @JsonAdapter(EnumByOrdinal.class)
    private Revision revision;
    @SerializedName("available_revisions")
    @JsonAdapter(EnumByOrdinalList.class)
    private List<Revision> availableRevisions;
    @SerializedName("reactions")
    private List<Reaction> reactions;
    @SerializedName("ban_text_check_result")
    @JsonAdapter(UnsignedInt.class)
    private int banTextCheckResult;

    @SuppressWarnings("unused")
    private PublishedFileDetails() {}

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

    public int getConsumerShortcutId() {
        return consumerShortcutId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getPreviewFileSize() {
        return previewFileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public long getFileUGCId() {
        return fileUGCId;
    }

    public long getPreviewUGCId() {
        return previewUGCId;
    }

    public String getTitle() {
        return title;
    }

    public String getFileDescription() {
        return fileDescription;
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

    public int getFlags() {
        return flags;
    }

    public boolean isWorkshopFile() {
        return workshopFile;
    }

    public boolean isWorkshopAccepted() {
        return workshopAccepted;
    }

    public boolean isShowSubscribeAll() {
        return showSubscribeAll;
    }

    public int getNumCommentsDeveloper() {
        return numCommentsDeveloper;
    }

    public int getNumCommentsPublic() {
        return numCommentsPublic;
    }

    public boolean isBanned() {
        return banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public long getBanner() {
        return banner;
    }

    public boolean isCanBeDeleted() {
        return canBeDeleted;
    }

    public boolean isIncompatible() {
        return incompatible;
    }

    public String getAppName() {
        return appName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public boolean isCanSubscribe() {
        return canSubscribe;
    }

    public int getSubscriptions() {
        return subscriptions;
    }

    public int getFavorited() {
        return favorited;
    }

    public int getFollowers() {
        return followers;
    }

    public int getLifetimeSubscriptions() {
        return lifetimeSubscriptions;
    }

    public int getLifetimeFavorited() {
        return lifetimeFavorited;
    }

    public int getLifetimeFollowers() {
        return lifetimeFollowers;
    }

    public long getLifetimePlaytime() {
        return lifetimePlaytime;
    }

    public long getLifetimePlaytimeSessions() {
        return lifetimePlaytimeSessions;
    }

    public int getViews() {
        return views;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSpoilerTag() {
        return spoilerTag;
    }

    public int getShortcutId() {
        return shortcutId;
    }

    public String getShortcutName() {
        return shortcutName;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public int getNumReports() {
        return numReports;
    }

    public List<Preview> getPreviews() {
        return previews;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Child> getChildren() {
        return children;
    }

    public List<KVTag> getKvTags() {
        return kvTags;
    }

    public VoteData getVoteData() {
        return voteData;
    }

    public PlaytimeStats getPlaytimeStats() {
        return playtimeStats;
    }

    public long getTimeSubscribed() {
        return timeSubscribed;
    }

    public ForSaleData getForSaleData() {
        return forSaleData;
    }

    public String getMetadata() {
        return metadata;
    }

    public int getLanguage() {
        return language;
    }

    public boolean isMaybeInappropriateSex() {
        return maybeInappropriateSex;
    }

    public boolean isMaybeInappropriateViolence() {
        return maybeInappropriateViolence;
    }

    public long getRevisionChangeNumber() {
        return revisionChangeNumber;
    }

    public Revision getRevision() {
        return revision;
    }

    public List<Revision> getAvailableRevisions() {
        return availableRevisions;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public int getBanTextCheckResult() {
        return banTextCheckResult;
    }

    public enum ForSaleStatus {
        NotForSale,
        PendingApproval,
        ApprovedForSale,
        RejectedForSale,
        NoLongerForSale,
        TentativeApproval
    }

    public enum Revision {
        Default,
        Latest,
        ApprovedSnapshot,
        ApprovedSnapshot_China,
        RejectedSnapshot,
        RejectedSnapshot_China,
    }

    public static final class Preview {

        public static final int IMAGE = 0;
        public static final int YOUTUBE_VIDEO = 1;
        public static final int SKETCHFAB = 2;
        public static final int ENVIRONMENT_MAP_HORIZONTAL_CROSS = 3;
        public static final int ENVIRONMENT_MAP_LAT_LONG = 4;
        public static final int RESERVED_MAX = 255;

        @SerializedName("previewid")
        @JsonAdapter(UnsignedLong.class)
        private long previewId;
        @SerializedName("sortorder")
        @JsonAdapter(UnsignedInt.class)
        private int sortOrder;
        @SerializedName("url")
        private String url;
        @JsonAdapter(UnsignedInt.class)
        private int size;
        @SerializedName("filename")
        private String fileName;
        @SerializedName("youtubevideoid")
        private String youtubeVideoId;
        @SerializedName("preview_type")
        @JsonAdapter(UnsignedInt.class)
        private int previewType;
        @SerializedName("external_reference")
        private String externalReference;

        @SuppressWarnings("unused")
        private Preview() {}

        public long getPreviewId() {
            return previewId;
        }

        public int getSortOrder() {
            return sortOrder;
        }

        public String getUrl() {
            return url;
        }

        public int getSize() {
            return size;
        }

        public String getFileName() {
            return fileName;
        }

        public String getYoutubeVideoId() {
            return youtubeVideoId;
        }

        public int getPreviewType() {
            return previewType;
        }

        public String getExternalReference() {
            return externalReference;
        }
    }

    public static final class Tag {

        private String tag;
        @SerializedName("adminonly")
        private boolean adminOnly;
        @SerializedName("display_name")
        private String displayName;

        @SuppressWarnings("unused")
        private Tag() {}

        public String getTag() {
            return tag;
        }

        public boolean isAdminOnly() {
            return adminOnly;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final class KVTag {

        private String key;
        private String value;

        @SuppressWarnings("unused")
        private KVTag() {}

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public static final class VoteData {

        private float score;
        @SerializedName("votes_up")
        @JsonAdapter(UnsignedInt.class)
        private int votesUp;
        @SerializedName("votes_down")
        @JsonAdapter(UnsignedInt.class)
        private int votesDown;

        @SuppressWarnings("unused")
        private VoteData() {}

        public float getScore() {
            return score;
        }

        public int getVotesUp() {
            return votesUp;
        }

        public int getVotesDown() {
            return votesDown;
        }
    }

    public static final class PlaytimeStats {

        @SerializedName("playtime_seconds")
        @JsonAdapter(UnsignedLong.class)
        private long playtimeSeconds;
        @SerializedName("num_sessions")
        @JsonAdapter(UnsignedLong.class)
        private long numSessions;

        @SuppressWarnings("unused")
        private PlaytimeStats() {}

        public long getPlaytimeSeconds() {
            return playtimeSeconds;
        }

        public long getNumSessions() {
            return numSessions;
        }
    }

    public static final class ForSaleData {

        @SerializedName("is_for_sale")
        private boolean isForSale;
        @SerializedName("price_category")
        @JsonAdapter(UnsignedInt.class)
        private int priceCategory;
        @SerializedName("estatus")
        @JsonAdapter(EnumByOrdinal.class)
        private ForSaleStatus status;
        @SerializedName("price_category_floor")
        @JsonAdapter(UnsignedInt.class)
        private int priceCategoryFloor;
        @SerializedName("price_is_pay_what_you_want")
        private boolean priceIsPayWhatYouWant;
        @SerializedName("discount_percentage")
        @JsonAdapter(UnsignedInt.class)
        private int discountPercentage;

        @SuppressWarnings("unused")
        private ForSaleData() {}

        public boolean isForSale() {
            return isForSale;
        }

        public int getPriceCategory() {
            return priceCategory;
        }

        public ForSaleStatus getStatus() {
            return status;
        }

        public int getPriceCategoryFloor() {
            return priceCategoryFloor;
        }

        public boolean isPriceIsPayWhatYouWant() {
            return priceIsPayWhatYouWant;
        }

        public int getDiscountPercentage() {
            return discountPercentage;
        }
    }

    public static final class Reaction {

        @SerializedName("reactionid")
        @JsonAdapter(UnsignedInt.class)
        private int reactionId;
        @JsonAdapter(UnsignedInt.class)
        private int count;

        @SuppressWarnings("unused")
        private Reaction() {}

        public int getReactionId() {
            return reactionId;
        }

        public int getCount() {
            return count;
        }
    }
}
