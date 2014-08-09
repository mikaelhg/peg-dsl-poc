package io.mikael.peg;

import java.util.Arrays;

public class RuleBuilder {

    public static void main(String[] args) {

        System.out.printf("character('a', 'h').match(\"far\") = <%s>%n",
                character('a', 'h').match("far"));

        System.out.printf("oneOrMore(character('a', 'h')).match(\"fabaraba\") = <%s>%n",
                oneOrMore(character('a', 'h')).match("fabaraba"));

        System.out.printf("character('f').match(\"far\") = <%s>%n",
                character('f').match("far"));

        System.out.printf("string(\"fa\").match(\"far\") = <%s>%n",
                string("fa").match("far"));

        System.out.printf("sequence(string(\"ab\"), string(\"ba\"), string(\"arb\")).match(\"abbaarb\")) = <%s>%n",
                sequence(string("ab"), string("ba"), string("arb")).match("abbaarb"));

        System.out.printf("oneOrMore(or(character('a'), character('b'))).match(\"abba\")) = <%s>%n",
                oneOrMore(or(character('a'), character('b'))).match("abbararara"));

        System.out.printf("oneOrMore(not(character('a'))).match(\"bcdbabcd\")) = <%s>%n",
                oneOrMore(not(character('a'))).match("bcdbabcd"));

    }

    @FunctionalInterface
    public static interface Rule {

        default public Match match(String input) {
            return match(input.toCharArray(), 0);
        }

        public Match match(char[] chars, int i);

    }

    public static class Match {
        public final int length;
        public final CharSequence content;
        public final boolean matched;
        public Match(final boolean matched, final int length, final CharSequence content) {
            this.length = length;
            this.content = content;
            this.matched = matched;
        }
        @Override public String toString() {
            return String.format("Match(%s, %s, %s)", length, content, matched);
        }
    }

    public static class NoMatch extends Match {
        public NoMatch() { super(false, 0, null); }
        @Override public String toString() { return "NoMatch()"; }
    }

    private static Match match(final CharSequence content) {
        return new Match(true, content.length(), content);
    }

    private static Match match(final char[] content) {
        return new Match(true, content.length, new StringBuilder().append(content));
    }

    private static Match match(final char content) {
        return new Match(true, 1, Character.toString(content));
    }

    private static Match match(final Match ... matches) {
        if (Arrays.stream(matches).anyMatch(m -> !m.matched)) {
            return noMatch();
        }
        final StringBuilder sb = new StringBuilder();
        for (final Match m : matches) {
            sb.append(m.content);
        }
        return match(sb);
    }

    private static Match noMatch() {
        return new NoMatch();
    }

    public static Rule character(final char low, final char high) {
        return (chars, i) -> {
            if (i < chars.length && chars[i] >= low && chars[i] <= high) {
                return match(chars[i]);
            } else {
                return noMatch();
            }
        };
    }

    public static Rule character(final char constant) {
        return (chars, i) -> {
            if (i < chars.length && chars[i] == constant) {
                return match(chars[i]);
            } else {
                return noMatch();
            }
        };
    }

    public static Rule string(final String constant) {
        return (chars, i) -> {
            final char[] input = constant.toCharArray();
            int j = i,  k = 0;
            for (; j < chars.length && k < input.length; j++, k++) {
                if (chars[j] != input[k]) {
                    return noMatch();
                }
            }
            if (j - i == k && k == input.length) {
                return match(input);
            }
            return noMatch();
        };
    }

    public static Rule sequence(final Rule ... rules) {
        return (chars, i) -> {
            int j = i;
            for (final Rule rule : rules) {
                final Match match = rule.match(chars, j);
                if (match.matched) {
                    j += match.length;
                } else {
                    return noMatch();
                }
            }
            return match(new StringBuilder().append(chars, i, j));
        };
    }

    public static Rule and(final Rule a, final Rule b) {
        return (chars, i) -> {
            final Match am = a.match(chars, i);
            if (!am.matched) {
                return noMatch();
            }
            final Match bm = b.match(chars, i + am.length);
            if (!bm.matched) {
                return noMatch();
            }
            return match(new StringBuilder().append(am.content).append(bm.content));
        };
    }

    public static Rule or(final Rule... choices) {
        return (chars, i) -> {
            for (final Rule m : choices) {
                final Match n = m.match(chars, i);
                if (n.matched) {
                    return n;
                }
            }
            return noMatch();
        };
    }

    // !! how do we get the character / string for a non-match
    public static Rule not(final Rule rule) {
        return (chars, i) -> {
            final Match match = rule.match(chars, i);
            if (match.matched) {
                return noMatch();
            } else {
                return match(match.content);
            }
        };
    }

    public static Rule zeroOrMore(final Rule m) {
        return (chars, i) -> {
            Match n;
            int k = i;
            while ((n = m.match(chars, k)).matched) {
                k += n.length;
            }
            return match(Arrays.copyOfRange(chars, i, k));
        };
    }

    public static Rule oneOrMore(final Rule m) {
        return (chars, i) -> {
            Match n;
            int k = 0, matches = 0;
            while ((n = m.match(chars, k)).matched) {
                k += n.length;
                matches++;
            }
            if (matches < 1) {
                throw new RuntimeException("one or more matches expected");
            }
            return match(Arrays.copyOfRange(chars, i, k));
        };
    }

    public static Rule optional(Rule m) {
        return (chars, i) -> noMatch();
    }

}
