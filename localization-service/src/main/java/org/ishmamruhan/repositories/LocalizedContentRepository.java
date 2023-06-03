package org.ishmamruhan.repositories;

import org.ishmamruhan.entities.LocalizedContents;
import org.ishmamruhan.enums.LocalizedContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalizedContentRepository extends JpaRepository<LocalizedContents,Long> {
    LocalizedContents findTopByContentIdEqualsAndLanguageCodeEqualsAndContentTypeEqualsAndFieldNameEqualsOrderByLastModifiedDateDesc(
            Long contentId, String languageCode, LocalizedContentType contentType, String fieldName);

    List<LocalizedContents> findAllByContentIdEqualsAndContentTypeEquals(Long contentId, LocalizedContentType contentType);
}
