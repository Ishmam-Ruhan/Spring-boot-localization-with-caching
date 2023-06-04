package org.ishmamruhan.controllers;

import org.ishmamruhan.entities.Book;
import org.ishmamruhan.requests.BookRequest;
import org.ishmamruhan.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public List<Book> getAllBooks(@RequestParam(required = false) String language){
        return bookService.getAllBooks(language);
    }

    @PostMapping("/save")
    public Book saveBook(@RequestBody BookRequest bookRequest){
        return bookService.saveBook(bookRequest);
    }

    @PutMapping("/update")
    public Book updateBook(@RequestBody BookRequest bookRequest){
        return bookService.updateBook(bookRequest);
    }
}
