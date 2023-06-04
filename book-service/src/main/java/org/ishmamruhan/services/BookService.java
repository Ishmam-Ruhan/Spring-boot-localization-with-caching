package org.ishmamruhan.services;

import org.ishmamruhan.constants.LocalizedContentType;
import org.ishmamruhan.cache_configuration.configurations.CacheConfig;
import org.ishmamruhan.entities.Book;
import org.ishmamruhan.repository.BookRepository;
import org.ishmamruhan.requests.BookRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.ishmamruhan.cache_configuration.constants.CacheConstants.CACHE_KEY_GENERATOR;
import static org.ishmamruhan.cache_configuration.constants.CacheConstants.CACHE_NAME;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LocalizationUtils<Book, BookRequest> localizationUtils;

    private final CacheConfig cacheConfig;

    public BookService(BookRepository bookRepository, LocalizationUtils<Book, BookRequest> localizationUtils, CacheConfig cacheConfig) {
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
                savedBook,bookRequest, bookRepository, LocalizedContentType.BOOK);
    }

    public Book updateBook(BookRequest bookRequest){
        Book book = bookRepository.findById(bookRequest.getId()).orElseThrow(() -> new RuntimeException("Book not found!"));
        book.setBookEIN(bookRequest.getBookEIN());
        Book savedBook = bookRepository.save(book);
        refreshCache();
        return localizationUtils.updateLocalizedData(
                savedBook,bookRequest, bookRepository, LocalizedContentType.BOOK);
    }

    @Cacheable(value = CACHE_NAME,keyGenerator = CACHE_KEY_GENERATOR)
    public List<Book> getAllBooks(String language){
        if(language == null){
            return bookRepository.findAll();
        }
        List<Book> books = bookRepository.findAll();
        return localizationUtils.getLocalizedData(books,language,LocalizedContentType.BOOK, new Book().availableLocalizedFields());
    }

    private void refreshCache(){
        /*
         * We have to provide the name of the functions from where we want to update the cache
         * if any changes happens.
         */
        cacheConfig.clearCache(this.getClass(),"getAllBooks");
    }
}
