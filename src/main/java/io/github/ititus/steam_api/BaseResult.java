package io.github.ititus.steam_api;

import com.google.gson.annotations.JsonAdapter;
import io.github.ititus.steam_api.json.EnumByOrdinal;

public class BaseResult {

    /**
     * EResult m_eResult; // The result of the operation.
     */
    @JsonAdapter(EnumByOrdinal.class)
    private final Result result;

    public BaseResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}
