package MicroServices.ShippingService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import MicroServices.ShippingService.entity.Book;

@Service
public interface BookService {
    List<Book> getAllBook();
    Book getBookById(Long id);
    List<Book> getBooksByCategoryID(Long id);
    List<Book> searchBooks(String query);
    Book patchBookQuantity(Long id, Book book);
    
}
