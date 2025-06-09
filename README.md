# ASID-MS
mvn clean package -DskipTests
docker compose up --build

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
(2, 'Programação', 2),
(3, 'Épico', 1),
(4, 'Suspense', 1),
(5, 'Frameworks', 2),
(6, 'Drama Histórico', 1);

-- Autores
INSERT INTO author (authorid, author_name) VALUES 
(1, 'J.K. Rowling'),
(2, 'Robert C. Martin'),
(3, 'J.R.R. Tolkien'),
(4, 'Erich Gamma'),
(5, 'George R.R. Martin'),
(6, 'Dan Brown'),
(7, 'João Angularino'),
(8, 'Markus Zusak'),
(9, 'Stoyan Stefanov');

-- Livros
INSERT INTO book (bookid, title, description, price, isbn_number, quantity, author_id, category_id, subcategory_id)
VALUES 
(1, 'Harry Potter e a Pedra Filosofal', 'Magia e aventura.', 29.99, '978-1234567890', 10, 1, 1, 1),
(2, 'Clean Code', 'Um manual de boas práticas.', 39.99, '978-0132350884', 5, 2, 2, 2),
(3, 'O Senhor dos Anéis: A Sociedade do Anel', 'A primeira parte da lendária trilogia.', 49.90, '978-0261103573', 8, 3, 1, 3),
(4, 'Design Patterns', 'Elementos de software orientado a objetos reutilizáveis.', 45.50, '978-0201633610', 4, 4, 2, 2),
(5, 'A Guerra dos Tronos', 'O início da saga épica de Westeros.', 34.99, '978-0553103540', 7, 5, 1, 3),
(6, 'Refactoring', 'Melhoria do design de código existente.', 41.00, '978-0201485677', 6, 2, 2, 2),
(7, 'O Código Da Vinci', 'Mistério e simbologia com Robert Langdon.', 26.90, '978-0385504201', 12, 6, 1, 4),
(8, 'Angular para Desenvolvedores', 'Domine o framework moderno de front-end.', 37.90, '978-8575227890', 10, 7, 2, 5),
(9, 'A Menina que Roubava Livros', 'Uma história comovente narrada pela Morte.', 29.00, '978-0375842207', 9, 8, 1, 6),
(10, 'Padrões JavaScript', 'Boas práticas e padrões no JS.', 31.50, '978-0596806750', 5, 9, 2, 2);



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


-- Carts para os utilizadores 1 e 2
INSERT INTO cart (cartid, created_date, user_id) VALUES 
(1, CURRENT_DATE(), 1),
(2, CURRENT_DATE(), 2);

-- Utilizador 1: adiciona 2x 'Clean Code' (bookid = 2, preço 39.99)
-- Utilizador 2: adiciona 1x 'Harry Potter' (bookid = 1, preço 29.99)

INSERT INTO cart_item (id, quantity, unit_price, sub_total, book_id, user_id, cart_id) VALUES 
(1, 2, 39.99, 79.98, 2, 1, 1),
(2, 1, 29.99, 29.99, 1, 2, 2);
