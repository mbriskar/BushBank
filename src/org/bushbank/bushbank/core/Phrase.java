package org.bushbank.bushbank.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Phrase extends OrderedTokens {
    private Sentence parentSentence;
    private String grammarTag;
    private int status;
    /* @note: is it up-to-date in any case?
     * token numbers according to their position in parent sentence **/
    private List<Integer> tokenNumbers;
    private SyntaxRelation parentPhrase;

    public Phrase(String id, Sentence sentence) {
        super(id);

        if (sentence == null) {
            throw new NullPointerException();
        }

        this.parentSentence = sentence;
        if (this.parentSentence.addPhrase(this) == false) {
            throw new RuntimeException();
        }
        this.grammarTag = null;
        this.status = ValidityStatus.UNKNOWN;
        this.tokenNumbers = new ArrayList<Integer>();
    }

    // @todo: add test here - null; bug in parsing; correct
    public void readAndAddTokensByNumber(String line) {
        if (line == null) { return; }

        try {
            for (String s : line.split(" ")) {
                this.add(parentSentence.getTokens().get(Integer.parseInt(s) - 1));
                this.tokenNumbers.add(Integer.parseInt(s) - 1);
            }
        } catch (RuntimeException e) {
            /** If parsing fails then remove existing numbers **/
            tokenNumbers = new ArrayList<Integer>();
            throw e;
        }
    }

    public void setGrammarTag(String grammarTag) { this.grammarTag = grammarTag; }
    public void setValidityStatus(int status) { this.status = status; }
    public void setInRelationWith(SyntaxRelation parent) { this.parentPhrase = parent; }

    public String getGrammarTag() { return grammarTag; }
    public int getValidityStatus() { return status; }
    public Sentence getParentSentence() { return parentSentence; }
    public List<Integer> getTokenNumbers() {
        return Collections.unmodifiableList(tokenNumbers);
    }
    public SyntaxRelation getInRelationWith() { return this.parentPhrase; }
}
