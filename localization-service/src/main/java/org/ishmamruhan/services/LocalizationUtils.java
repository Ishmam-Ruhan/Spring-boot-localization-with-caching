package org.ishmamruhan.services;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LocalizationUtils<T,R> {
    String getLanguageCode(Map<String, Object> parameters);

    Specification<T> getSpecification(T object ,
            Map<String, Object> parameters, String languageCode);

    T saveLocalizedData(T object, R requestBodyObject, JpaRepository<T,Long> dbRepository);
    T updateLocalizedData(T object,R requestBodyObject, JpaRepository<T,Long> dbRepository);
    void deleteLocalizedData(T object);

    T getLocalizedData(
            T object, String languageCode);

    List<T> getLocalizedData(
            List<T> objects, String languageCode);

    Page<T> getLocalizedData(
            Page<T> page, String languageCode);
}
