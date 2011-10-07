package org.bushbank.bushbank.core;

import java.util.Comparator;

public class PhraseComparator implements Comparator {
    private static final String CLAUSE = "clause";

    // @todo: add tests
    // @todo: better documentation with examples
    public int compare(Object arg0, Object arg1) {
        Phrase p0 = (Phrase) arg0;
        Phrase p1 = (Phrase) arg1;

        if (p0.getGrammarTag().equals(CLAUSE) && (p1.getGrammarTag().equals(CLAUSE)) == false) {
            return -1;
        } else if ((p0.getGrammarTag().equals(CLAUSE) == false) && (p1.getGrammarTag().equals(CLAUSE))) {
            return 1;
        }

        try {
            if (p0.getTokenNumbers().get(0) == p1.getTokenNumbers().get(0)) {
                return 0;
            } else if (p0.getTokenNumbers().get(0) < p1.getTokenNumbers().get(0)) {
                return -1;
            } else {
                return 1;
            }
        } catch (Exception ex) {
            // @todo: ? remove debug info
            System.out.println("ERROR " + p0.getTokenNumbers().size() + p0.getID() + " / " + p1.getTokenNumbers().size() + p1.getID());
            return (p0.getID().compareTo(p1.getID()));
        }
    }
}
