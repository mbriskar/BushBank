package org.bushbank.bushbank.core;

/** Class that holds information information for token
 *
 *  Token is created from source after 'tokenization'. Tokenization
 *  is a process that identifies an elements of original text. These
 *  elements are usually "words" in normal language. In bushbank we do
 *  not have any specific condition for tokens and what they can/cant
 *  contain. 
 */
public class Token{
    
    private Morphology morphology;
    private String id;
    protected String wordForm;

    public Token(String id, String wordForm) {
        this.id=id;
        this.wordForm=wordForm;

    }

    public void setMorphology(Morphology morphology) { this.morphology = morphology; }



    public Morphology getMorphology() { return morphology; }

      @Override 
      public boolean equals(Object bToken) {
          if ( this == bToken ) {
              return true;
          }
          if ( !(bToken instanceof Token) ) {
              return false;
          }
          Token t2 = (Token) bToken;
          if(t2.getID().equals(this.getID())) {
              return true;
          }
          else {
              return false;
          }
      }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (getID() != null ? getID().hashCode() : 0);
        hash = 59 * hash + (this.wordForm != null ? this.wordForm.hashCode() : 0);
        hash = 59 * hash + (this.morphology != null ? this.morphology.hashCode() : 0);
        return hash;
    }
    @Override
    public String toString() {
        String res = new String();

        if (this.getWordForm() == null) {
            res += "<null>";
        } else {
            res += this.getWordForm();
        }

        if (this.getMorphology() != null) {
            res += " " + this.getMorphology();
        }

        return res;
    }
    
     public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWordForm() {
        return wordForm;
    }

    public void setWordForm(String wordForm) {
        this.wordForm = wordForm;
    }
}
