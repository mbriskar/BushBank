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
    
    private Token pointer;
    private Token target;
    private String id;
    
    public Anaphora (String id) {
        this.id=id;
    }
    
      public Anaphora(String id,Token token, Token target) {
        this(id);
        this.pointer=token;
        this.target=target;
    }

    public void setPointer(Token pointer) {
        this.pointer = pointer;
    }

    public Token getTarget() {
        return target;
    }

    public void setTarget(Token target) {
        this.target = target;
    }

    public Token getPointer() {
        return pointer;
    }

    public String getId() {
        return id;
    }

 

    public void setId(String id) {
        this.id=id;
    }
  
    
}
