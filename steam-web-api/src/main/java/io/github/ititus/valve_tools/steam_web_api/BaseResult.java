package io.github.ititus.valve_tools.steam_web_api;

import com.google.gson.annotations.JsonAdapter;
import io.github.ititus.valve_tools.steam_web_api.json.EnumByOrdinal;

public class BaseResult {

    /**
     * EResult m_eResult; // The result of the operation.
     */
    @JsonAdapter(EnumByOrdinal.class)
    private final Result result;

    @SuppressWarnings("unused")
    private BaseResult() {
        this(Result.None);
    }

    protected BaseResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}
