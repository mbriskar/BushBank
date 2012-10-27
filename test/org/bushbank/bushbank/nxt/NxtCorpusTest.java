package org.bushbank.bushbank.nxt;

import java.util.HashSet;
import org.bushbank.bushbank.core.ValidityStatus;
import org.bushbank.bushbank.core.Phrase;
import org.bushbank.bushbank.core.Sentence;
import java.util.List;
import java.util.Set;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import org.bushbank.bushbank.core.Anaphora;
import org.bushbank.bushbank.core.Annotation;
import org.bushbank.bushbank.core.MissingToken;
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
                assertEquals(3,p.getSemantic().size());
                assertTrue(p.getSemantic().contains("a") && p.getSemantic().contains("b") && p.getSemantic().contains("c"));
            }
            if (p.getID().equals("ff.syntax.3")) {

                assertEquals("k1c1gInS", p.getGrammarTag());
                assertEquals(1, p.getTokens().size());
                assertEquals("ff.text.2", p.getTokens().get(0).getID());
            }

            if (p.getID().equals("ff.syntax.5")) {
                assertEquals("k7c7", p.getGrammarTag());
                assertEquals(2, p.getTokens().size());
                assertEquals(1,p.getSemantic().size());
                assertTrue(p.getSemantic().contains("a"));
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
                assertEquals(Token.class,t.getClass());

            }
            if (t.getID().equals("ff.text.5")) {
                assertEquals("rokem", t.getWordForm());
                assertEquals(Token.class,t.getClass());
            }

            if (t.getID().equals("ff.text.8")) {
                assertEquals("opět", t.getWordForm());
                assertEquals(Token.class,t.getClass());
            }
            
            if (t.getID().equals("ff.text.13")) {
                assertEquals("co", t.getWordForm());
                assertEquals(MissingToken.class,t.getClass());
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
    public void testGetSentence_Annaphoras() throws NxtException  {
         loadSimpleFull();
         List<Anaphora> anaphoras =sentences.get(0).getAnaphoras();
         assertEquals(1,anaphoras.size());
         for(Anaphora anaphora : anaphoras) {
             if("ff.anaphora.1".equals(anaphora.getId())) {
                 assertEquals("ff.text.6", anaphora.getTarget().getID());
                 assertEquals("sešel", anaphora.getTarget().getWordForm());
                 
                 assertEquals("ff.text.4", anaphora.getPointer().getID());
                 
             }
         }
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


        corpus.getCorpusLoader().deleteObject(p1.getID());
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
    
    @Test
    public void testPhraseAttribute_semantic() throws NxtException, InterruptedException {
        loadDynamicFull();
        String stringToAdd = "newabc";
        Phrase p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        
        Set<String> oldSemanticSet = new HashSet<String>();
        Set<String> newSemanticSet = new HashSet<String>();
        for(String s : p0.getSemantic()) {
            oldSemanticSet.add(s);
            newSemanticSet.add(s);
        }
        
        newSemanticSet.add(stringToAdd);
        p0.addSemantic(stringToAdd);
        
        corpus.save();
        loadDynamicFull();
        
        p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        assertEquals(newSemanticSet,p0.getSemantic());
        p0.setSemantic(oldSemanticSet);
      
        corpus.save();
        loadDynamicFull();
        p0 = sentences.get(0).getPhraseById("ff.syntax.1");
        if(oldSemanticSet.isEmpty()) {
            assertTrue(p0.getSemantic().isEmpty());
        }else {
            assertEquals(oldSemanticSet,p0.getSemantic());
        }
        
        
        
    }

    @Test
    public void testSaveAnaphora() throws NxtException, InterruptedException {
        loadDynamicFull();
        int anaphorasNumber = sentences.get(0).getAnaphoras().size();
        Anaphora a0 = sentences.get(0).getAnaphoraById("ff.anaphora.1");
        Anaphora likeA0 = new Anaphora("new_Anaphora");
        Anaphora uniqueNew = new Anaphora("new_Anaphora2");

        likeA0.setPointer(sentences.get(0).getTokenByID("ff.text.4"));
        likeA0.setTarget(sentences.get(0).getTokenByID("ff.text.6"));

        uniqueNew.setPointer(sentences.get(0).getTokenByID("ff.text.3"));
        uniqueNew.setTarget(sentences.get(0).getTokenByID("ff.text.2"));

        assertFalse(corpus.trySaveAnaphora(a0));
        assertFalse(corpus.trySaveAnaphora(likeA0));
        assertTrue(corpus.trySaveAnaphora(uniqueNew));
 
        corpus.save();
        loadDynamicFull();
        //check if it was saved
        Anaphora savedUniqueAnaphora = null;
        for( Anaphora anaph : sentences.get(0).getAnaphoras() ) {
            if ((anaph.getPointer().getID().equals(uniqueNew.getPointer().getID())) &&
                    (anaph.getPointer().getID().equals(uniqueNew.getPointer().getID()))) {
                savedUniqueAnaphora = anaph;
            }
        }


        assertNotNull(savedUniqueAnaphora);
        assertFalse(corpus.trySaveAnaphora(uniqueNew));

        corpus.getCorpusLoader().deleteObject(savedUniqueAnaphora.getId());
        corpus.save();

        loadDynamicFull();
        savedUniqueAnaphora = null;
        for( Anaphora anaph : sentences.get(0).getAnaphoras() ) {
            if ((anaph.getPointer().getID().equals(uniqueNew.getPointer().getID())) &&
                    (anaph.getPointer().getID().equals(uniqueNew.getPointer().getID()))) {
                savedUniqueAnaphora = anaph;
            }
        }
        assertNull(savedUniqueAnaphora);
    }
    /*
     @Test
    public void testSaveAnaphoraWithMissingToken() throws NxtException, InterruptedException {
        loadDynamicFull();
        int anaphorasNumber = sentences.get(0).getAnaphoras().size();
        Anaphora a0 = sentences.get(0).getAnaphoraById("ff.anaphora.1");
        Anaphora uniqueNew = new Anaphora("new_Anaphora2");

        uniqueNew.setPhrase(sentences.get(0).getPhraseById("ff.syntax.1"));
        uniqueNew.setToken(new MissingToken("token","token"));

        assertTrue(corpus.trySaveAnaphoraWithUnsavedMissingToken(uniqueNew, sentences.get(0)));
 
        corpus.save();
        loadDynamicFull();
        //check if it was saved
        Anaphora savedUniqueAnaphora = null;
        for( Anaphora anaph : sentences.get(0).getAnaphoras() ) {
            if ((anaph.getPhrase().getID().equals(uniqueNew.getPhrase().getID())) &&
                    (anaph.getToken().getWordForm().equals(uniqueNew.getToken().getWordForm())) &&
                        (anaph.getToken() instanceof MissingToken)) {
                savedUniqueAnaphora = anaph;
            }
        }


        assertNotNull(savedUniqueAnaphora);
        assertFalse(corpus.trySaveAnaphora(uniqueNew));

        corpus.getCorpusLoader().deleteObject(savedUniqueAnaphora.getId());
        corpus.getCorpusLoader().deleteObject(savedUniqueAnaphora.getToken().getID());
        corpus.save();

        loadDynamicFull();
        savedUniqueAnaphora = null;
        for( Anaphora anaph : sentences.get(0).getAnaphoras() ) {
            if ((anaph.getPhrase().getID().equals(uniqueNew.getPhrase().getID())) &&
                    (anaph.getToken().getWordForm().equals(uniqueNew.getToken().getWordForm())) &&
                        (anaph.getToken() instanceof MissingToken)) {
                savedUniqueAnaphora = anaph;
            }
        }
        assertNull(savedUniqueAnaphora);
    }
    */
     @Test
    public void testMissingToken() throws NxtException, InterruptedException {
        loadDynamicFull();
        int tokenNumber = sentences.get(0).getTokens().size();
        Token t0 = sentences.get(0).getTokenByID("ff.text.2");
        MissingToken t = new MissingToken("a", "word");

        

        assertNotNull(corpus.trySaveMissingToken(t,sentences.get(0)));
 
        corpus.save();
        loadDynamicFull();
        //check if it was saved
        Anaphora savedUniqueAnaphora = null;
        int tokenNumber2 = sentences.get(0).getTokens().size();
        assertEquals(tokenNumber +1, tokenNumber2);
        Token savedToken = null;
        for( Token token : sentences.get(0).getTokens() ) {
            if(("word".equals(token.getWordForm())) && (token instanceof MissingToken) ){
                savedToken = token;
            }
        }


        assertNotNull(savedToken);

        corpus.getCorpusLoader().deleteObject(savedToken.getID());
        corpus.save();

        loadDynamicFull();
        savedUniqueAnaphora = null;
        savedToken = null;
        for( Token token : sentences.get(0).getTokens() ) {
            if(("word".equals(token.getWordForm())) && (token instanceof MissingToken) ){
                savedToken = token;
            }
        }


        assertNull(savedToken);
    }
}