package org.ishmamruhan.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     *  1. Retrieve book object from database
     *  2. Send it to localizationUtils to get localized book object if language code provides.
     *      otherwise it'll return default language data
     *   ** Currently, systems default language is english.
     */
    T getLocalizedData(
            T object, String languageCode);

    /**
     *  If we need to localized list of books, we can use the following method
     *  Pass list of book objects and language code, it'll provide you same data with localized text
     */
    List<T> getLocalizedData(
            List<T> objects, String languageCode);

    /**
     *  Let's assume our Book entity have 15 properties. Nine of them are localized and the
     *  rest of them are non-localized. We need list of books with total 7 request parameters
     *  that contains both localized and non-localized data, and it needs specific language support
     *  Then the following 'getQuickProcessedData' method will take care everything. Just pass all
     *  required parameters, and you'll get your desired result.
     *
     *  Examples can be found at book-service module's BookService class.
     */

    List<T> getQuickProcessedData(
            T object,Map<String, Object> parameters,JpaRepository<T,Long> dbRepository);

    /**
     *  It also does the same as above 'getQuickProcessedData' method. but instead of providing
     *  list of data, It provides paginated data. That's why it takes one extra parameter called pageable.
     */
    Page<T> getQuickProcessedPaginatedData(
            T object,Map<String, Object> parameters, Pageable pageable,JpaRepository<T,Long> dbRepository);


    /**
     *  If we provide paged object, it'll process the paged object and returns same with localized data.
     */
    Page<T> getLocalizedData(
            Page<T> page, String languageCode);
}
