package io.github.ititus.valve_tools.kv.internal;

import io.github.ititus.commons.lexer.MatchResult;
import io.github.ititus.commons.lexer.token.TokenType;

public final class WhitespaceToken implements TokenType<Void> {

    public static final WhitespaceToken INSTANCE = new WhitespaceToken();

    private WhitespaceToken() {}

    public static boolean isWhitespace(char c) {
        if (c == ' ' || c == '\t' || c == '\u000b' || c == '\f' || c == '\n' || c == '\r') {
            return true;
        }

        int type = Character.getType(c);
        return type == Character.SPACE_SEPARATOR || type == Character.LINE_SEPARATOR || type == Character.PARAGRAPH_SEPARATOR;
    }

    @Override
    public String name() {
        return "WHITESPACE";
    }

    @Override
    public MatchResult matches(CharSequence str) {
        int len = str.length();
        if (len == 0) {
            return MatchResult.PREFIX_ONLY_MATCH;
        }

        for (int i = len - 1; i >= 0; i--) {
            if (!isWhitespace(str.charAt(i))) {
                return MatchResult.NO_MATCH;
            }
        }

        return MatchResult.FULL_MATCH;
    }

    @Override
    public boolean ignore() {
        return true;
    }

    @Override
    public Void convert(String token) {
        return null;
    }
}
