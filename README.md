
# Spring Boot Localization with Caching 



## Documentation


#### 1. Entity Level:

First, we have to implement an interface called 'Localization' provided by localization-service.

```bash
@Entity
public class Book extends AuditModel<String> implements Serializable, Localization {

    /**
     *  At Entity level, we have to implement an interface named 'Localization' which have a method that provides us
     *  localization field names
     */
```
and the ovrridden method:

```bash
    @JsonIgnore
    @Transient
    @Override
    public List<String> availableLocalizedFields(){
        return List.of("authorName","bookTitle","bookPublisher");
    }
```
As we want to localize authorName, bookTitle and bookPublisher,  we have to returns these field names. 

#### 2. Service Level:

In service level, first we have to inject localizationUtils as dependency.

```bash
    private final BookRepository bookRepository;
    private final LocalizationUtils<Book, BookRequest> localizationUtils;
    private final CacheConfig cacheConfig;

    public BookService(BookRepository bookRepository, 
                       LocalizationUtils<Book, BookRequest> localizationUtils, 
                       CacheConfig cacheConfig) {
        this.bookRepository = bookRepository;
        this.localizationUtils = localizationUtils;
        this.cacheConfig = cacheConfig;
    }
```

Here, we injected three dependencies- bookRepository, localizationUtils, and cacheconfig. As localizationUtils is generic type, it takes two Parameters One is our Entity and another one is BookRequest(Request body for create/update).

#### ****Create/Update book:* ***

Let's see how our request body should look like for localiztion:

```bash
    public class BookRequest {
        private Long id;
        private Map<String,String> _localization_authorName = new HashMap<>();
        private Map<String,String> _localization_bookTitle = new HashMap<>();
        private Map<String,String> _localization_bookPublisher = new HashMap<>();
        private String bookEIN;
    }
```
As we can see, we have added, ```_localization_``` before every field name that are localized. That's how our system can understand it's a localized field. We can change this ```_localization_``` identifier with our own custom one at LocalizedAppConstants.java file. We can found this setting in localiztion-service.

Now, while saving/updating book, 

    - We have to set all non-localized field value to main object.
    - save the main object.
    - send the saved main object through localizationUtils service with repository,

Example:

```
    public Book saveBook(BookRequest bookRequest){
        Book book = new Book();
        book.setBookEIN(bookRequest.getBookEIN());
        Book savedBook = bookRepository.save(book);
        refreshCache();
        return localizationUtils.saveLocalizedData(
                savedBook,bookRequest, bookRepository);
    }
```

while updating, LocalizationUtils has another method:

```
return localizationUtils.updateLocalizedData(
                savedBook,bookRequest, bookRepository);
```

#### ****Get Book(s):* ***

```bash
    /**
     *   Retrieve book object from database
     *   Send it to localizationUtils to get localized book object if language code provides.
     *      otherwise it'll return default language data
     *   ** Currently, systems default language is english.
     */
    T getLocalizedData(
            T object, String languageCode);

    /**
     *  If we need to localized list of books, we can use the following method
     *  Pass list of book objects and language code, it'll provide us same data with localized text
     */
    List<T> getLocalizedData(
            List<T> objects, String languageCode);

    /**
     *  Let's assume our Book entity have 15 properties. Nine of them are localized and the 
     *  rest of them are non-localized. We need list of books with total 7 request parameters
     *  that contains both localized and non-localized data, and it needs specific language support
     *  Then the following 'getQuickProcessedData' method will take care everything. Just pass all
     *  required parameters, and we'll get our desired result.
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
```

All examples can be found at book module's service class.

#### ****Delete Book:* ***

First, delete it from database then from localizationUtils.

```bash
    public void deleteBook(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if(book != null){
            bookRepository.delete(book);
            localizationUtils.deleteLocalizedData(book);
        }
    }

```

- **All code examples of consuming localizationUtil can be found at book-service module**

- **ER diagram of this project canbe found at projects main classpath**

- **Postman collection with demo requests can be found at projects main classpath**
## How to run this project

Initially this project is configured with postgresql database. If we use another database, we have to configure it accordingly. 

Find Database and other configurations from following:

- book-service/src/main/resources/application-development.yml

- localization-service/src/main/resources/application-development.yml

To run this project, Run the following file with Intellij-Idea/Eclipse/Spring-Tool-Suite(STS).

- book-service/src/main/java/org/ishmamruhan/BookServiceApplication.java



## How Caching Works In this Project
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

- app-configuration-service/src/main/java/org/ishmamruhan/cache_configuration

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

