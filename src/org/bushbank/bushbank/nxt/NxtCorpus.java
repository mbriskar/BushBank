package org.bushbank.bushbank.nxt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.nite.meta.impl.NiteMetaData;
import net.sourceforge.nite.meta.impl.NiteMetaException;
import net.sourceforge.nite.nom.NOMException;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteCorpus;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import org.bushbank.bushbank.core.Morphology;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import org.bushbank.bushbank.core.SyntaxRelation;
import org.bushbank.bushbank.core.Token;
import org.bushbank.bushbank.core.ValidityStatus;

public class NxtCorpus {
    private NOMWriteCorpus corpus;
    private static final String NXTSENTENCE = "s";
    private static final String NXTMORPHOLOGY = "morpho";
    private static final String NXTLEMMA = "lemma";
    private static final String NXTGRAMMARTAG = "tag";
    private static final String NXTSYNTAX = "syntax";
    private static final String NXTVALIDITYSTATUS = "status";
    private static final String NXTSYNTAXRELATION = "srelation";
    private static final String NXTTYPE = "type";
    private static final String NXTINRELATIONWITH = "in-relation-with";

    /** Initialization of corpus, no data are loaded **/
    public NxtCorpus(String metadataPath, String observationName) throws NxtException {
        try {
            corpus = new NOMWriteCorpus(new NiteMetaData(metadataPath));
            corpus.loadData(corpus.getMetaData().getObservationWithName(observationName));
            corpus.completeLoad();

            if (corpus.getMetaData().getObservationWithName(observationName) == null) {
                throw new NxtException("Observation does not exists");
            }
        } catch (NOMException ex) {
            throw new NxtException(ex.toString());
        } catch (NiteMetaException ex) {
            throw new NxtException(ex.toString());
        }
    }

    public List<Sentence> loadSentences() {
        List<Sentence> sentences = this.getSentences();
        Map<String, Phrase> phrases = this.getPhrases(sentences);
        this.addSyntaxRelations(phrases);
        
        return sentences;
    }


    protected List<Sentence> getSentences() {
        List<Sentence> sentences = new ArrayList<Sentence>();

        /** load sentence and its parts
         *  -> load all tokens references in sentence and morphology information
         *  -> load all phrases referenced in sentence
         */
        for (NOMWriteElement s : (List<NOMWriteElement>) corpus.getElementsByName(NXTSENTENCE)) {
            Sentence sentence = new Sentence(s.getID());
            List<NOMWriteElement> tokenElem = s.getChildren();
            Set<NOMWriteElement> syntaxElements = new HashSet<NOMWriteElement>();

            for (NOMWriteElement t : (List<NOMWriteElement>) tokenElem) {
                Token token = new Token(t.getID(), t.getText());
                sentence.add(token);

                if (t.findAncestorsNamed(NXTMORPHOLOGY) != null) {
                    for (NOMWriteElement em : (Set<NOMWriteElement>) t.findAncestorsNamed(NXTMORPHOLOGY)) {
                        Morphology m = new Morphology(em.getID());
                        m.setLemma(em.getAttribute(NXTLEMMA).getStringValue());
                        m.setGrammarTag(em.getAttribute(NXTGRAMMARTAG).getStringValue());
                        token.setMorphology(m);
                    }
                }

                if (t.findAncestorsNamed(NXTSYNTAX) != null) {
                    for (NOMWriteElement em : (Set<NOMWriteElement>) t.findAncestorsNamed(NXTSYNTAX)) {
                        /* Parsing of syntax elements is not handled directly because we point
                         * to same syntax element from several tokens. Creating set of these elements
                         * helps us to be parse it faster.
                         */
                        boolean found = false;
                        for (NOMWriteElement e : syntaxElements) {
                            if (e.getID().equals(em.getID())) {
                                found = true;
                            }
                        }
                        if (found == false) {
                            syntaxElements.add(em);
                        }
                    }
                }
            }

            // Parsing syntactic elements (phrases) from sentence
            for (NOMWriteElement syn : syntaxElements) {
                Phrase phrase = new Phrase(syn.getID(), sentence);

                // load basic information to syntax element
                if (syn.getAttribute(NXTVALIDITYSTATUS) != null) {
                    if (syn.getAttribute(NXTVALIDITYSTATUS).getStringValue().equals("-1")) {
                        phrase.setValidityStatus(ValidityStatus.INCORRECT);
                    } else if (syn.getAttribute(NXTVALIDITYSTATUS).getStringValue().equals("1")) {
                        phrase.setValidityStatus(ValidityStatus.CORRECT);
                    } else {
                        phrase.setValidityStatus(ValidityStatus.UNKNOWN);
                    }

                    if (syn.getAttribute(NXTGRAMMARTAG) != null) {
                        phrase.setGrammarTag(syn.getAttribute(NXTGRAMMARTAG).getStringValue());
                    }
                } else {
                    phrase.setValidityStatus(ValidityStatus.UNKNOWN);
                }

                // load tokens into syntax element
                for (NOMWriteElement n : (List<NOMWriteElement>) syn.getChildren()) {
                    for (int i = 0; i < sentence.getTokens().size(); i++) {
                        if (n.getID().equals(sentence.getTokens().get(i).getID())) {
                            /*
                             * @obsolete? Adding also number of token in sentence can be derived
                             * from token/sentence relation
                             *
                             * +1 => we are counting indexes from 1
                             **/
                            phrase.add(sentence.getTokenByID(n.getID()));
                            break;
                        }
                    }
                }

                // Syntax relations could not be loaded here because we
                // need an access to phrase to which we point. Quite usually
                // this phrase can be in sentence on later position, so
                // it does not exist at the moment of running this code
            }

            sentences.add(sentence);
        }

        return sentences;
    }

    public boolean isEdited() { return corpus.edited(); }
    
    protected Map<String, Phrase> getPhrases(List<Sentence> sentences) {
        Map<String, Phrase> phrases = new HashMap<String, Phrase>();

        for (Sentence s : sentences) {
            for (Phrase p : s.getPhrases()) {
                phrases.put(p.getID(), p);
            }
        }

        return phrases;
    }

    protected void addSyntaxRelations(Map<String, Phrase> phrases) {
        if (corpus.getElementsByName(NXTSYNTAXRELATION) == null) {
            return;
        }

        for (NOMWriteElement s : (List<NOMWriteElement>) corpus.getElementsByName(NXTSYNTAXRELATION)) {
            SyntaxRelation sr = new SyntaxRelation(s.getID());
            sr.setType(s.getAttribute(NXTTYPE).getStringValue());
            
            try {
                sr.setParentElement(phrases.get(s.getPointerWithRole(NXTINRELATIONWITH).getToElement().getID()));
            } catch (NullPointerException e) {
                // in-relation-with can be null (-none-)
            }
            
            // Each phrase can have only one parent
            if (sr.getParentElement() != null) {
                phrases.get(((NOMElement) s.getChildren().get(0)).getID()).setInRelationWith(sr);
            }
        }
    }
}
