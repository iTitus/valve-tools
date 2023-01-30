package io.github.ititus.valve_tools.kv.internal;

/**
 * Methods to deal with quoted strings.
 */
public final class StringUtil {

    private StringUtil() {}

    /**
     * Unquote a given quoted string without resolving escape sequences.
     *
     * @param s quoted string
     * @return unquoted string
     */
    public static String unquoteWithoutEscapes(String s) {
        int length = s.length();
        if (length < 2 || s.charAt(0) != '"' || s.charAt(length - 1) != '"') {
            throw new IllegalArgumentException("string not quoted");
        }

        String unquoted = s.substring(1, length - 1);
        int idx = unquoted.indexOf('\"');
        if (idx >= 0) {
            throw new IllegalArgumentException("invalid quote in string");
        }

        return unquoted;
    }

    /**
     * Unquote a given quoted string while resolving escape sequences.
     *
     * @param s quoted string
     * @return unquoted string
     */
    public static String unquote(String s) {
        int length = s.length();
        if (length < 2 || s.charAt(0) != '"' || s.charAt(length - 1) != '"') {
            throw new IllegalArgumentException("string not quoted");
        }

        StringBuilder b = new StringBuilder();
        for (int i = 1; i < length - 1; i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                if (i + 1 >= length - 1) {
                    throw new IllegalArgumentException("trailing backslash");
                }

                char escape = s.charAt(++i);
                c = switch (escape) {
                    case '"' -> '"';
                    case '\\' -> '\\';
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'v' -> '\u000b';
                    case 'b' -> '\b';
                    case 'r' -> '\r';
                    case 'f' -> '\f';
                    case 'a' -> '\u0007';
                    case '?' -> '?';
                    case '\'' -> '\'';
                    default -> throw new IllegalArgumentException("unknown escape character '" + escape + "'");
                };
            } else if (c == '\"') {
                throw new IllegalArgumentException("invalid unescaped character '" + c + "'");
            }

            b.append(c);
        }

        return b.toString();
    }

    /**
     * Quote a given unquoted string while adding escape sequences where necessary.
     *
     * @param s unquoted string
     * @return quoted string
     */
    public static String quote(String s) {
        StringBuilder b = new StringBuilder().append('"');
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> b.append('\\').append('\\');
                case '"' -> b.append('\\').append('"');
                case '\n' -> b.append('\\').append('n');
                case '\t' -> b.append('\\').append('t');
                case '\u000b' -> b.append('\\').append('v');
                case '\b' -> b.append('\\').append('b');
                case '\r' -> b.append('\\').append('r');
                case '\f' -> b.append('\\').append('f');
                case '\u0007' -> b.append('\\').append('a');
                // case '?' -> b.append('\\').append('?');
                // case '\'' -> b.append('\\').append('\'');
                default -> b.append(c);
            }
        }

        return b.append('"').toString();
    }
}
