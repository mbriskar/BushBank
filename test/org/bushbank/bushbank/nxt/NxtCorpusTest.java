package org.bushbank.bushbank.nxt;

import org.bushbank.bushbank.core.ValidityStatus;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import java.util.List;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import org.bushbank.bushbank.core.Annotation;
import org.bushbank.bushbank.core.SyntaxRelation;
import org.bushbank.bushbank.core.Token;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author xbriskar
 */
public class NxtCorpusTest {

    private NxtCorpus corpus;
    private List<Sentence> sentences;

    private void loadSimpleFull() throws NxtException {
        corpus = new NxtCorpus("test/files/simplefull/prase.xml", "ff");
        sentences = corpus.getSentences();
    }

    private void loadTextOnly() throws NxtException {
        corpus = new NxtCorpus("test/files/textonly/prase.xml", "ff");
        sentences = corpus.getSentences();
    }

    private void loadDynamicFull() throws NxtException {
        corpus = new NxtCorpus("test/files/dynamicfull/prase.xml", "ff");
        sentences = corpus.getSentences();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetSentence_Size() throws NxtException {
        loadSimpleFull();
        assertEquals(1, sentences.size());
        loadTextOnly();
        assertEquals(1, sentences.size());
    }

    @Test
    public void testGetSentence_Attributes() throws NxtException {
        loadSimpleFull();
        //phrases attributes
        for (Phrase p : sentences.get(0).getPhrases()) {
            if (p.getID().equals("ff.syntax.1")) {
                assertEquals("clause", p.getGrammarTag());
                assertEquals(5, p.getTokens().size());
            }
            if (p.getID().equals("ff.syntax.3")) {

                assertEquals("k1c1gInS", p.getGrammarTag());
                assertEquals(1, p.getTokens().size());
                assertEquals("ff.text.2", p.getTokens().get(0).getID());
            }

            if (p.getID().equals("ff.syntax.5")) {
                assertEquals("k7c7", p.getGrammarTag());
                assertEquals(2, p.getTokens().size());
            }

            if (p.getID().equals("ff.syntax.8")) {
                assertEquals("k6eAd1", p.getGrammarTag());
                assertEquals(1, p.getTokens().size());
                assertEquals("ff.text.8", p.getTokens().get(0).getID());
            }
        }
        //token attributes
        for (Token t : sentences.get(0).getTokens()) {
            if (t.getID().equals("ff.text.2")) {
                assertEquals("Rok", t.getWordForm());

            }
            if (t.getID().equals("ff.text.5")) {
                assertEquals("rokem", t.getWordForm());
            }

            if (t.getID().equals("ff.text.8")) {
                assertEquals("opět", t.getWordForm());
            }
        }

    }

    @Test
    public void testGetSentence_Count() throws NxtException {
        loadSimpleFull();
        // @note: ((Tradiční metody pomalého zmrazování) (nebyly) (úspěšné) .)
        assertEquals(16, sentences.get(0).getTokens().size());
        assertEquals(13, sentences.get(0).getPhrases().size());
        //assertEquals(ValidityStatus.CORRECT, sentences.get(0).getPhrases().get(0).getValidityStatus());
        loadTextOnly();
        assertEquals(16, sentences.get(0).getTokens().size());
        assertEquals(0, sentences.get(0).getPhrases().size());

    }

    @Test
    public void tesGetSentence_Relations() throws NxtException {
        loadSimpleFull();
        for (int i = 0; i < sentences.get(0).getPhrases().size(); i++) {
            Phrase phrase = sentences.get(0).getPhrases().get(i);


            if (phrase.getID().equals("ff.syntax.2")) {
                assertEquals(true, null == phrase.getInRelationWith());
            }

            if (phrase.getID().equals("ff.syntax.3")) {
                assertEquals(true, null == phrase.getInRelationWith());
            }

            if (phrase.getID().equals("ff.syntax.4")) {
                Phrase phraseInRelation = phrase.getInRelationWith().getParentElement();
                assertEquals("ff.syntax.2", phraseInRelation.getID());
                assertEquals("verb", phrase.getInRelationWith().getType());
            }

            if (phrase.getID().equals("ff.syntax.5")) {
                Phrase phraseInRelation = phrase.getInRelationWith().getParentElement();
                assertEquals("ff.syntax.3", phraseInRelation.getID());
                assertEquals("noun", phrase.getInRelationWith().getType());
            }
        }
    }

    @Test
    public void testGetAnnotation() throws NxtException {
        loadSimpleFull();
        List<Annotation> annotations =corpus.getAnnotations();
        assertEquals(5, annotations.size());
        for (Annotation a : annotations) {
            if("ff.syntax-annotation.1".equals(a.getID())) {
                Phrase content = (Phrase) a.getContent();
                assertEquals("ff.syntax.7", content.getID());
                assertEquals("vp", content.getGrammarTag());
                assertEquals(1, content.getTokens().size());
            }
            if("ff.syntax-annotation.2".equals(a.getID())) {
                Phrase content = (Phrase) a.getContent();
                assertEquals("ff.syntax.2", content.getID());
                assertEquals("vp", content.getGrammarTag());
                assertEquals(1, content.getTokens().size());
            }
            
            if("ff.syntax-annotation.3".equals(a.getID())) {
                Phrase content = (Phrase) a.getContent();
                assertEquals("ff.syntax.6", content.getID());
                assertEquals("clause", content.getGrammarTag());
                assertEquals(5, content.getTokens().size());
            }
        }
    }

    @Test
    public void testSavePhrase() throws NxtException, InterruptedException {
        loadDynamicFull();
        int phraseNumber = sentences.get(0).getPhrases().size();
        Phrase p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        Phrase p1 = new Phrase("new_IDDDD", sentences.get(0));
        Phrase p3 = new Phrase("new_ID", sentences.get(0));

        p3.add(sentences.get(0).getTokenByID("ff.text.6"));
        p1.add(sentences.get(0).getTokenByID("ff.text.4"));
        p1.setGrammarTag("Testing");
        p1.setValidityStatus(0);

        assertFalse(corpus.trySavePhrase(p0));
        assertFalse(corpus.trySavePhrase(p3));
        assertTrue(corpus.trySavePhrase(p1));
        corpus.save();
        loadDynamicFull();
        p1 = null;
        for (Phrase ph : sentences.get(0).getPhrases()) {

            if (ph.getID().equals("ff.syntax." + (phraseNumber + 1))) {
                p1 = ph;
            }
        }


        assertNotNull(p1);
        assertEquals("Testing", p1.getGrammarTag());


        corpus.getCorpusLoader().deletePhrase(p1.getID());
        corpus.save();

        loadDynamicFull();
        p1 = null;
        for (Phrase ph : sentences.get(0).getPhrases()) {
            if ("Testing".equals(ph.getGrammarTag())) {
                p1 = ph;
            }
        }
        assertNull(p1);
    }

    @Test
    public void testUpdateRelation() throws NxtException, InterruptedException {
        loadDynamicFull();
        SyntaxRelation relationToSet = new SyntaxRelation("abc");
        relationToSet.setType("abc");

        Phrase p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        Phrase p1 = sentences.get(0).getPhraseById("ff.syntax.2");
        Phrase p2 = sentences.get(0).getPhraseById("ff.syntax.3");
        Phrase p3 = sentences.get(0).getPhraseById("ff.syntax.5");
        relationToSet.setParentElement(p3);
        //read old values
        SyntaxRelation relationp0 = p0.getInRelationWith();
        SyntaxRelation relationp1 = p1.getInRelationWith();
        SyntaxRelation relationp2 = p2.getInRelationWith();
        //set new values


        p0.setInRelationWith(relationToSet);
        p1.setInRelationWith(relationToSet);
        p2.setInRelationWith(relationToSet);
        corpus.save();
        loadDynamicFull();
        p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        p1 = sentences.get(0).getPhraseById("ff.syntax.2");
        p2 = sentences.get(0).getPhraseById("ff.syntax.3");

        //check new values
        assertEquals(relationToSet.getParentElement().getID(), p0.getInRelationWith().getParentElement().getID());
        assertEquals(relationToSet.getParentElement().getID(), p1.getInRelationWith().getParentElement().getID());
        assertEquals(relationToSet.getParentElement().getID(), p2.getInRelationWith().getParentElement().getID());

        p0.setInRelationWith(relationp0);
        p1.setInRelationWith(relationp1);
        p2.setInRelationWith(relationp2);

        corpus.save();

        loadDynamicFull();
        p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        p1 = sentences.get(0).getPhraseById("ff.syntax.2");
        p2 = sentences.get(0).getPhraseById("ff.syntax.3");

        //check if old values were saved correctly
        assertEquals(relationp0.getParentElement().getID(), p0.getInRelationWith().getParentElement().getID());
        assertEquals(relationp1.getParentElement().getID(), p1.getInRelationWith().getParentElement().getID());
        assertEquals(relationp2.getParentElement().getID(), p2.getInRelationWith().getParentElement().getID());
    }
}