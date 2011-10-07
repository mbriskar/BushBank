package org.bushbank.bushbank.core;

/** Class that holds information information for token
 *
 *  Token is created from source after 'tokenization'. Tokenization
 *  is a process that identifies an elements of original text. These
 *  elements are usually "words" in normal language. In bushbank we do
 *  not have any specific condition for tokens and what they can/cant
 *  contain. 
 */
public class Token {
    private String id;
    private String wordForm;
    private Morphology morphology;

    public Token(String id, String wordForm) {
        this.id = id;
        this.wordForm = wordForm;
    }

    public void setMorphology(Morphology morphology) { this.morphology = morphology; }

    public String getWordForm() { return wordForm; }
    public String getID() { return id; }
    public Morphology getMorphology() { return morphology; }

    @Override
    public String toString() {
        String res = new String();

        if (this.getWordForm() == null) {
            res += "<null>";
        } else {
            res += this.getWordForm();
        }

        if (this.getMorphology() != null) {
            res += " " + this.getMorphology();
        }

        return res;
    }
}
