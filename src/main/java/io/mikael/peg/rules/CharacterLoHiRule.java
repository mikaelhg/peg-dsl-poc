package io.mikael.peg.rules;

import io.mikael.peg.MatchNode;
import io.mikael.peg.NoMatchNode;
import io.mikael.peg.Rule;

public class CharacterLoHiRule implements Rule {

    private final char low, high;

    public CharacterLoHiRule(final char low, final char high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public MatchNode match(final char[] chars, final int i) {
        if (i < chars.length && chars[i] >= low && chars[i] <= high) {
            return new MatchNode(true, 1, new StringBuilder().append(chars[i]));
        } else {
            return new NoMatchNode();
        }
    }

}
