package MicroServices.ShippingService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import MicroServices.ShippingService.entity.Book;
import MicroServices.ShippingService.service.BookService;

/**
 * Controller para manipular as requisições relacionadas aos livros.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookController {

    @Autowired
    private BookService bookSerivce;

    /**
     * Endpoint para obter todos os livros.
     * @return ResponseEntity contendo a lista de todos os livros e o status HTTP OK.
     */
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks(){
        List<Book> books = bookSerivce.getAllBook();

        return new ResponseEntity<>(books,HttpStatus.OK);
    }

    /**
     * Endpoint para obter um livro pelo seu ID.
     * @param id O ID do livro a ser recuperado.
     * @return ResponseEntity contendo o livro e o status HTTP OK se encontrado, caso contrário, status HTTP NOT FOUND.
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){

        Book existBook = bookSerivce.getBookById(id);

        if(existBook !=null){
            return new ResponseEntity<>(existBook,HttpStatus.OK);

        } else{

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para obter livros pelo ID da categoria.
     * @param id O ID da categoria para recuperar os livros.
     * @return ResponseEntity contendo a lista de livros na categoria e o status HTTP OK se encontrado, caso contrário, status HTTP NOT FOUND.
     */
    @GetMapping("/books/category/{id}")
    public ResponseEntity<List<Book>> getBooksByCategoryID(@PathVariable Long id) {

        List<Book> existBook = bookSerivce.getBooksByCategoryID(id);

         if(existBook !=null){

            return new ResponseEntity<>(existBook,HttpStatus.OK);

        } else{

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para atualizar a quantidade de um livro.
     * @param id O ID do livro a ser atualizado.
     * @param book O objeto livro contendo a nova quantidade.
     * @return ResponseEntity contendo o livro atualizado e o status HTTP OK.
     */
    @PatchMapping("/updatequantity/{id}")
    public ResponseEntity<Book> patchQuantity(@PathVariable Long id , @RequestBody Book book){

        Book updatedBookQuantity = bookSerivce.patchBookQuantity(id,book);

        return new ResponseEntity<>(updatedBookQuantity,HttpStatus.OK);
    }
}
