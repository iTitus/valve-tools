package io.github.ititus.valve_tools.kv.internal;

import io.github.ititus.commons.lexer.token.MapBasedCharacterTokenType;
import io.github.ititus.commons.lexer.token.TokenType;

public enum KvSymbol implements MapBasedCharacterTokenType.CharacterToken {

    L_BRACE('{'),
    R_BRACE('}');

    public static final TokenType<KvSymbol> TYPE = MapBasedCharacterTokenType.of("SYMBOL", KvSymbol.class);

    private final char symbol;

    KvSymbol(char symbol) {
        this.symbol = symbol;
    }

    static boolean isSymbolChar(char c) {
        return c == '{' || c == '}';
    }

    @Override
    public char character() {
        return symbol;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
