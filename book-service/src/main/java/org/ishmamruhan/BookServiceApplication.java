package org.ishmamruhan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
public class BookServiceApplication {
    @Autowired
    BookService bookService;

    @GetMapping("/api/book/all")
    public List<Book> getAllBooks(@RequestParam(required = false) String language){
        return bookService.getAllBooks(language);
    }

    @PostMapping("/api/save/book")
    public Book saveBook(@RequestBody BookRequest bookRequest){
        return bookService.saveBook(bookRequest);
    }



    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}