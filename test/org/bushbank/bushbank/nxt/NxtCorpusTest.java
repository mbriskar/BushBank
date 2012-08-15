package org.bushbank.bushbank.nxt;

import org.bushbank.bushbank.core.ValidityStatus;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import java.util.List;
import org.bushbank.bushbank.core.SyntaxRelation;
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
    public void testUpdateValidityStatus() throws NxtException, InterruptedException {
        loadDynamicFull();
        int statusToSet=1;
        Phrase p0=sentences.get(0).getPhraseById("ff.syntax.1");
        Phrase p1=sentences.get(0).getPhraseById("ff.syntax.2");
        Phrase p2=sentences.get(0).getPhraseById("ff.syntax.3");

        //read old values
        int statusp0=p0.getValidityStatus();
        int statusp1=p1.getValidityStatus();
        int statusp2=p2.getValidityStatus();
        //set new values

        
        p0.setValidityStatus(statusToSet);
        p1.setValidityStatus(statusToSet);
        p2.setValidityStatus(statusToSet);
        corpus.save();
        //Thread.sleep(5000);
        loadDynamicFull();
        p0=sentences.get(0).getPhraseById("ff.syntax.1");
        p1=sentences.get(0).getPhraseById("ff.syntax.2");
        p2=sentences.get(0).getPhraseById("ff.syntax.3");

        //check new values
        assertEquals(statusToSet, p0.getValidityStatus());
        assertEquals(statusToSet, p1.getValidityStatus());
        assertEquals(statusToSet, p2.getValidityStatus());

        p0.setValidityStatus(statusp0);
        p1.setValidityStatus(statusp1);
        p2.setValidityStatus(statusp2);
        
        corpus.save();
        //Thread.sleep(5000);
        loadDynamicFull();
        p0=sentences.get(0).getPhraseById("ff.syntax.1");
        p1=sentences.get(0).getPhraseById("ff.syntax.2");
        p2=sentences.get(0).getPhraseById("ff.syntax.3");

        //check if old values were saved correctly
        assertEquals(statusp0, p0.getValidityStatus());
        assertEquals(statusp1, p1.getValidityStatus());
        assertEquals(statusp2, p2.getValidityStatus()); 
    }
    
    @Test
    public void testUpdateRelation() throws NxtException, InterruptedException {
        loadDynamicFull();
        SyntaxRelation relationToSet =new SyntaxRelation("abc");
        relationToSet.setType("abc");
 
        Phrase p0=sentences.get(0).getPhraseById("ff.syntax.1");
        Phrase p1=sentences.get(0).getPhraseById("ff.syntax.2");
        Phrase p2=sentences.get(0).getPhraseById("ff.syntax.3");
        Phrase p3=sentences.get(0).getPhraseById("ff.syntax.5");
        relationToSet.setParentElement(p3);
        //read old values
        SyntaxRelation relationp0=p0.getInRelationWith();
        SyntaxRelation relationp1=p1.getInRelationWith();
        SyntaxRelation relationp2=p2.getInRelationWith();
        //set new values
        
        
        p0.setInRelationWith(relationToSet);
        p1.setInRelationWith(relationToSet);
        p2.setInRelationWith(relationToSet);
        corpus.save();
        //Thread.sleep(5000);
        loadDynamicFull();
        p0=sentences.get(0).getPhraseById("ff.syntax.1");
        p1=sentences.get(0).getPhraseById("ff.syntax.2");
        p2=sentences.get(0).getPhraseById("ff.syntax.3");

        //check new values
        assertEquals(relationToSet.getParentElement().getID(), p0.getInRelationWith().getParentElement().getID());
        assertEquals(relationToSet.getParentElement().getID(), p1.getInRelationWith().getParentElement().getID());
        assertEquals(relationToSet.getParentElement().getID(), p2.getInRelationWith().getParentElement().getID());

        p0.setInRelationWith(relationp0);
        p1.setInRelationWith(relationp1);
        p2.setInRelationWith(relationp2);
        
        corpus.save();
        //Thread.sleep(5000);
        loadDynamicFull();
        p0=sentences.get(0).getPhraseById("ff.syntax.1");
        p1=sentences.get(0).getPhraseById("ff.syntax.2");
        p2=sentences.get(0).getPhraseById("ff.syntax.3");

        //check if old values were saved correctly
        assertEquals(relationp0.getParentElement().getID(), p0.getInRelationWith().getParentElement().getID());
        assertEquals(relationp1.getParentElement().getID(),p1.getInRelationWith().getParentElement().getID());
        assertEquals(relationp2.getParentElement().getID(), p2.getInRelationWith().getParentElement().getID()); 
    }
    
        
}