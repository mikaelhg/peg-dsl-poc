package io.mikael.peg;

@FunctionalInterface
public interface Rule {

    default public MatchNode match(String input) {
        return match(input.toCharArray(), 0);
    }

    public MatchNode match(char[] chars, int i);

}
