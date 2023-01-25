package io.github.ititus.valve_tools.steam_web_api.common;

import com.google.gson.annotations.JsonAdapter;
import io.github.ititus.valve_tools.steam_web_api.json.EnumByOrdinal;

public class BaseResult {

    @JsonAdapter(EnumByOrdinal.class)
    private Result result;

    @SuppressWarnings("unused")
    protected BaseResult() {}

    public Result getResult() {
        return result;
    }
}
