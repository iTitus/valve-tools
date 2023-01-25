package io.github.ititus.valve_tools.steam_web_api.published_file_service;

import io.github.ititus.valve_tools.steam_web_api.Interface;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.List;

public final class PublishedFileService extends Interface {

    public PublishedFileService(SteamWebApi api) {
        super("IPublishedFileService", api);
    }

    public PublishedFileDetails getDetails(Boolean includeTags, Boolean includeAdditionalPreviews, Boolean includeChildren, Boolean includeKVTags, Boolean includeVotes, Boolean shortDescription, Boolean includeForSaleData, Boolean includeMetadata, Integer language, Integer returnPlaytimeStats, Integer appId, Boolean stripDescriptionBBCode, PublishedFileDetails.Revision desiredRevision, Boolean includeReactions, long publishedFileId) throws SteamWebApiException {
        return new GetDetails(this, includeTags, includeAdditionalPreviews, includeChildren, includeKVTags, includeVotes, shortDescription, includeForSaleData, includeMetadata, language, returnPlaytimeStats, appId, stripDescriptionBBCode, desiredRevision, includeReactions, publishedFileId).request().get(0);
    }

    public List<PublishedFileDetails> getDetails(Boolean includeTags, Boolean includeAdditionalPreviews, Boolean includeChildren, Boolean includeKVTags, Boolean includeVotes, Boolean shortDescription, Boolean includeForSaleData, Boolean includeMetadata, Integer language, Integer returnPlaytimeStats, Integer appId, Boolean stripDescriptionBBCode, PublishedFileDetails.Revision desiredRevision, Boolean includeReactions, long... publishedFileIds) throws SteamWebApiException {
        return new GetDetails(this, includeTags, includeAdditionalPreviews, includeChildren, includeKVTags, includeVotes, shortDescription, includeForSaleData, includeMetadata, language, returnPlaytimeStats, appId, stripDescriptionBBCode, desiredRevision, includeReactions, publishedFileIds).request();
    }
}
