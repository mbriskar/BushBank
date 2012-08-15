package org.bushbank.bushbank.core;

import java.util.Comparator;

public class PhraseComparator implements Comparator {
    private static final String CLAUSE = "clause";


    /**
     * Compares two phrases depending on the grammar tag and tokens.
     * Rules : 1. Clauses are before others
     *         2. The first phrase is the one which has the first word 
     *            sooner (with smaller index) in the parent sentence.
     * 
     * Example for the second rule
     * Sentence : This is the comparison example.
     * Clause1 : is example.
     * Clause2 : the comparison example.
     * Clause3 : the example
     * compare(Clause1,Clause2) == -1
     * compare(Clause2,Clause3) == 0
     * compare(Clause3,Clause1) == 1
     */
    public int compare(Object arg0, Object arg1) {
        Phrase p0 = (Phrase) arg0;
        Phrase p1 = (Phrase) arg1;

        if (CLAUSE.equals(p0.getGrammarTag()) && (CLAUSE.equals(p1.getGrammarTag())) == false) {
            return -1;
        } else if ((CLAUSE.equals(p0.getGrammarTag()) == false) && (CLAUSE.equals(p1.getGrammarTag()))) {
            return 1;
        }
        if (p0.getTokens().isEmpty() && !p1.getTokens().isEmpty()) {
            return 1;
        }
        if (p1.getTokens().isEmpty() && !p0.getTokens().isEmpty()) {
            return -1;
        }
        // search for order number of first word in phrase p0 
        int p0OrderNumber;
        for (p0OrderNumber=0; p0OrderNumber<p0.getParentSentence().getTokens().size() ; p0OrderNumber++) {
            String sentenceTokenId=p0.getParentSentence().getTokens().get(p0OrderNumber).getID();
            if(sentenceTokenId.equals(p0.getTokens().get(0).getID())) {
                break;
            }
        }
        int p1OrderNumber;
        for (p1OrderNumber=0; p1OrderNumber<p1.getParentSentence().getTokens().size() ; p1OrderNumber++) {
            String sentenceTokenId=p1.getParentSentence().getTokens().get(p1OrderNumber).getID();
            if(sentenceTokenId.equals(p1.getTokens().get(0).getID())) {
                break;
            }
        }
        
        
        
      //  try {
            if (p0OrderNumber == p1OrderNumber) {
                return 0;
            } else if (p0OrderNumber < p1OrderNumber) {
                return -1;
            } else {
                return 1;
            }
     //   } catch (Exception ex) {
            // @todo: ? remove debug info
      //      System.out.println("ERROR " + p0.getTokens().size() + p0.getID() + " / " + p1.getTokenNumbers().size() + p1.getID());
      //      return (p0.getID().compareTo(p1.getID()));
     //   }
    }
}
