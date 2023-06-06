package org.ishmamruhan.controllers;

import org.ishmamruhan.entities.Book;
import org.ishmamruhan.requests.BookRequest;
import org.ishmamruhan.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @GetMapping("/all")
    public List<Book> getAllBooksPaginated(
          @RequestParam  Map<String, Object> parameters
    ){
        return bookService.getAllBooksQuick(parameters);
    }

    @PostMapping("/save")
    public Book saveBook(@RequestBody BookRequest bookRequest){
        return bookService.saveBook(bookRequest);
    }

    @PutMapping("/update")
    public Book updateBook(@RequestBody BookRequest bookRequest){
        return bookService.updateBook(bookRequest);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return "Book deleted successfully!";
    }

}
