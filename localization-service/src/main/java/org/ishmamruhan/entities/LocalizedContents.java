package org.ishmamruhan.entities;

import org.ishmamruhan.audit_config.AuditModel;

import javax.persistence.*;

@Entity
public class LocalizedContents extends AuditModel<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long contentId;
    private String contentType;
    private String languageCode;
    private String fieldName;
    @Column(columnDefinition = "TEXT")
    private String content;

    public LocalizedContents() {
    }

    public LocalizedContents(Long contentId, String contentType, String languageCode, String fieldName, String content) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.languageCode = languageCode;
        this.fieldName = fieldName;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
