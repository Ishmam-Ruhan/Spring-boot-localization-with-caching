package org.ishmamruhan.services;

import org.ishmamruhan.enums.LocalizedContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LocalizationUtils<T,R> {
    T saveLocalizedData(T object,R requestBodyObject,JpaRepository<T,Long> dbRepository,LocalizedContentType contentType);
    T updateLocalizedData(T object,R requestBodyObject, JpaRepository<T,Long> dbRepository,LocalizedContentType contentType);
    void deleteLocalizedData(T object, LocalizedContentType contentType);

    T getLocalizedData(
            T object, String languageCode, LocalizedContentType contentType, String... fields);

    List<T> getLocalizedData(
            List<T> objects, String languageCode, LocalizedContentType contentType, String... fields);

    Page<T> getLocalizedData(
            Page<T> page, String languageCode, LocalizedContentType contentType, String... fields);
}
