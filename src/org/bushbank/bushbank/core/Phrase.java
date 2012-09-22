package org.bushbank.bushbank.core;

import java.util.HashSet;
import java.util.Set;
import org.bushbank.bushbank.nxt.NxtCorpus;

public class Phrase extends OrderedTokens {
    private NxtCorpus corpus;

    
    private Sentence parentSentence;
    private String grammarTag;
    private Set<String> semantic = new HashSet<String>();
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

    public Set<String> getSemantic() {
        return semantic;
    }

    /*
     * Set the semantic attribute. If the attribute was changed, inform corpus.
     */
    public void setSemantic(Set<String> semantic) {
        boolean changed=true;
        if(this.semantic != null) {
            changed = !this.semantic.equals(semantic);
        }
        this.semantic = semantic;
        if (changed) {
            corpus.updateAttributes(this);
        }
        
    }
    
    /*
     * Set the semantic attribute. This method is used when reading, so it 
     * does not inform corpus about the change ( corpus.updateAttributes(this) ).
     */
    public void setSemantic(String semantic) {
        this.semantic= new HashSet<String>();
        String[] array =semantic.split(",");
        for(String sem : array) {
            addSemantic(sem);
        }
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
        semantic= new HashSet<String>();
        this.status = ValidityStatus.UNKNOWN;
    }

    public Phrase (NxtCorpus corpus, String id, Sentence sentence) {
        this(id, sentence);
        this.corpus = corpus;
    }
    


    /*
     * Add string to the semantic attribute. 
     * If the attribute was changed, inform corpus.
     */
   public void addSemantic(String sem) {
       boolean changed =semantic.add(sem);
       if (changed) {
         corpus.updateAttributes(this);
       }
      
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

    public void removeSemantic(String semanticAttribute) {
        boolean changed =semantic.remove(semanticAttribute);
        if (changed) {
         corpus.updateAttributes(this);
       }
    }
}
