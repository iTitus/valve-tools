package io.github.ititus.valve_tools.kv.internal;

import io.github.ititus.commons.lexer.LexerTemplate;
import io.github.ititus.valve_tools.kv.KeyValues;

import java.util.Objects;

public final class KvLexer {

    private KvLexer() {}

    public static LexerTemplate lexerTemplate(KeyValues.Settings settings) {
        Objects.requireNonNull(settings, "settings");
        if (settings.allowConditionals() || settings.allowMacros()) {
            throw new UnsupportedOperationException();
        }

        var lt = LexerTemplate.create()
                .addTokenType(KvSymbol.TYPE);

        if (settings.allowEscapes()) {
            lt.addTokenType(QuotedToken.WITH_ESCAPES);
        } else {
            lt.addTokenType(QuotedToken.WITHOUT_ESCAPES);
        }

        return lt
                .addTokenType(LineCommentToken.INSTANCE)
                .addTokenType(WhitespaceToken.INSTANCE);
    }
}
