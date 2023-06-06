package org.ishmamruhan.services;

import org.ishmamruhan.cache_configuration.configurations.CacheConfig;
import org.ishmamruhan.entities.Book;
import org.ishmamruhan.repository.BookRepository;
import org.ishmamruhan.requests.BookRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ishmamruhan.cache_configuration.constants.CacheConstants.CACHE_KEY_GENERATOR;
import static org.ishmamruhan.cache_configuration.constants.CacheConstants.CACHE_NAME;

@Service
public class BookService {
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

    public Book saveBook(BookRequest bookRequest){
        Book book = new Book();
        book.setBookEIN(bookRequest.getBookEIN());
        Book savedBook = bookRepository.save(book);
        refreshCache();
        return localizationUtils.saveLocalizedData(
                savedBook,bookRequest, bookRepository);
    }

    public Book updateBook(BookRequest bookRequest){
        /**
         * While updating existing book, we just set non-localized data with main book entity from book request
         * After that, we have to save this book and pass the saved book to localizationUtils. It will set
         * all localized fields, set defaults to main book fields and return us the updated book object
         */
        Book book = bookRepository.findById(bookRequest.getId()).orElseThrow(() -> new RuntimeException("Book not found!"));
        book.setBookEIN(bookRequest.getBookEIN());
        Book savedBook = bookRepository.save(book);
        refreshCache();
        return localizationUtils.updateLocalizedData(
                savedBook,bookRequest, bookRepository);
    }

    /**
     * Demo for quick processed data:
     * Just pass a blank object, request parameters and repository -> localization utils will
     * responsible to do entire process that we have done in "getAllBooks" method(immediate next method).
     *
     * localizationUtils also has a method called "getQuickProcessedPaginatedData" which takes extra
     * one parameter called pageable, it will then return paginated data
     *
     * You can do explicitly every operation of your own like "getAllBooks" method(immediate next method) of Book Service.
     * or you can give the responsibility to "localizationUtils".
     */
    @Cacheable(value = CACHE_NAME,keyGenerator = CACHE_KEY_GENERATOR)
    public List<Book> getAllBooksQuick(Map<String, Object> parameters){
        return localizationUtils.getQuickProcessedData(new Book(),parameters,bookRepository);
    }

    @Cacheable(value = CACHE_NAME,keyGenerator = CACHE_KEY_GENERATOR)
    public List<Book> getAllBooks(Map<String, Object> parameters){
        /**
         *  Step-1 : Find language code from parameters
         */
        String languageCode = localizationUtils.getLanguageCode(parameters);

        /**
         *  Step-2 : If you need specification, use localizationUtils to get specification
         */
        Specification<Book> specification = localizationUtils.getSpecification(new Book(),parameters,languageCode);

        /**
         *  Step-3 :  Now, find books using previous specification. I didn't use pageable here, we can use it if needed
         */
        List<Book> bookList;

        if(specification != null){
            bookList = bookRepository.findAll(specification);
        }else{
            bookList = bookRepository.findAll();
        }

        /**
         *  Step-4 :  Finally, if language code is available(not null in this case), then return localized data
         *  localizationUtils.getLocalizedData() method accepts both normal list and paged data and it returns same.
         */
        if(languageCode != null){
            return localizationUtils.getLocalizedData(bookList,languageCode);
        }
        return bookList;
    }

    public void deleteBook(Long id){
        /**
         *  Delete book from book table as well as localization table
         */
        Book book = bookRepository.findById(id).orElse(null);
        if(book != null){
            bookRepository.delete(book);
            localizationUtils.deleteLocalizedData(book);
        }
    }

    private void refreshCache(){
        /**
         * We have to provide the name of the functions from where we want to update the cache
         * if any changes happens.
         */
        cacheConfig.clearCache(this.getClass(),"getAllBooks");
    }
}
