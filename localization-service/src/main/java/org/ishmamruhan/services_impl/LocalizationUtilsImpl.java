package org.ishmamruhan.services_impl;

import org.ishmamruhan.constants.LocalizedAppConstants;
import org.ishmamruhan.entities.LocalizedContents;
import org.ishmamruhan.repositories.AppLocalizedSpecification;
import org.ishmamruhan.repositories.LocalizedContentRepository;
import org.ishmamruhan.services.LocalizationUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    public Specification<T> getSpecification(T object,
            Map<String, Object> parameters, String languageCode) {
        Specification<T> specification = null;

        List<String> localizedFields = getLocalizedFields(object);
        if(localizedFields == null){
            return AppLocalizedSpecification.getSimpleSpecification(parameters);
        }

        String localizedContentType = getContentType(object);
        if(!parameters.isEmpty()){
            if(languageCode != null){
                specification = AppLocalizedSpecification.getLocalizedSpecification(
                        parameters, languageCode, localizedContentType, localizedFields);
            }else{
                specification = AppLocalizedSpecification.getSimpleSpecification(parameters);
            }
        }
        return specification;
    }

    @Override
    public T saveLocalizedData(
            T object,R requestBodyObject,JpaRepository<T,Long> dbRepository) {
        object = dbRepository.save(object);
        Long objectId = getObjectId(object);
        String contentType = getContentType(object);
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
    public T updateLocalizedData(T object,R requestBodyObject,JpaRepository<T,Long> dbRepository) {
        deleteLocalizedData(object);
        return saveLocalizedData(object,requestBodyObject,dbRepository);
    }

    @Override
    public void deleteLocalizedData(T object) {
        String contentType = getContentType(object);
        List<LocalizedContents> localizedContents = localizationRepository.findAllByContentIdEqualsAndContentTypeEquals(
                getObjectId(object), contentType
        );
        if(!localizedContents.isEmpty()){
            localizationRepository.deleteAll(localizedContents);
        }
    }

    @Override
    public T getLocalizedData(
            T object, String languageCode) {
        List<String> localizedFields = getLocalizedFields(object);

        if(localizedFields == null)return object;
        String contentType = getContentType(object);

        return getLocalizedData(object,languageCode,contentType,localizedFields);
    }

    private T getLocalizedData(
            T object, String languageCode, String contentType, List<String> localizedFields) {
        for (String field : localizedFields) {
            LocalizedContents localizedContent = localizationRepository
                    .findTopByContentIdEqualsAndLanguageCodeEqualsAndContentTypeEqualsAndFieldNameEqualsOrderByIdDesc(
                            getObjectId(object), languageCode, contentType,field);
            if(localizedContent != null){
                setLocalizedField(object, field, localizedContent.getContent());
            }
        }
        return object;
    }

    @Override
    public List<T> getLocalizedData(
            List<T> objects, String languageCode) {
        List<String> localizedFields = null;
        String contentType = null;
        if(!objects.isEmpty()){
            T firstObject = objects.get(0);
            localizedFields = getLocalizedFields(firstObject);
            if(localizedFields == null)return objects;
            contentType = getContentType(firstObject);
        }else{
            return objects;
        }

        for (T object : objects) {
            getLocalizedData(object,languageCode,contentType,localizedFields);
        }
        return objects;
    }

    @Override
    public List<T> getQuickProcessedData(
            T object,Map<String, Object> parameters,JpaRepository<T,Long> dbRepository) {
        String languageCode = getLanguageCode(parameters);
        Specification<T> specification = getSpecification(object,parameters,languageCode);

        List<T> dataList;

        if(specification != null){
            dataList = ((JpaSpecificationExecutor<T>) dbRepository).findAll(specification);
        }else{
            dataList = dbRepository.findAll();
        }

        if(languageCode != null){
            return getLocalizedData(dataList,languageCode);
        }
        return dataList;
    }

    @Override
    public Page<T> getQuickProcessedPaginatedData(
            T object,Map<String, Object> parameters, Pageable pageable,JpaRepository<T,Long> dbRepository) {
        String languageCode = getLanguageCode(parameters);
        Specification<T> specification = getSpecification(object,parameters,languageCode);

        Page<T> pagedData;

        if(specification != null){
            pagedData = ((JpaSpecificationExecutor<T>) dbRepository).findAll(specification,pageable);
        }else{
            pagedData = dbRepository.findAll(pageable);
        }

        if(languageCode != null){
            return getLocalizedData(pagedData,languageCode);
        }
        return pagedData;
    }

    @Override
    public Page<T> getLocalizedData(
            Page<T> page, String languageCode) {
        List<T> content = page.getContent();
        content = getLocalizedData(content, languageCode);

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

    private String getContentType(T object){
        return object.getClass().getSimpleName();
    }

    private List<String> getLocalizedFields(T object){
        if(object == null)return null;
        try{
            Method method = object.getClass().getDeclaredMethod("availableLocalizedFields");
            method.setAccessible(true);
            return  (List<String>)method.invoke(object);
        }catch (Exception exception){
            return null;
        }
    }
}
