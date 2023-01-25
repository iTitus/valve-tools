package io.github.ititus.valve_tools.steam_web_api.published_file_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.ititus.valve_tools.steam_web_api.ApiMethod;
import io.github.ititus.valve_tools.steam_web_api.JsonMethod;
import io.github.ititus.valve_tools.steam_web_api.exception.ParameterException;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.List;

public final class GetDetails extends JsonMethod<PublishedFileService, List<PublishedFileDetails>> {

    private final Boolean includeTags;
    private final Boolean includeAdditionalPreviews;
    private final Boolean includeChildren;
    private final Boolean includeKVTags;
    private final Boolean includeVotes;
    private final Boolean shortDescription;
    private final Boolean includeForSaleData;
    private final Boolean includeMetadata;
    private final Integer language;
    private final Integer returnPlaytimeStats;
    private final Integer appId;
    private final Boolean stripDescriptionBBCode;
    private final PublishedFileDetails.Revision desiredRevision;
    private final Boolean includeReactions;
    private final long[] publishedFileIds;

    GetDetails(PublishedFileService apiInterface, Boolean includeTags, Boolean includeAdditionalPreviews, Boolean includeChildren, Boolean includeKVTags, Boolean includeVotes, Boolean shortDescription, Boolean includeForSaleData, Boolean includeMetadata, Integer language, Integer returnPlaytimeStats, Integer appId, Boolean stripDescriptionBBCode, PublishedFileDetails.Revision desiredRevision, Boolean includeReactions, long... publishedFileIds) throws SteamWebApiException {
        super(apiInterface, "GetDetails", 1, ApiMethod.GET);
        if (publishedFileIds.length == 0) {
            throw new ParameterException("Expected non-empty ids");
        }

        this.includeTags = includeTags;
        this.includeAdditionalPreviews = includeAdditionalPreviews;
        this.includeChildren = includeChildren;
        this.includeKVTags = includeKVTags;
        this.includeVotes = includeVotes;
        this.shortDescription = shortDescription;
        this.includeForSaleData = includeForSaleData;
        this.includeMetadata = includeMetadata;
        this.language = language;
        this.returnPlaytimeStats = returnPlaytimeStats;
        this.appId = appId;
        this.stripDescriptionBBCode = stripDescriptionBBCode;
        this.desiredRevision = desiredRevision;
        this.includeReactions = includeReactions;
        this.publishedFileIds = publishedFileIds;
    }

    @Override
    public void populateJson(JsonObject json) {
        var arr = new JsonArray();
        for (var id : publishedFileIds) {
            arr.add(Long.toUnsignedString(id));
        }
        json.add("publishedfileids", arr);
        json.addProperty("includetags", includeTags);
        json.addProperty("includeadditionalpreviews", includeAdditionalPreviews);
        json.addProperty("includechildren", includeChildren);
        json.addProperty("includekvtags", includeKVTags);
        json.addProperty("includevotes", includeVotes);
        json.addProperty("short_description", shortDescription);
        json.addProperty("includeforsaledata", includeForSaleData);
        json.addProperty("includemetadata", includeMetadata);
        json.addProperty("language", language);
        json.addProperty("return_playtime_stats", returnPlaytimeStats != null ? Integer.toUnsignedString(returnPlaytimeStats) : null);
        json.addProperty("appid", appId != null ? Integer.toUnsignedString(appId) : null);
        json.addProperty("strip_description_bbcode", stripDescriptionBBCode);
        json.addProperty("desired_revision", desiredRevision != null ? Integer.toUnsignedString(desiredRevision.ordinal()) : null);
        json.addProperty("includereactions", includeReactions);
    }

    @Override
    protected List<PublishedFileDetails> parseJson(JsonObject json) throws SteamWebApiException {
        return extractArrayElements(json, "publishedfiledetails", publishedFileIds.length, PublishedFileDetails.class);
    }
}
