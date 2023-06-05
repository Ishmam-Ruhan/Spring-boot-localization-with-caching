package org.ishmamruhan.repositories;

import org.ishmamruhan.constants.LocalizedContentType;
import org.ishmamruhan.entities.LocalizedContents;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AppLocalizedSpecification {
    public static <T> Specification<T> getLocalizedSpecification(Map<String, Object> parameters,
                                                        String languageCode,
                                                        LocalizedContentType localizedContentType,
                                                        List<String> localizedFieldNames) {
        return Specification.where((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> subQueryPredicates = new ArrayList<>();

            Subquery<Long> subquery = null;
            Root<LocalizedContents> subqueryRoot = null;

            if(languageCode != null){
                if(localizedContentType == null){
                    throw new RuntimeException("Localized Content Type Not Provided To App Specification.");
                }

                subquery = query.subquery(Long.class);
                subqueryRoot = subquery.from(LocalizedContents.class);

                Predicate contentTypePredicate =
                        criteriaBuilder.equal(subqueryRoot.get("contentType"), localizedContentType);
                Predicate languagePredicate =
                        criteriaBuilder.equal(subqueryRoot.get("languageCode"), languageCode);

                subQueryPredicates.add(contentTypePredicate);
                subQueryPredicates.add(languagePredicate);
            }

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String filterBy = entry.getKey();
                String filterWith = entry.getValue().toString();

                if(filterWith != null && !filterWith.isEmpty()) {
                    Class<?> type = root.get(filterBy).getJavaType();

                    if (type.equals(Long.class)) {
                        predicates.add(criteriaBuilder.equal(root.get(filterBy), Long.valueOf(filterWith)));
                    } else if (type.equals(Boolean.class)) {
                        predicates.add(criteriaBuilder.equal(root.get(filterBy), Boolean.valueOf(filterWith)));
                    } else if (type.equals(String.class)) {
                        if(languageCode != null && localizedFieldNames.contains(filterBy)){
                            Predicate searchTextPredicate =
                                    criteriaBuilder.like(criteriaBuilder.upper(subqueryRoot.get("content")), "%" + filterWith.toUpperCase() + "%");
                            subQueryPredicates.add(searchTextPredicate);
                        }else{
                            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(filterBy)), "%" + filterWith.toUpperCase() + "%"));
                        }
                    } else if (type.equals(LocalDateTime.class)) {
                        LocalDate localDate = LocalDate.parse(filterWith);
                        LocalDateTime startDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
                        LocalDateTime endDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
                        predicates.add(criteriaBuilder.between(root.get(filterBy), startDateTime, endDateTime));
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get(filterBy), filterWith));
                    }
                }
            }

            if(languageCode != null){
                subquery.select(subqueryRoot.get("contentId"))
                        .where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[]{})));

                Predicate mainQueryPredicate = criteriaBuilder.in(root.get("id")).value(subquery);

                if (!predicates.isEmpty()) {
                    mainQueryPredicate =
                            criteriaBuilder.and(mainQueryPredicate, criteriaBuilder.and(predicates.toArray(new Predicate[]{})));
                }

                return mainQueryPredicate;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        });
    }

    public static <T> Specification<T> getSimpleSpecification(Map<String, Object> parameters) {
        return Specification.where((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String filterBy = entry.getKey();
                String filterWith = entry.getValue().toString();

                if(filterWith != null && !filterWith.isEmpty()) {
                    Class<?> type = root.get(filterBy).getJavaType();

                    if (type.equals(Long.class)) {
                        predicates.add(criteriaBuilder.equal(root.get(filterBy), Long.valueOf(filterWith)));
                    } else if (type.equals(Boolean.class)) {
                        predicates.add(criteriaBuilder.equal(root.get(filterBy), Boolean.valueOf(filterWith)));
                    } else if (type.equals(String.class)) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(filterBy)), "%" + filterWith.toUpperCase() + "%"));
                    } else if (type.equals(LocalDateTime.class)) {
                        LocalDate localDate = LocalDate.parse(filterWith);
                        LocalDateTime startDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
                        LocalDateTime endDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
                        predicates.add(criteriaBuilder.between(root.get(filterBy), startDateTime, endDateTime));
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get(filterBy), filterWith));
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        });
    }
}
