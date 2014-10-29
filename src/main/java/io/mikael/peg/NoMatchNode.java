package io.mikael.peg;

public class NoMatchNode extends MatchNode {

    public NoMatchNode() {
        super(false, 0, null);
    }

    @Override public String toString() {
        return "NoMatch()";
    }
}
