package org.bushbank.bushbank.nxt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.nite.nom.NOMException;
import org.bushbank.bushbank.core.Annotation;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import org.bushbank.bushbank.core.SyntaxRelation;

public class NxtCorpus {

    private NxtCorpusLoader corpusLoader;

    
    Map<String, String> validityStatusUpdated; //<id,status>
    Set<String> relationUpdated;
    List<Sentence> sentences = null;
    List<Annotation> annotations = null;

    /**
     * Initialization of corpus, no data are loaded *
     */
    public NxtCorpus(String metadataPath, String observationName) throws NxtException {
        corpusLoader = new NxtCorpusLoader(metadataPath, observationName);
        validityStatusUpdated = new HashMap<String, String>();
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

    /**
     * Load annotations for sentences.
     * If sentences were not loaded, they will be loaded before annotations.
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
    
    public boolean trySavePhrase(Phrase phrase) {
        boolean result = false;
        boolean isNew =true;
        // we need to load it again, as the sentences attribute contain phrases which are not saved
        List<Sentence> loadedSentences = corpusLoader.getSentences(this);
        
        if((getPhrases(loadedSentences).containsKey(phrase.getID()))) {
            isNew=false;
        }
        
        if(isNew && phrase !=null ) {
            try {
                corpusLoader.savePhrase(phrase);
                result=true;
            } catch (NOMException ex) {
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }
    
    public static Map<String, Phrase> getPhrases(List<Sentence> sentences) {
        Map<String, Phrase> phrases = new HashMap<String, Phrase>();

        for (Sentence s : sentences) {
            for (Phrase p : s.getPhrases()) {
                phrases.put(p.getID(), p);
            }
        }

        return phrases;
    }

    /**
     * Save all the changes to xml files. *
     */
    public void save() {
        try {
            // Creating an annotation for all the objects which has changed the status.
            for (Iterator<Entry<String, String>> it = validityStatusUpdated.entrySet().iterator(); it.hasNext();) {
                Entry s = it.next();
                corpusLoader.createAndSaveAnnotation((String) s.getKey(), (String) s.getValue());
            }
            validityStatusUpdated.clear();
            corpusLoader.saveChanges();

        } catch (NOMException ex) {
            Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Called only from Phrase object, when it changes its status *
     */
    public void updateValidityStatus(String id, int status) {
        corpusLoader.checkAndSaveValidityStatus(id, status);
        // we will create for this IDs an annotation ( only for the last selected option) when saving at the end.
        validityStatusUpdated.put(id, Integer.toString(status));
    }

    /**
     * Called only from Phrase object, when it changes its relation *
     */
    public void updateSetInRelationWith(String phraseId, SyntaxRelation parent) {
        corpusLoader.checkAndSaveRelation(phraseId, parent);
    }


}
