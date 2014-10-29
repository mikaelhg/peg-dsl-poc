package io.mikael.peg;

import io.mikael.peg.rules.CharacterConstantRule;
import io.mikael.peg.rules.CharacterLoHiRule;

import java.util.Arrays;

/**
 * Prototype.
 */
public class RuleBuilder {

    public static void main(final String ... args) {

        System.out.printf("%s/%s%n", null, null);

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

    private static MatchNode match(final CharSequence content) {
        return new MatchNode(true, content.length(), content);
    }

    private static MatchNode match(final char[] content) {
        return new MatchNode(true, content.length, new StringBuilder().append(content));
    }

    private static MatchNode match(final char content) {
        return new MatchNode(true, 1, Character.toString(content));
    }

    private static MatchNode match(final MatchNode... matchNodes) {
        if (Arrays.stream(matchNodes).anyMatch(m -> !m.matched)) {
            return noMatch();
        }
        final StringBuilder sb = new StringBuilder();
        for (final MatchNode m : matchNodes) {
            sb.append(m.content);
        }
        return match(sb);
    }

    private static MatchNode noMatch() {
        return new NoMatchNode();
    }

    public static Rule character(final char low, final char high) {
        return new CharacterLoHiRule(low, high);
    }

    public static Rule character(final char constant) {
        return new CharacterConstantRule(constant);
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
                final MatchNode matchNode = rule.match(chars, j);
                if (matchNode.matched) {
                    j += matchNode.length;
                } else {
                    return noMatch();
                }
            }
            return match(new StringBuilder().append(chars, i, j));
        };
    }

    public static Rule and(final Rule a, final Rule b) {
        return (chars, i) -> {
            final MatchNode am = a.match(chars, i);
            if (!am.matched) {
                return noMatch();
            }
            final MatchNode bm = b.match(chars, i + am.length);
            if (!bm.matched) {
                return noMatch();
            }
            return match(new StringBuilder().append(am.content).append(bm.content));
        };
    }

    public static Rule or(final Rule... choices) {
        return (chars, i) -> {
            for (final Rule m : choices) {
                final MatchNode n = m.match(chars, i);
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
            final MatchNode matchNode = rule.match(chars, i);
            if (matchNode.matched) {
                return noMatch();
            } else {
                return match(matchNode.content);
            }
        };
    }

    public static Rule zeroOrMore(final Rule m) {
        return (chars, i) -> {
            MatchNode n;
            int k = i;
            while ((n = m.match(chars, k)).matched) {
                k += n.length;
            }
            return match(Arrays.copyOfRange(chars, i, k));
        };
    }

    public static Rule oneOrMore(final Rule m) {
        return (chars, i) -> {
            MatchNode n;
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
