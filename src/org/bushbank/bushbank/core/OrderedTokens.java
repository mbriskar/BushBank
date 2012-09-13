package org.bushbank.bushbank.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author marx
 */
public class OrderedTokens {
    private String id;
    private List<Token> tokens;

    public OrderedTokens(String id) {
        tokens = new ArrayList<Token>();
        this.id = id;
    }

    public List<Token> getTokens() { return Collections.unmodifiableList(tokens); }
    public String getID() { return id; }

    public void add(Token t) {
        if (t == null) throw new NullPointerException();
        tokens.add(t);
    }

    public Token getTokenByID(String id) {
        for (Token t : this.getTokens()) {
            if (t.getID() != null && t.getID().equals(id)) {
                return t;
            }
        }

        return null;
    }

    @Override
    // @todo: replace with join from some standard class
    // @note: there is an additional space at the end of string right now
    public String toString() {
        String res = new String();
        for (Token x : this.getTokens()) {
            res += x.getWordForm() + " ";
        }
        return res;
    }
}
