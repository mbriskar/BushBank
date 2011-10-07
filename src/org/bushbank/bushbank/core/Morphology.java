package org.bushbank.bushbank.core;

/** Class that holds information about morphology level
 *
 *  For each token we can have an information about morphology
 *  usually it contains lemma / grammar tag. This information have
 *  to be disambiguated. To cover ambiguity you should use annotations.
 */
public class Morphology {
    private String id;
    private String lemma;
    private String grammarTag;

    public Morphology(String id) {
        this.id = id;
    }

    public void setLemma(String lemma) { this.lemma = lemma; }
    public void setGrammarTag(String grammarTag) { this.grammarTag = grammarTag; }

    public String getID() { return id; }
    public String getLemma() { return lemma; }
    public String getGrammarTag() { return grammarTag; }

    @Override
    public String toString() {
        return "<" + lemma + ":" + grammarTag + ">";
    }
}
