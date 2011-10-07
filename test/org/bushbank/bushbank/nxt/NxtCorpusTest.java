package org.bushbank.bushbank.nxt;

import org.bushbank.bushbank.core.ValidityStatus;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marx
 */
public class NxtCorpusTest {
    private NxtCorpus corpus;
    private List<Sentence> sentences;

    public NxtCorpusTest() throws NxtException {
        // @todo: prepare smaller test corpus
        corpus = new NxtCorpus("/home/marx/CBB/23/uco333279/prase.xml", "ff");
        sentences = corpus.loadSentences();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetSentences_size() throws NxtException {
        assertEquals(400, sentences.size());
    }

    @Test
    public void testGetSentences_tokens() throws NxtException {
        // @note: ((Tradiční metody pomalého zmrazování) (nebyly) (úspěšné) .)
        assertEquals(7, sentences.get(0).getTokens().size());        
        assertEquals(4, sentences.get(0).getPhrases().size());
        assertEquals(ValidityStatus.CORRECT, sentences.get(0).getPhrases().get(0).getValidityStatus());

    }

    @Test
    public void testGetSentences_relations() throws NxtException {
        for (int i = 0; i < sentences.get(0).getPhrases().size(); i++) {
            if (sentences.get(0).getPhrases().get(i).getID().equals("ff.syntax.2")) {
                assertEquals(true, null == sentences.get(0).getPhrases().get(i).getInRelationWith());
            }

            if (sentences.get(0).getPhrases().get(i).getID().equals("ff.syntax.3")) {
                assertEquals(false, null == sentences.get(0).getPhrases().get(i).getInRelationWith());
                assertEquals("valency", sentences.get(0).getPhrases().get(i).getInRelationWith().getType());
            }
        }
    }
}