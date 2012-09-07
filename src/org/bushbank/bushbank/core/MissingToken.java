/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bushbank.bushbank.core;

/**
 *
 * @author Mato
 */
public class MissingToken extends Token{
    
    
     public MissingToken(String id, String wordForm) {
        super(id,wordForm);
    }
     
     public MissingToken(String id) {
         super(id,"");
     }

   
}
