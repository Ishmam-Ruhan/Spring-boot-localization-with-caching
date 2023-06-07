package org.ishmamruhan.repositories;

import org.ishmamruhan.entities.LocalizedContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalizedContentRepository extends JpaRepository<LocalizedContents,Long> {
    LocalizedContents findTopByContentIdEqualsAndLanguageCodeEqualsAndContentTypeEqualsAndFieldNameEqualsOrderByIdDesc(
            Long contentId, String languageCode, String contentType, String fieldName);

    List<LocalizedContents> findAllByContentIdEqualsAndContentTypeEquals(Long contentId, String contentType);
}
