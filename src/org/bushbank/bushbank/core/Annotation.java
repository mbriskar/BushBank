package org.bushbank.bushbank.core;

public class Annotation<T> {
    private String author;
    private String level;
    private String date;
    private String id;
    private String status;
    private T content;

 
    public Annotation(String id) { this.id = id; }

    public String getID() { return id; }
    public String getAuthor() { return author; }
    public String getLevel() { return level; }
    public String getDate() { return date; }
    public String getStatus() { return status;}
    public T getContent() { return content; }

    public void setDate(String date) { this.date = date; }
    public void setAuthor(String author) { this.author = author; }
    public void setLevel(String level) { this.level = level; }
    public void setContent(T content) { this.content = content; }
    public void setStatus(String status) { this.status = status;}
    @Override
    public String toString() {
        return "[ " + author + " / " + level + " ] => " + content.toString();
    }
}
