package org.bushbank.bushbank.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** This class represents sentence as written in corpora
 *
 *  Class contains pointers to all tokens and phrases that belongs
 *  in it.
 */
public class Sentence extends OrderedTokens {
    private List<Phrase> phrases;

    public Sentence(String id) {
        super(id);
        phrases = new ArrayList<Phrase>();
    }

    public boolean addPhrase(Phrase phrase) {
        /** do not add duplicate phrases **/
        if (phrases.contains(phrase) == false) {
            phrases.add(phrase);
            return true;
        } else {
            return false;
        }
    }
    
    public Phrase getPhraseById(String id) {
        for (Phrase p : phrases) {
            if(p.getID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public boolean removePhrase(Phrase phrase) { return phrases.remove(phrase); }
    public List<Phrase> getPhrases() { return Collections.unmodifiableList(phrases); }
}
