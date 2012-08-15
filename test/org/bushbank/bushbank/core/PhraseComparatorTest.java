package org.bushbank.bushbank.core;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Mato
 */
public class PhraseComparatorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    private List<Phrase> createPhrases(Sentence s) {
        Phrase p1 = new Phrase("Pa", s);
        Phrase p2 = new Phrase("Pb", s);
        Phrase p3 = new Phrase("Pc", s);
        Phrase p4 = new Phrase("Pd", s);
        p1.setGrammarTag("clause");
        p2.setGrammarTag("clause");
        p3.setGrammarTag("clause");
        p4.setGrammarTag("clause");
        Token t1 = new Token("a", "a");
        Token t2 = new Token("b", "b");
        Token t3 = new Token("c", "c");
        Token t33 = new Token("cc", "cc");
        Token t4 = new Token("d", "d");
        s.add(t1);
        s.add(t2);
        s.add(t3);
        s.add(t33);
        s.add(t4);
        
        p1.add(t1);
        p2.add(t2);
        p3.add(t3);
        p3.add(t33);
        p4.add(t4);

        List<Phrase> list = new ArrayList<Phrase>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        return list;
        
    }

    /**
     * Test of compare method depending on grammarTag.
     */
    @Test
    public void testCompare_clause() {
        Sentence s = new Sentence(null);
        Phrase p1 = new Phrase(null, s);
        p1.setGrammarTag("clause");
        Phrase p2 = new Phrase(null, s);
        Token t1 = new Token("a", "a");
        p1.add(t1);

        PhraseComparator instance = new PhraseComparator();
        assertEquals(-1, instance.compare(p1, p2));
        p1.setGrammarTag(null);
        p2.setGrammarTag("clause");
        assertEquals(1, instance.compare(p1, p2));

    }

     /**
     * Test of compare method depending on tokens.
     */
    @Test
    public void testCompare() {
        Sentence s = new Sentence(null);
        List<Phrase> phrases=createPhrases(s);
        PhraseComparator instance = new PhraseComparator();
        
        assertEquals(-1, instance.compare(phrases.get(0),phrases.get(1)));
        assertEquals(1, instance.compare(phrases.get(1), phrases.get(0)));
        assertEquals(-1, instance.compare(phrases.get(0), phrases.get(2)));
        assertEquals(1, instance.compare(phrases.get(2), phrases.get(0)));
        assertEquals(-1, instance.compare(phrases.get(0), phrases.get(3)));
        assertEquals(1, instance.compare(phrases.get(3), phrases.get(0)));
        assertEquals(0, instance.compare(phrases.get(2), phrases.get(2)));
        assertEquals(1, instance.compare(phrases.get(2), phrases.get(1)));
        assertEquals(0, instance.compare(phrases.get(0), phrases.get(0)));
        assertEquals(0, instance.compare(phrases.get(1), phrases.get(1)));

    }
}
