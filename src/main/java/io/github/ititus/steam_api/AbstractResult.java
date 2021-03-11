package io.github.ititus.steam_api;

import com.google.gson.annotations.JsonAdapter;

public abstract class AbstractResult {

    /**
     * EResult m_eResult; // The result of the operation.
     */
    @JsonAdapter(Result.ById.class)
    private final Result result;

    protected AbstractResult(Result result) {
        this.result = result;
    }

    public final Result getResult() {
        return result;
    }
}
