package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.annotations.SerializedName;
import io.github.ititus.valve_tools.steam_web_api.BaseResult;
import io.github.ititus.valve_tools.steam_web_api.common.Child;

import java.util.List;

public final class CollectionDetails extends BaseResult {

    @SerializedName("publishedfileid")
    private long publishedFileId;

    private List<Child> children;

    @SuppressWarnings("unused")
    private CollectionDetails() {}

    public long getPublishedFileId() {
        return publishedFileId;
    }

    public List<Child> getChildren() {
        return children;
    }
}
