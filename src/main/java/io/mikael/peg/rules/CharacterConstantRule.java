package io.mikael.peg.rules;

import io.mikael.peg.MatchNode;
import io.mikael.peg.NoMatchNode;
import io.mikael.peg.Rule;

public class CharacterConstantRule implements Rule {

    private final char constant;

    public CharacterConstantRule(final char constant) {
        this.constant = constant;
    }

    @Override
    public MatchNode match(final char[] chars, final int i) {
        if (i < chars.length && chars[i] == constant) {
            return new MatchNode(true, 1, new StringBuilder().append(chars[i]));
        } else {
            return new NoMatchNode();
        }
    }

}
