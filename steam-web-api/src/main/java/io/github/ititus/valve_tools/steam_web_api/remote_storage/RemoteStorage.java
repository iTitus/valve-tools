package io.github.ititus.valve_tools.steam_web_api.remote_storage;

import io.github.ititus.valve_tools.steam_web_api.Interface;
import io.github.ititus.valve_tools.steam_web_api.SteamWebApi;
import io.github.ititus.valve_tools.steam_web_api.exception.SteamWebApiException;

import java.util.List;

public class RemoteStorage extends Interface {

    public RemoteStorage(SteamWebApi api) {
        super("ISteamRemoteStorage", api);
    }

    public CollectionDetails getCollectionDetails(long collectionId) throws SteamWebApiException {
        return new GetCollectionDetails(this, collectionId).request().get(0);
    }

    public List<CollectionDetails> getCollectionDetails(long... collectionIds) throws SteamWebApiException {
        return new GetCollectionDetails(this, collectionIds).request();
    }

    public PublishedFileDetails getPublishedFileDetails(long publishedFileId) throws SteamWebApiException {
        return new GetPublishedFileDetails(this, publishedFileId).request().get(0);
    }

    public List<PublishedFileDetails> getPublishedFileDetails(long... publishedFileIds) throws SteamWebApiException {
        return new GetPublishedFileDetails(this, publishedFileIds).request();
    }
}
