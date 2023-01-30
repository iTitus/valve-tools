package io.github.ititus.valve_tools.kv.internal;

import io.github.ititus.commons.lexer.MatchResult;
import io.github.ititus.commons.lexer.token.TokenType;

public final class QuotedToken implements TokenType<String> {

    public static final QuotedToken WITH_ESCAPES = new QuotedToken(true);
    public static final QuotedToken WITHOUT_ESCAPES = new QuotedToken(false);

    private final boolean allowEscapes;

    private QuotedToken(boolean allowEscapes) {
        this.allowEscapes = allowEscapes;
    }

    @Override
    public String name() {
        return "QUOTED";
    }

    @Override
    public MatchResult matches(CharSequence str) {
        int len = str.length();
        if (len == 0) {
            return MatchResult.PREFIX_ONLY_MATCH;
        }

        char first = str.charAt(0);
        if (first != '"') {
            return MatchResult.NO_MATCH;
        } else if (len == 1) {
            return MatchResult.PREFIX_ONLY_MATCH;
        }

        int i = 1;
        while (i < len) {
            char c = str.charAt(i++);
            if (allowEscapes && c == '\\') {
                if (i < len) {
                    char next = str.charAt(i++);
                    if (next != '\\' && next != '"' && next != 'n' && next != 't' && next != 'v' && next != 'b' && next != 'r' && next != 'f' && next != 'a' && next != '?' && next != '\'') {
                        return MatchResult.NO_MATCH;
                    }
                }
            } else if (c == '"') {
                if (i == len) {
                    return MatchResult.FULL_MATCH;
                } else {
                    return MatchResult.NO_MATCH;
                }
            }
        }

        return MatchResult.PREFIX_ONLY_MATCH;
    }

    @Override
    public String convert(String token) {
        return allowEscapes ? StringUtil.unquote(token) : StringUtil.unquoteWithoutEscapes(token);
    }
}
