package org.bushbank.bushbank.core;

public class SyntaxRelation {
    private String id;
    private Phrase parentElement;
    private String type;

    public SyntaxRelation(String id) { this.id = id; }

    public String getType() { return type; }

    public Phrase getParentElement() { return parentElement; }

    public String getID() { return id; }

    public void setType(String type) { this.type = type; }

    public void setParentElement(Phrase parent) { this.parentElement = parent; }

    @Override
    public String toString() {
        if (parentElement == null) {
            return "not identified yet";
        } else {
            return type + ": " + parentElement.toString();
        }
    }
}
