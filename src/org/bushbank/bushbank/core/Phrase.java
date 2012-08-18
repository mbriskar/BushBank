package org.bushbank.bushbank.core;

import org.bushbank.bushbank.nxt.NxtCorpus;

public class Phrase extends OrderedTokens {
    private NxtCorpus corpus;

    
    private Sentence parentSentence;
    private String grammarTag;
    private int status;
    /* @note: is it up-to-date in any case?
     * token numbers according to their position in parent sentence **/

    private SyntaxRelation parentPhrase;

    // constructor for loading (needed this big one because setValidityStatus would call corpus)
    public Phrase(NxtCorpus corpus, String id, Sentence sentence, int validityStatus, String tag) {
        this(id,sentence);

        status = validityStatus;
        grammarTag = tag;
        this.corpus = corpus;
        
    }
    
    
    
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
    }

    public Phrase (NxtCorpus corpus, String id, Sentence sentence) {
        this(id, sentence);
        this.corpus = corpus;
    }
    


   
   public void setCorpus(NxtCorpus corpus) {
        this.corpus = corpus;
    }
    public void setGrammarTag(String grammarTag) {
        this.grammarTag = grammarTag;
        
        // grammar tag can't be changed under current condition
    }
    public void setValidityStatus(int status) {
        this.status = status;
        
        if ((corpus != null) && (this.getID() != null)) {
            corpus.updateValidityStatus(this.getID(), status);
        }
    }
    public void setInRelationWith(SyntaxRelation parent) {
        this.parentPhrase = parent;

        if ((corpus != null) && (this.getID() != null)) {
            corpus.updateSetInRelationWith(this.getID(), parent);
        }
    }

    public String getGrammarTag() { return grammarTag; }
    public int getValidityStatus() { return status; }
    public Sentence getParentSentence() { return parentSentence; }

    public SyntaxRelation getInRelationWith() { return this.parentPhrase; }
}
