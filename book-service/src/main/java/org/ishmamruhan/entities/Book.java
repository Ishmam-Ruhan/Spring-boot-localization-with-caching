package org.ishmamruhan.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ishmamruhan.audit_config.AuditModel;
import org.ishmamruhan.services.Localization;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Book extends AuditModel<String> implements Serializable, Localization {

    /**
     *  At Entity level, we have to implement an interface which have a method that provides us
     *  localization field names
     *  I've implemented it at the end of this entity
     */

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

    @JsonIgnore
    @Transient
    @Override
    public List<String> availableLocalizedFields(){
        return List.of("authorName","bookTitle","bookPublisher");
    }
}
