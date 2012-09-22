/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bushbank.bushbank.core;

/**
 *
 * @author Mato
 */
public class Anaphora {
    
    private Token token;
    private Phrase phrase;
    private String id;
    
    public Anaphora (String id) {
        this.id=id;
    }
    
      public Anaphora(String id,Token token, Phrase phrase) {
        this(id);
        this.token=token;
        this.phrase=phrase;
    }

    public Token getToken() {
        return token;
    }

    public String getId() {
        return id;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public void setPhrase(Phrase phrase) {
        this.phrase = phrase;
    }

    public void setId(String id) {
        this.id=id;
    }
  
    
}
