package org.ishmamruhan;

import org.ishmamruhan.enums.LocalizedContentType;
import org.ishmamruhan.services.LocalizationUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LocalizationUtils<Book, BookRequest> localizationUtils;

    public BookService(BookRepository bookRepository, LocalizationUtils<Book, BookRequest> localizationUtils) {
        this.bookRepository = bookRepository;
        this.localizationUtils = localizationUtils;
    }

    @Cacheable("book_library")
    public Book saveBook(BookRequest bookRequest){
        Book book = new Book();
        book.setBookEIN(bookRequest.getBookEIN());
        Book savedBook = bookRepository.save(book);
        return localizationUtils.saveLocalizedData(
                savedBook,bookRequest, bookRepository,LocalizedContentType.BOOK);
    }

    @Cacheable("book_library")
    public List<Book> getAllBooks(String language){
        if(language == null){
            return bookRepository.findAll();
        }
        List<Book> books = bookRepository.findAll();
        return localizationUtils.getLocalizedData(books,language,LocalizedContentType.BOOK, new Book().availableLocalizedFields());
    }
}
