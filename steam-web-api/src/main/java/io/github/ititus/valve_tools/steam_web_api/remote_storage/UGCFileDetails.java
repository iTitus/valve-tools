package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import com.google.gson.annotations.SerializedName;

public final class UGCFileDetails {

    @SerializedName("filename")
    private String fileName;
    private String url;
    private int size;

    @SuppressWarnings("unused")
    private UGCFileDetails() {}

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }
}
