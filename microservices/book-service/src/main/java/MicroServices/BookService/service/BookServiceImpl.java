package MicroServices.BookService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import MicroServices.BookService.entity.Book;
import MicroServices.BookService.repository.BookRepository;


@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;

    

    @Override
    public List<Book> getAllBook(){

        return bookRepository.findAll();
    }


    @Override
    public Book getBookById(Long id){


        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public List<Book> getBooksByCategoryID(Long id){

        return bookRepository.findByCategoryId(id);
           
            
    }

    @Override
    public List<Book> searchBooks(String query) {
        return bookRepository.searchBooks(query);
    }
       
    @Override
    public Book patchBookQuantity(Long id, Book book){

        Book existBook = bookRepository.findById(id).orElse(null);

        if (existBook != null) {
            
            existBook.setQuantity(book.getQuantity());
            bookRepository.save(existBook);
    
            return existBook;
        } else { 
            return null;
        }
    }

    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Book existingBook = bookRepository.findById(id).orElse(null);
        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setIsbnNumber(book.getIsbnNumber());
            existingBook.setDescription(book.getDescription());
            existingBook.setPrice(book.getPrice());
            existingBook.setQuantity(book.getQuantity());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setCategory(book.getCategory());
            existingBook.setSubcategory(book.getSubcategory());
            existingBook.setImage(book.getImage());
            return bookRepository.save(existingBook);
        }
        return null;
    }

}
