package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import io.github.ititus.valve_tools.steam_web_api.common.BaseResult;
import io.github.ititus.valve_tools.steam_web_api.common.Visibility;
import io.github.ititus.valve_tools.steam_web_api.json.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public final class PublishedFileDetails extends BaseResult {

    @SerializedName("publishedfileid")
    @JsonAdapter(UnsignedLong.class)
    private long publishedFileId;
    @JsonAdapter(UnsignedLong.class)
    private long creator;
    @SerializedName("creator_app_id")
    @JsonAdapter(UnsignedInt.class)
    private int creatorAppId;
    @SerializedName("consumer_app_id")
    @JsonAdapter(UnsignedInt.class)
    private int consumerAppId;
    @SerializedName("filename")
    private String fileName;
    @SerializedName("file_size")
    private int fileSize;
    @SerializedName("file_url")
    private String fileUrl;
    @SerializedName("hcontent_file")
    private long fileUGCId;
    @SerializedName("preview_url")
    private String previewUrl;
    @SerializedName("hcontent_preview")
    private long previewUGCId;
    private String title;
    private String description;
    @SerializedName("time_created")
    @JsonAdapter(UnixTime.Seconds.class)
    private Instant timeCreated;
    @SerializedName("time_updated")
    @JsonAdapter(UnixTime.Seconds.class)
    private Instant timeUpdated;
    @JsonAdapter(EnumByOrdinal.class)
    private Visibility visibility;
    @JsonAdapter(CBoolean.class)
    private boolean banned;
    @SerializedName("ban_reason")
    private String banReason;
    private int subscriptions;
    private int favorited;
    @SerializedName("lifetime_subscriptions")
    private int lifetimeSubscriptions;
    @SerializedName("lifetime_favorited")
    private int lifetimeFavorited;
    private int views;
    @JsonAdapter(TagListAdapter.class)
    private List<String> tags;

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

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public long getFileUGCId() {
        return fileUGCId;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public long getPreviewUGCId() {
        return previewUGCId;
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

    public boolean isBanned() {
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

    public List<String> getTags() {
        return tags;
    }

    public static final class TagListAdapter implements JsonSerializer<List<String>>, JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.<List<Tag>>deserialize(json, new TypeToken<List<Tag>>() {}.getType()).stream()
                    .map(Tag::getTag)
                    .collect(Collectors.toList());
        }

        @Override
        public JsonElement serialize(List<String> src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(
                    src.stream()
                            .map(Tag::new)
                            .toList()
            );
        }

        private static final class Tag {

            String tag;

            @SuppressWarnings("unused")
            Tag() {}

            Tag(String tag) {
                this.tag = tag;
            }

            String getTag() {
                return tag;
            }
        }
    }
}
