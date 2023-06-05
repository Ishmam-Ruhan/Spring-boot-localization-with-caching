package org.ishmamruhan.services_impl;

import org.ishmamruhan.constants.LocalizedAppConstants;
import org.ishmamruhan.constants.LocalizedContentType;
import org.ishmamruhan.entities.LocalizedContents;
import org.ishmamruhan.repositories.AppLocalizedSpecification;
import org.ishmamruhan.repositories.LocalizedContentRepository;
import org.ishmamruhan.services.LocalizationUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class LocalizationUtilsImpl<T,R> implements LocalizationUtils<T,R> {
    private final LocalizedContentRepository localizationRepository;

    public LocalizationUtilsImpl(LocalizedContentRepository localizationRepository) {
        this.localizationRepository = localizationRepository;
    }

    @Override
    public String getLanguageCode(Map<String, Object> parameters) {
        String languageCode = (String)parameters.get(LocalizedAppConstants.LOCALIZED_PARAM_NAME);
        parameters.remove(LocalizedAppConstants.LOCALIZED_PARAM_NAME);
        return languageCode;
    }

    @Override
    public Specification<T> getSpecification(
            Map<String, Object> parameters, String languageCode,
            LocalizedContentType localizedContentType, List<String> localizedFieldNames) {
        Specification<T> specification = null;

        if(!parameters.isEmpty()){
            if(languageCode != null){
                specification = AppLocalizedSpecification.getLocalizedSpecification(
                        parameters, languageCode, localizedContentType, localizedFieldNames);
            }else{
                specification = AppLocalizedSpecification.getSimpleSpecification(parameters);
            }
        }
        return specification;
    }

    @Override
    public T saveLocalizedData(
            T object,R requestBodyObject,JpaRepository<T,Long> dbRepository, LocalizedContentType contentType) {
        Long objectId = getObjectId(object);
        for(Field field : requestBodyObject.getClass().getDeclaredFields()){
            if(field.getName().startsWith(LocalizedAppConstants.LOCALIZED_FIELD_STARTS_WITH)){
                try {
                    field.setAccessible(true);
                    Object localizedField = field.get(requestBodyObject);
                    if (localizedField instanceof LinkedHashMap) {
                        for(Map.Entry<String,String> entry : ((HashMap<String,String>)localizedField).entrySet()){
                           if(entry.getKey().equals(LocalizedAppConstants.LOCALIZED_DEFAULT_LANGUAGE)){
                               String actualFieldName = field.getName().substring(LocalizedAppConstants.LOCALIZED_FIELD_STARTS_WITH.length());
                               setLocalizedField(object,actualFieldName,entry.getValue());
                           }
                           localizationRepository.save(
                                   new LocalizedContents(
                                           objectId,
                                           contentType,
                                           entry.getKey(),
                                           field.getName().substring(LocalizedAppConstants.LOCALIZED_FIELD_STARTS_WITH.length()),
                                           entry.getValue()));
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return dbRepository.save(object);
    }

    @Override
    public T updateLocalizedData(T object,R requestBodyObject,JpaRepository<T,Long> dbRepository, LocalizedContentType contentType) {
        deleteLocalizedData(object,contentType);
        return saveLocalizedData(object,requestBodyObject,dbRepository,contentType);
    }

    @Override
    public void deleteLocalizedData(T object, LocalizedContentType contentType) {
        List<LocalizedContents> localizedContents = localizationRepository.findAllByContentIdEqualsAndContentTypeEquals(
                getObjectId(object), contentType
        );
        if(!localizedContents.isEmpty()){
            localizationRepository.deleteAll(localizedContents);
        }
    }

    @Override
    public T getLocalizedData(
            T object, String languageCode, LocalizedContentType contentType, String... fields) {
        for (String field : fields) {
            LocalizedContents localizedContent = localizationRepository
                    .findTopByContentIdEqualsAndLanguageCodeEqualsAndContentTypeEqualsAndFieldNameEqualsOrderByLastModifiedDateDesc(
                            getObjectId(object), languageCode, contentType,field);
            if(localizedContent != null){
                setLocalizedField(object, field, localizedContent.getContent());
            }
        }
        return object;
    }

    @Cacheable("book_library")
    @Override
    public List<T> getLocalizedData(
            List<T> objects, String languageCode, LocalizedContentType contentType, String... fields) {
        for (T object : objects) {
            getLocalizedData(object,languageCode,contentType, fields);
        }
        return objects;
    }

    @Cacheable("book_library")
    @Override
    public Page<T> getLocalizedData(
            Page<T> page, String languageCode, LocalizedContentType contentType, String... fields) {
        List<T> content = page.getContent();
        content = getLocalizedData(content, languageCode,contentType, fields);

        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private Long getObjectId(T object) {
        try {
            Field idField = object.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(object);
            if (idValue instanceof Long) {
                return (Long) idValue;
            }
        } catch (Exception e) {
            return 0L;
        }
        return 0L;
    }

    private void setLocalizedField(T object, String field, String localizedText) {
        try {
            Field declaredField = object.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(object, localizedText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
