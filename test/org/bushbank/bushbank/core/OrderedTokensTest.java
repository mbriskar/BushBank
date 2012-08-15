package org.bushbank.bushbank.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marx
 */
public class OrderedTokensTest {

    public OrderedTokensTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testAdd_Null() {
        Token t = null;
        OrderedTokens instance = new OrderedTokens(null);
        try {
            instance.add(t);
            fail("NullPointer exception should be raised");
        } catch (NullPointerException e) {
            // this exception should be invoked, no action needed
        }
    }

    @Test
    public void testAdd() {
        Token t = new Token(null, null);
        OrderedTokens instance = new OrderedTokens(null);
        try {
            instance.add(t);
            if (instance.getTokens().size() != 1) {
                fail ("Invalid number of elements (!=1) in OrderedToken collection");
            }
            instance.add(t);
            if (instance.getTokens().size() != 2) {
                fail ("Invalid number of elements (!=2) in OrderedToken collection");
            }
        } catch (Exception e) {
            fail("No exception should be raised");
        }
    }

    @Test
    public void testGetTokenByID_empty() {
        OrderedTokens instance = new OrderedTokens(null);

        assertNull(instance.getTokenByID(null));
        assertNull(instance.getTokenByID("id#01"));
    }

    @Test
    public void testGetTokenByID() {
        OrderedTokens instance = new OrderedTokens(null);
        Token t1 = new Token("1", "alpha");
        Token t2 = new Token("2", "beta");

        instance.add(t1);
        instance.add(t2);

        assertNull(instance.getTokenByID("3"));
        assertNull(instance.getTokenByID(null));
        assertTrue(instance.getTokenByID("2") == t2);
        assertTrue(instance.getTokenByID("1") == t1);
    }
}