package io.github.ititus.valve_tools.steam_web_api.common;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.ititus.valve_tools.steam_web_api.json.EnumByOrdinal;
import io.github.ititus.valve_tools.steam_web_api.json.UnsignedInt;
import io.github.ititus.valve_tools.steam_web_api.json.UnsignedLong;

public final class Child {

    @SerializedName("publishedfileid")
    @JsonAdapter(UnsignedLong.class)
    private long publishedFileId;
    @SerializedName("sortorder")
    @JsonAdapter(UnsignedInt.class)
    private int sortOrder;
    @SerializedName("filetype")
    @JsonAdapter(EnumByOrdinal.class)
    private FileType fileType;

    @SuppressWarnings("unused")
    private Child() {}

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
