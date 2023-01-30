package io.github.ititus.valve_tools.kv;

import io.github.ititus.commons.data.pair.Pair;
import io.github.ititus.commons.lexer.LexerIterator;
import io.github.ititus.commons.lexer.token.Token;
import io.github.ititus.valve_tools.kv.internal.KvLexer;
import io.github.ititus.valve_tools.kv.internal.KvSymbol;
import io.github.ititus.valve_tools.kv.internal.QuotedToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class KeyValues extends KvBase {

    private final Map<String, KvBase> children;

    private KeyValues(Map<String, KvBase> children) {
        this.children = children;
    }

    public static KeyValues parseText(Path file, Settings settings) throws IOException {
        var lexer = new LexerIterator(KvLexer.lexerTemplate(settings).lexer(Files.newBufferedReader(file)));
        return parseTextSimple(lexer, true);
    }

    private static KeyValues parseTextSimple(LexerIterator lexer, boolean root) throws IOException {
        Map<String, KvBase> kvs = new LinkedHashMap<>();
        while (true) {
            Token<?> next;
            if (root) {
                if (!lexer.hasNext()) {
                    break;
                }

                next = lexer.next();
            } else {
                next = lexer.next();
                if (next.token() == KvSymbol.R_BRACE) {
                    break;
                }
            }

            var kv = parseTextKeyValue(next, lexer);
            kvs.put(kv.a(), kv.b());
        }

        return new KeyValues(kvs);
    }

    private static Pair<String, KvBase> parseTextKeyValue(Token<?> next, LexerIterator lexer) throws IOException {
        if (next == null) {
            next = lexer.next();
        }

        if (!(next.type() instanceof QuotedToken)) {
            throw new KvException("expected key");
        }

        var key = (String) next.token();
        System.out.println(key);

        next = lexer.next();
        KvBase value;
        if (next.token() == KvSymbol.L_BRACE) {
            value = parseTextSimple(lexer, false);
        } else if (next.type() instanceof QuotedToken) {
            value = new KvPrimitive(next.token());
        } else {
            throw new KvException("unexpected token");
        }

        return Pair.of(key, value);
    }

    public Map<String, KvBase> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "KeyValues" + children;
    }

    public record Settings(boolean allowEscapes, boolean allowConditionals, boolean allowMacros) {

        public static Settings simple() {
            return new Settings(true, false, false);
        }

        public static Settings def() {
            return new Settings(false, true, true);
        }
    }
}
