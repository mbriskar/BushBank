/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bushbank.bushbank.nxt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.nite.meta.impl.NiteMetaData;
import net.sourceforge.nite.meta.impl.NiteMetaException;
import net.sourceforge.nite.nom.NOMException;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAnnotation;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAttribute;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteCorpus;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWritePointer;
import org.bushbank.bushbank.core.Anaphora;
import org.bushbank.bushbank.core.Annotation;
import org.bushbank.bushbank.core.MissingToken;
import org.bushbank.bushbank.core.Morphology;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import org.bushbank.bushbank.core.SyntaxRelation;
import org.bushbank.bushbank.core.Token;
import org.bushbank.bushbank.core.ValidityStatus;

/**
 *
 * @author Mato
 */
public class NxtCorpusLoader {

    private NOMWriteCorpus corpus;
    private static final String NXTSENTENCE = "s";
    private static final String NXTMORPHOLOGY = "morpho";
    private static final String NXTANAPHORA = "anaphora";
    private static final String NXTLEMMA = "lemma";
    private static final String NXTTOKENMISSING = "missing";
    private static final String NXTGRAMMARTAG = "tag";
    private static final String NXTSEMANTIC = "semantic";
    private static final String NXTSYNTAX = "syntax";
    private static final String NXTVALIDITYSTATUS = "status";
    private static final String NXTSYNTAXRELATION = "srelation";
    private static final String NXTTYPE = "type";
    private static final String NXTINRELATIONWITH = "in-relation-with";
    private String observation;

    public NxtCorpusLoader(String metadataPath, String observationName) throws NxtException {
        try {
            corpus = new NOMWriteCorpus(new NiteMetaData(metadataPath));
            corpus.loadData(corpus.getMetaData().getObservationWithName(observationName));
            corpus.completeLoad();
            observation = observationName;

            if (corpus.getMetaData().getObservationWithName(observationName) == null) {
                throw new NxtException("Observation does not exists");
            }
        } catch (NOMException ex) {
            throw new NxtException(ex.toString());
        } catch (NiteMetaException ex) {
            throw new NxtException(ex.toString());
        }
    }

    /**
     * load sentence and its parts -> load all tokens references in sentence and
     * morphology information -> load all phrases referenced in sentence
     */
    public List<Sentence> getSentences(NxtCorpus corp) {
        List<Sentence> sentences = new ArrayList<Sentence>();


        for (NOMWriteElement s : (List<NOMWriteElement>) corpus.getElementsByName(NXTSENTENCE)) {
            Sentence sentence = new Sentence(s.getID());
            List<NOMWriteElement> tokenElem = s.getChildren();
            Set<NOMWriteElement> syntaxElements = new HashSet<NOMWriteElement>();

            for (NOMWriteElement t : (List<NOMWriteElement>) tokenElem) {
                Token token;
                if (t.getAttribute(NXTTOKENMISSING) != null && "true".equals(t.getAttribute("missing").getStringValue())) {
                    token = new MissingToken(t.getID(), t.getText());
                    sentence.add(token);
                } else {
                    token = new Token(t.getID(), t.getText());
                    sentence.add(token);
                }

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
                                break;
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
                int status;
                String tag = null;
                String semantic = null;
                // load basic information to syntax element
                if (syn.getAttribute(NXTVALIDITYSTATUS) != null) {
                    if (syn.getAttribute(NXTVALIDITYSTATUS).getStringValue().equals("-1")) {
                        status = ValidityStatus.INCORRECT;
                    } else if (syn.getAttribute(NXTVALIDITYSTATUS).getStringValue().equals("1")) {
                        status = ValidityStatus.CORRECT;
                    } else {
                        status = ValidityStatus.UNKNOWN;
                    }

                } else { // if attribute not found 
                    status = ValidityStatus.UNKNOWN;
                }

                if (syn.getAttribute(NXTGRAMMARTAG) != null) {
                    tag = syn.getAttribute(NXTGRAMMARTAG).getStringValue();
                }

                Phrase phrase = new Phrase(corp, syn.getID(), sentence, status, tag);
                if (syn.getAttribute(NXTSEMANTIC) != null) {
                    semantic = syn.getAttribute(NXTSEMANTIC).getStringValue();
                    phrase.setSemantic(semantic);
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

    /*
     * Save validity status for given id. This method can be used on
     * each object, which has status attribute.
     */
    public void checkAndSaveValidityStatus(String id, Integer status) {
        NOMElement elem = corpus.getElementByID(id);

        if ((elem.getAttribute("status") == null) || (elem.getAttribute("status").getStringValue().equals(status.toString()) == false)) {
            try {
                // change status directly in NOM representation of corpus
                elem.setStringAttribute("status", status.toString());


            } catch (NOMException ex) {
                // this should not happend as we change existing element
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /*
     * Add informations (set the attributes) about syntax relations to given phrases.
     */

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

    protected void addAnaphoras(List<Sentence> sentences) {
        if (corpus.getElementsByName(NXTANAPHORA) == null) {
            return;
        }

        for (NOMWriteElement s : (List<NOMWriteElement>) corpus.getElementsByName(NXTANAPHORA)) {
            Anaphora an = new Anaphora(s.getID());
            NOMElement pointerParentSentence = ((NOMElement)s.getChildren().get(0)).getParentInFile();
            NOMElement targetParentSentence = s.getPointerWithRole(NXTINRELATIONWITH).getToElement().getParentInFile();
            Sentence pointerSentence = NxtCorpus.getSentenceById(pointerParentSentence.getID(), sentences);
            Sentence targetSentence = NxtCorpus.getSentenceById(targetParentSentence.getID(), sentences);
           
            Token pointerToken = pointerSentence.getTokenByID(((NOMElement) s.getChildren().get(0)).getID());
            an.setPointer(pointerToken);

            
            Token targetToken = targetSentence.getTokenByID(s.getPointerWithRole(NXTINRELATIONWITH).getToElement().getID());
            an.setTarget(targetToken);

            pointerSentence.addAnaphora(an);
        }
    }

    public boolean isEdited() {
        return corpus.edited();
    }

    public void saveChanges() throws NOMException {
        corpus.serializeCorpusChanged();
        System.out.println("Corpus saved succesfully");
    }

    public List<Sentence> loadSentences(NxtCorpus corp) {

        List<Sentence> sentences = getSentences(corp);
        Map<String, Phrase> phrases = NxtCorpus.getPhrases(sentences);
        addSyntaxRelations(phrases);
        addAnaphoras(sentences);

        return sentences;
    }
    /*
     * Create and save annotation for given object ID and new status. This method 
     * can be called on each object containing status attribute (dont know what is sanno).
     */

    void createAndSaveAnnotation(String id, String status) throws NOMException {

        DateFormat df = new SimpleDateFormat("y/MM/dd HH:mm");

        NOMWriteElement sAnno = new NOMWriteAnnotation(corpus, "sanno", observation, "");
        sAnno.addAttribute(new NOMWriteAttribute("status", status));
        sAnno.addAttribute(new NOMWriteAttribute("date", df.format(new Date())));
        sAnno.addAttribute(new NOMWriteAttribute("annotator", "Hugolin"));
        sAnno.addAttribute(new NOMWriteAttribute("level", "ff"));
        sAnno.addChild((NOMElement) corpus.getElementByID(id));

        sAnno.addToCorpus();
    }
    /*
     * Check and save the new relation for given phrase ID.
     */

    public void checkAndSaveRelation(String id, SyntaxRelation relation) {
        NOMElement oldSyntaxRelation = null;
        NOMElement phraseInCorpus = corpus.getElementByID(id);

        List<NOMElement> parents = phraseInCorpus.getParents();

        for (NOMElement e : parents) {
            if (e.getName().equals("srelation")) {
                oldSyntaxRelation = e;
                break;
            }
        }

        // handle 'not identified yet 
        if ((relation != null) && (relation.getType() == null)) {
            relation = null;
        }

        if ((oldSyntaxRelation != null) && (relation == null)) {
            /**
             * Delete old element in XML file if possible *
             */
            NOMElement syntaxRelationParent = oldSyntaxRelation.getParentInFile();
            try {
                syntaxRelationParent.deleteChild(oldSyntaxRelation);
            } catch (NOMException ex) {
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ((oldSyntaxRelation == null) && (relation == null)) {
            /**
             * There was no element before so no change is needed *
             */
        } else if ((oldSyntaxRelation == null) && (relation != null)) {
            /**
             * Create a new element if possible *
             */
            if (relation.getType() != null) {
                try {
                    NOMWriteElement sRelation = new NOMWriteAnnotation(corpus, "srelation", observation, "");
                    sRelation.addAttribute(new NOMWriteAttribute("type", relation.getType()));
                    sRelation.addChild(corpus.getElementByID(id));
                    sRelation.addPointer(new NOMWritePointer(corpus, "in-relation-with", null, corpus.getElementByID(relation.getParentElement().getID())));
                    sRelation.addToCorpus();
                } catch (NOMException ex) {
                    Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            /**
             * update an existing element *
             */
            try {
                /**
                 * Test if there was a real change *
                 */
                if (oldSyntaxRelation.getAttribute("type").getStringValue().equals(relation.getType())) {
                    if (oldSyntaxRelation.getPointerWithRole("in-relation-with") == null) {
                        if (relation.getParentElement() == null) {
                            return;
                        }
                    } else {
                        if (relation.getParentElement() != null) {
                            if (oldSyntaxRelation.getPointerWithRole("in-relation-with").getToElement().getID().equals(relation.getParentElement().getID())) {
                                return;
                            }
                        }
                    }
                }

                /**
                 * Change data in corpora *
                 */
                oldSyntaxRelation.setStringAttribute("type", relation.getType());
                if (oldSyntaxRelation.getPointerWithRole("in-relation-with") != null) {
                    oldSyntaxRelation.removePointer(oldSyntaxRelation.getPointerWithRole("in-relation-with"));
                }
                if (relation.getParentElement() != null) {
                    oldSyntaxRelation.addPointer(new NOMWritePointer(corpus, "in-relation-with", null, corpus.getElementByID(relation.getParentElement().getID())));
                }
            } catch (NOMException ex) {
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /*
     * Checks the annotations for the given sentences.
     */

    public List<Annotation> getAnnotations(List<Sentence> sentences) {
        List<Annotation> result = new ArrayList<Annotation>();
        List<NOMWriteElement> lsanno = corpus.getElementsByName("sanno");
        if (lsanno != null) {
            for (NOMWriteElement sanno : lsanno) {
                for (NOMWriteElement n : (List<NOMWriteElement>) sanno.getChildren()) {
                    Annotation a = new Annotation(n.getID());

                    if (sanno.getAttribute("status") != null) {

                        if (sanno.getAttribute("status").getStringValue().equals("-1")) {
                            a.setStatus("-");
                        } else if (sanno.getAttribute("status").getStringValue().equals("0")) {
                            a.setStatus("?");
                        } else if (sanno.getAttribute("status").getStringValue().equals("1")) {
                            a.setStatus("+");
                        } else if (sanno.getAttribute("status").getStringValue().equals("-2")) {
                            a.setStatus("NaN");
                        }
                    }

                    if (sanno.getAttribute("annotator") != null) {
                        a.setAuthor(sanno.getAttribute("annotator").getStringValue());
                    }
                    if (sanno.getAttribute("level") != null) {
                        a.setLevel(sanno.getAttribute("level").getStringValue());
                    }
                    if (sanno.getAttribute("date") != null) {
                        a.setDate(sanno.getAttribute("date").getStringValue());
                    }
                    /*Works only if there are only annotations for phrases. */
                    Map<String, Phrase> phrases = NxtCorpus.getPhrases(sentences);
                    Phrase p = phrases.get(n.getID());
                    a.setContent(p);
                    result.add(a);

                }
            }
        }

        return result;
    }

    /*
     * Check and save the given phrase.
     */
    void savePhrase(Phrase phrase) throws NOMException {
        if (corpus.getElementByID(phrase.getID()) == null) {
            System.out.println("NEW " + phrase);
            NOMWriteElement pElem = new NOMWriteAnnotation(corpus, "syntax", observation, "");


            pElem.addAttribute(new NOMWriteAttribute("tag", phrase.getGrammarTag()));
            pElem.addAttribute(new NOMWriteAttribute("status", String.valueOf(phrase.getValidityStatus())));
            for (Token t : phrase.getTokens()) {
                pElem.addChild(corpus.getElementByID(t.getID()));
            }

            pElem.addToCorpus();
        }
    }

    /*
     * Delete object with the given ID
     */
    void deleteObject(String id) {
        NOMElement pElem = corpus.getElementByID(id);
        if (pElem != null) {
            NOMElement parentElem = pElem.getParentInFile();
            try {
                parentElem.deleteChild(pElem);
            } catch (NOMException ex) {
                Logger.getLogger(NxtCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void saveAnaphora(Anaphora anaphora) throws NOMException {
         if (corpus.getElementByID(anaphora.getId()) == null) {
            System.out.println("NEW " + anaphora);
            NOMWriteElement pElem = new NOMWriteAnnotation(corpus, "anaphora", observation, "");
            pElem.addChild(corpus.getElementByID(anaphora.getPointer().getID()));
            pElem.addPointer(new NOMWritePointer(corpus, "in-relation-with", null, corpus.getElementByID(anaphora.getTarget().getID())));
            pElem.addToCorpus();
        }
    }

    // checks only semantic attribute
    void updateAttributes(Phrase phrase) throws NOMException {
        if (corpus.getElementByID(phrase.getID()) != null) {
            NOMElement el = corpus.getElementByID(phrase.getID());
           
            
            String newSemantic = "";
            for(String s :  phrase.getSemantic() ) {
                if(!newSemantic.equals("")) {
                    s="," +s;
                }
                newSemantic=newSemantic + s;
            }
            if(newSemantic.equals("")) {
                el.removeAttribute("semantic");
            } else {
                  el.setStringAttribute("semantic", newSemantic);
            }
          

        }
    }
    
    //not needed for now
    void updateAttributes(Anaphora anaphora) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void saveMissingToken(MissingToken token, Sentence parentSentence) throws NOMException {
        NOMElement sentenceElem = corpus.getElementByID(parentSentence.getID());

       
        NOMWriteElement tElem = new NOMWriteAnnotation(corpus, "token", observation, "");
        tElem.appendText(token.getWordForm());
        tElem.addAttribute(new NOMWriteAttribute("missing", "true"));
        sentenceElem.addChild(tElem);
            
    }
}
