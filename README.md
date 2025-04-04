# ASID-MS

# 1. Para os containers
docker-compose down

# 2. Remove a imagem antiga do book-service
docker rmi asid-ms-book-service:latest

# 3. Rebuild da imagem sem cache
docker-compose build --no-cache

# 4. Sobe os containers novamente
docker-compose up



#BASE DE DADOS



-- ORDER-SERVICE
DELETE FROM order_details;
DELETE FROM orders;

-- USER-SERVICE
DELETE FROM users;

-- BOOK-SERVICE
DELETE FROM book;
DELETE FROM author;
DELETE FROM subcategory;
DELETE FROM category;



-- BOOK-SERVICE

-- Categorias
INSERT INTO category (categoryid, name) VALUES (1, 'Ficção'), (2, 'Tecnologia');

-- Subcategorias
INSERT INTO subcategory (subcategoryid, name, category_id) VALUES 
(1, 'Fantasia', 1),
(2, 'Programação', 2);

-- Autores
INSERT INTO author (authorid, author_name) VALUES 
(1, 'J.K. Rowling'),
(2, 'Robert C. Martin');

-- Livros
INSERT INTO book (bookid, title, description, price, isbn_number, quantity, author_id, category_id, subcategory_id)
VALUES 
(1, 'Harry Potter e a Pedra Filosofal', 'Magia e aventura.', 29.99, '978-1234567890', 10, 1, 1, 1),
(2, 'Clean Code', 'Um manual de boas práticas.', 39.99, '978-0132350884', 5, 2, 2, 2);



-- USER-SERVICE

-- Utilizadores
INSERT INTO users (id, fullname, username, email, password)
VALUES 
(1, 'Tiago Silva', 'tiago', 'tiago@email.com', '1234'),
(2, 'Maria Costa', 'maria', 'maria@email.com', 'abcd');


-- ORDER-SERVICE

-- Encomendas
INSERT INTO orders (orderid, order_date, total_price, shippingorder_id, user_id)
VALUES 
(1, NOW(), 49.99, 1, 1);

-- Detalhes de Encomenda
INSERT INTO order_details (order_detailsid, quantity, sub_total, book_id, order_id)
VALUES 
(1, 1, 29.99, 1, 1),
(2, 1, 20.00, 2, 1);
