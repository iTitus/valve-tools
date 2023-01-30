package io.github.ititus.valve_tools.kv.internal;

import io.github.ititus.commons.lexer.MatchResult;
import io.github.ititus.commons.lexer.token.TokenType;

public final class LineCommentToken implements TokenType<Void> {

    public static final LineCommentToken INSTANCE = new LineCommentToken();

    private LineCommentToken() {}

    static boolean isCommentStartChar(char c) {
        return c == '/';
    }

    @Override
    public String name() {
        return "LINE_COMMENT";
    }

    @Override
    public MatchResult matches(CharSequence str) {
        int len = str.length();
        if (len == 0) {
            return MatchResult.PREFIX_ONLY_MATCH;
        }

        char first = str.charAt(0);
        if (!isCommentStartChar(first)) {
            return MatchResult.NO_MATCH;
        }

        if (len < 2) {
            return MatchResult.PREFIX_ONLY_MATCH;
        }

        char second = str.charAt(1);
        if (!isCommentStartChar(second)) {
            return MatchResult.NO_MATCH;
        }

        for (int i = len - 1; i >= 2; i--) {
            if (str.charAt(i) == '\n') {
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
