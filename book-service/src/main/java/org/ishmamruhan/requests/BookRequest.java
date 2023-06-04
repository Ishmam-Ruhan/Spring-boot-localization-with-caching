package org.ishmamruhan.requests;

import org.ishmamruhan.constants.LocalizedAppConstants;

import java.util.HashMap;
import java.util.Map;

public class BookRequest {
    private Long id;
    private Map<String,String> _localization_authorName = new HashMap<>();
    private Map<String,String> _localization_bookTitle = new HashMap<>();
    private Map<String,String> _localization_bookPublisher = new HashMap<>();
    private String bookEIN;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> get_localization_authorName() {
        return _localization_authorName;
    }

    public void set_localization_authorName(Map<String, String> _localization_authorName) {
        this._localization_authorName = _localization_authorName;
    }

    public Map<String, String> get_localization_bookTitle() {
        return _localization_bookTitle;
    }

    public void set_localization_bookTitle(Map<String, String> _localization_bookTitle) {
        this._localization_bookTitle = _localization_bookTitle;
    }

    public Map<String, String> get_localization_bookPublisher() {
        return _localization_bookPublisher;
    }

    public void set_localization_bookPublisher(Map<String, String> _localization_bookPublisher) {
        this._localization_bookPublisher = _localization_bookPublisher;
    }

    public String getBookEIN() {
        return bookEIN;
    }

    public void setBookEIN(String bookEIN) {
        this.bookEIN = bookEIN;
    }

    public boolean hasAnyErrorWithLocalization(){
        if(!_localization_authorName.containsKey(LocalizedAppConstants.LOCALIZED_DEFAULT_LANGUAGE))return true;
        if(!_localization_bookPublisher.containsKey(LocalizedAppConstants.LOCALIZED_DEFAULT_LANGUAGE))return true;
        if(!_localization_bookTitle.containsKey(LocalizedAppConstants.LOCALIZED_DEFAULT_LANGUAGE))return true;
        return false;
    }
}
