package org.bushbank.bushbank.nxt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.nite.nom.NOMException;
import org.bushbank.bushbank.core.Anaphora;
import org.bushbank.bushbank.core.Annotation;
import org.bushbank.bushbank.core.MissingToken;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import org.bushbank.bushbank.core.SyntaxRelation;
import org.bushbank.bushbank.core.Token;

public class NxtCorpus {

    private NxtCorpusLoader corpusLoader;
    // used to set if save new annotations with each edited file.
    private boolean addAnnotations = true;
    private Map<String, String> validityStatusUpdated; //<id,status>
    private Set<String> relationUpdated;
    private List<Sentence> sentences = null;
    private List<Annotation> annotations = null;

    /**
     * Initialization of corpus, no data are loaded *
     */
    public NxtCorpus(String metadataPath, String observationName) throws NxtException {
        corpusLoader = new NxtCorpusLoader(metadataPath, observationName);
        validityStatusUpdated = new HashMap<String, String>();
    }

    public boolean isAddAnnotations() {
        return addAnnotations;
    }

    public void setAddAnnotations(boolean addAnnotations) {
        this.addAnnotations = addAnnotations;
    }

    /**
     * Load sentences with phrases, tokens and syntax relations *
     */
    public List<Sentence> getSentences() {
        if (sentences == null) {
            sentences = corpusLoader.loadSentences(this);
        }
        return sentences;
    }

    public NxtCorpusLoader getCorpusLoader() {
        return corpusLoader;
    }

    public Sentence getSentence(int position) {
        return sentences.get(position);
    }

    /**
     * Load annotations for sentences. If sentences were not loaded, they will
     * be loaded before annotations.
     */
    public List<Annotation> getAnnotations() {
        if (annotations == null && sentences == null) {
            getSentences();
            annotations = corpusLoader.getAnnotations(sentences);
        } else if (sentences != null && annotations == null) {
            annotations = corpusLoader.getAnnotations(sentences);

        }

        return annotations;
    }

    public void deleteObject(String id) {
        corpusLoader.deleteObject(id);
    }

    /**
     * Tries to save a new created Phrase to file. Return false if there is a
     * Phrase with a given ID or if there is a Phrase with the same Tokens.
     *
     */
    public boolean trySavePhrase(Phrase phrase) {
        boolean result = false;
        boolean isNew = true;
        // we need to load it again, as the sentences attribute contain phrases which are not saved
        List<Sentence> loadedSentences = corpusLoader.loadSentences(this);
        Map<String, Phrase> phrases = getPhrases(loadedSentences);
        List<Phrase> sameSentence = new ArrayList<Phrase>();

        //check if they dont have the same ID
        if ((phrases.containsKey(phrase.getID()))) {
            isNew = false;
        }
        //check if they do not have the same Tokens
        for (Phrase p : phrases.values()) {
            if (p.getParentSentence().getID().equals(phrase.getParentSentence().getID())) {
                //are in same sentence
                if (p.getTokens().equals(phrase.getTokens())) {
                    isNew = false;
                }
            }
        }


        if (isNew) {
            try {
                corpusLoader.savePhrase(phrase);
                result = true;
            } catch (NOMException ex) {
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }
    
    public boolean trySaveAnaphora(Anaphora anaphora) {
        boolean saved = false;
        boolean isNew = true;

        // we need to load it again, as the sentences attribute contain phrases which are not saved
        // it may be irrelevant
        List<Sentence> loadedSentences = corpusLoader.loadSentences(this);
        Sentence anaphoraSentence = null;
        
        for (Sentence s : loadedSentences) {
            for(Token t:s.getTokens()) {
                if(t.getID().equals(anaphora.getPointer().getID())) {
                    anaphoraSentence = s;
                    break;
                }
            }
        }

        if (anaphoraSentence == null) {
            return false;
        }

        List<Anaphora> savedAnaphoras = anaphoraSentence.getAnaphoras();

        for (Anaphora a : savedAnaphoras) {
            if (a.getId().equals(anaphora.getId())) {
                isNew = false;
            }

            if ((a.getPointer().equals(anaphora.getPointer()))
                    && (a.getTarget().equals(anaphora.getTarget()))) {
                //anaphora with same token and phrase is already there
                isNew = false;
            }
        }

        if (isNew) {
            try {
                corpusLoader.saveAnaphora(anaphora);
                saved = true;
            } catch (NOMException ex) {
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(saved) {
            updateAnaphoraId(anaphora);
        }

        return saved;



    }

    private String updateAnaphoraId(Anaphora anaphora) {
        List<Sentence> sentences = corpusLoader.loadSentences(this);
        for (Sentence s : sentences) {
            for(Token t : s.getTokens()) {
             if (anaphora.getPointer().getID().equals(t.getID())) {
                for (Anaphora a : s.getAnaphoras()) {
                    if ((a.getPointer().getID().equals(anaphora.getPointer().getID())) && (a.getTarget().getID().equals(anaphora.getTarget().getID()))) {
                        anaphora.setId(a.getId());
                        return a.getId();
                    }
                }
            }}
        }
        return null;
    }

    /**
     * Get all the phrases which are in the given sentences.
     */
    public static Map<String, Phrase> getPhrases(List<Sentence> sentences) {
        Map<String, Phrase> phrases = new HashMap<String, Phrase>();

        for (Sentence s : sentences) {
            for (Phrase p : s.getPhrases()) {
                phrases.put(p.getID(), p);
            }
        }

        return phrases;
    }
    
     public static Map<String, Token> getTokens(List<Sentence> sentences) {
        Map<String, Token> tokens = new HashMap<String, Token>();

        for (Sentence s : sentences) {
            for (Token t : s.getTokens()) {
                tokens.put(t.getID(), t);
            }
        }

        return tokens;
    }

    public static Sentence getSentenceById(String id, List<Sentence> sentences) {
        if (id == null) {
            return null;
        }
        for (Sentence sentence : sentences) {
            if (id.equals(sentence.getID())) {
                return sentence;
            }
        }
        return null;
    }

    /**
     * Save all the changes to xml files. *
     */
    public void save() {
        try {
            // Creating an annotation for all the objects which has changed the status.
            if (addAnnotations) {
                for (Iterator<Entry<String, String>> it = validityStatusUpdated.entrySet().iterator(); it.hasNext();) {
                    Entry s = it.next();
                    corpusLoader.createAndSaveAnnotation((String) s.getKey(), (String) s.getValue());
                }
            }
            validityStatusUpdated.clear();
            corpusLoader.saveChanges();

        } catch (NOMException ex) {
            Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Called only from OBJECT, when it changes its status. Use this method when
     * you want to save status for given object id (doesnt need to be Phrase
     * object).
     */
    public void updateValidityStatus(String id, int status) {
        corpusLoader.checkAndSaveValidityStatus(id, status);
        // we will create for this IDs an annotation ( only for the last selected option) when saving at the end.
        validityStatusUpdated.put(id, Integer.toString(status));
    }

    /**
     * Called only from child object, when it changes its relation.
     */
    public void updateSetInRelationWith(String id, SyntaxRelation parent) {
        corpusLoader.checkAndSaveRelation(id, parent);
    }

    /**
     * Called only from phrase, when it changes its attribute.
     */
    public void updateAttributes(Phrase phrase) {
        try {
            corpusLoader.updateAttributes(phrase);
        } catch (NOMException ex) {
            Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/*
    public boolean trySaveAnaphoraWithUnsavedMissingToken(Anaphora anaphora, Sentence parentSentence) {
        MissingToken t = (MissingToken) anaphora.getToken();
        boolean saved = false;
        // we need to get saved ID of the missing TOKEN
        if (trySaveMissingToken(t, parentSentence)) {
            sentences = corpusLoader.loadSentences(this);
            Sentence newParentSentence = null;
            for (Sentence s : sentences) {
                if (s.getID().equals(parentSentence.getID())) {
                    newParentSentence = s;
                }
            }
            for (Token tok : newParentSentence.getTokens()) {
                if (((tok.getWordForm().equals(anaphora.getToken().getWordForm()))) && (tok instanceof MissingToken)) {
                    anaphora.setToken(tok);
                    saved = trySaveAnaphora(anaphora);
                }
            }
        }
        if(saved) {
            updateAnaphoraId(anaphora);
        }
        
        return saved;

    }
*/
    public boolean trySaveMissingToken(MissingToken token, Sentence parentSentence) {
        try {
            corpusLoader.saveMissingToken(token, parentSentence);
        } catch (NOMException ex) {
            Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
