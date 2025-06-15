package MicroServices.BookService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import MicroServices.BookService.entity.Book;

@Service
public interface BookService {
    List<Book> getAllBook();
    Book getBookById(Long id);
    List<Book> getBooksByCategoryID(Long id);
    List<Book> searchBooks(String query);
    Book patchBookQuantity(Long id, Book book);
    Book addBook(Book book);
    Book updateBook(Long id, Book book);

    
}
