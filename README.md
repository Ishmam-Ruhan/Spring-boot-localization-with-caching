
# Spring Boot Localization with Caching 



## Documentation




## Features

- Multi-language/Localization implementation
- Implemented caching mechanism(Caffeine)
- Multi-module spring boot application


## How to run this project

Initially this project is configured with postgresql database. If we use another database, we have to configure it accordingly. 

Find Database and other configurations from following:

book-service/src/main/resources/application-development.yml

localization-service/src/main/resources/application-development.yml

To run this project, Run the following file with Intellij-Idea/Eclipse/Spring-Tool-Suite(STS).

book-service/src/main/java/org/ishmamruhan/BookServiceApplication.java


## Change Required: Request Body Structure Level

Import the postman-collection from root directory. Now, if you can see our request body structure:

### For Localized Fields:
For every fileds that we need to localized, we have to introduce a constant called:

```bash
  _localization_fieldName
```
That's how, we can identify, the fields need localization. Each of this field containa a object(ke-value format), where key is the language code and value is the localized or translated string.

### For Non-Localized Fields:
If we don't want a field as localized, we simply add as its own format.


## Change Required: Entity Level
we have to add one extra method(Use @JsonIgnore) at our entity that provides localized field names as String array.

## How To Use It In Service Level
Firstly, we can adopt it as different module or single module. For re-use this module, just copy the module and paste it at your desired one.

Now, What we have to modify our service layer?

@Autowire or inject the dependency to our service. Now,

Step-1: Perform all operations as usual but EXCEPT localized fields. Do Not modify localized fields. Save entity using repository and pass the saved object, your repository object, your request body object, localization-Type(simple enum that indicated your feature name) to LocalizedUtil service. This service will responsible rest of the work. You'll get return back with an updated object.

```bash
      public Book saveBook(BookRequest bookRequest){
        Book book = new Book();
        book.setBookEIN(bookRequest.getBookEIN());
        Book savedBook = bookRepository.save(book);
        refreshCache();
        return localizationUtils.saveLocalizedData(
                savedBook,bookRequest, bookRepository, LocalizedContentType.BOOK);
    }
```

## How Caching work
We've used Caffeine Caching in this project.

Step-1: Add dependency

```bash
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
        </dependency>

```

Step-1: Add Configurations to properties:

```bash
spring:
  cache:
      cache-names: book_library  //use cache name as your own, but make sure change the name in CacheConstant file too.
      caffeine:
        spec: maximumSize=200,expireAfterWrite=900s  // maximum usages 200mb of Ram and it'll evicted  all data after 15 minutes.

```
Again, We can find cache configurations inside,

app-configuration-service/src/main/java/org/ishmamruhan/cache_configuration

Every save/update operation we have to clear previous caches by providing method name and class:

```bash
    private void refreshCache(){
        /*
         * We have to provide the name of the functions from where we want to update the cache
         * if any changes happens.
         */
        cacheConfig.clearCache(this.getClass(),"getAllBooks");  //Here, when adding/updating/removing any book, we delete caches that stores from 'getAllBooks' method
    }
```

