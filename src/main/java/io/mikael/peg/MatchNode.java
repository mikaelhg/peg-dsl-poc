package io.mikael.peg;

public class MatchNode {

    public final int length;
    public final CharSequence content;
    public final boolean matched;

    public MatchNode(final boolean matched, final int length, final CharSequence content) {
        this.length = length;
        this.content = content;
        this.matched = matched;
    }

    @Override
    public String toString() {
        return String.format("MatchNode(%s, %s, %s)", length, content, matched);
    }

}
