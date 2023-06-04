package org.ishmamruhan.entities;

import org.ishmamruhan.audit_config.AuditModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Book extends AuditModel<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String authorName;
    private String bookTitle;
    private String bookPublisher;
    private String bookEIN;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookEIN() {
        return bookEIN;
    }

    public void setBookEIN(String bookEIN) {
        this.bookEIN = bookEIN;
    }

    public String[] availableLocalizedFields(){
        return new String[]{"authorName","bookTitle","bookPublisher"};
    }
}
