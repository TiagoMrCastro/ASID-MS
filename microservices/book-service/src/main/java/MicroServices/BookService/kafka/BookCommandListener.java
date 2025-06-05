package MicroServices.BookService.kafka;

import MicroServices.BookService.entity.Book;
import MicroServices.BookService.repository.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookCommandListener {

    private final BookRepository bookRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "book.reserve.command", groupId = "book-service")
    public void handleReserveBooks(String message) {
        try {
            log.info("📩 Evento recebido: book.reserve.command");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

            // 1. Validar stock de todos os livros antes de alterar qualquer um
            for (Map<String, Object> item : items) {
                Long bookId = Long.valueOf(item.get("bookId").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());

                Book book = bookRepo.findById(bookId)
                        .orElseThrow(() -> new RuntimeException("Livro não encontrado: " + bookId));

                if (book.getQuantity() < quantity) {
                    sendFailureEvent(orderId, "❌ Stock insuficiente para o livro " + bookId);
                    return;
                }
            }

            // 2. Como todos têm stock, atualizar todos os livros
            for (Map<String, Object> item : items) {
                Long bookId = Long.valueOf(item.get("bookId").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());

                Book book = bookRepo.findById(bookId).get();
                book.setQuantity(book.getQuantity() - quantity);
                bookRepo.save(book);
            }

            // 3. Preparar resposta com possível info de shipping
            Object shipping = data.get("shipping");

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("message", "✅ Reserva efetuada com sucesso");

            if (shipping != null) {
                response.put("shipping", shipping);
            }

            String payload = objectMapper.writeValueAsString(response);
            kafkaTemplate.send("book.reserved.event", orderId.toString(), payload);
            log.info("📤 Evento publicado: book.reserved.event");
            log.info("✅ Reserva de encomenda concluída {}", orderId);

        } catch (Exception e) {
            log.error("❌ Erro ao processar book.reserve.command", e);
        }
    }

    private void sendFailureEvent(Long orderId, String message) {
        try {
            var payload = objectMapper.writeValueAsString(Map.of(
                    "orderId", orderId,
                    "message", message
            ));
            kafkaTemplate.send("book.reservation.failed.event", orderId.toString(), payload);
            log.info("📤 Evento publicado: book.reservation.failed.event");
        } catch (Exception e) {
            log.error("Erro ao enviar evento de falha para Kafka", e);
        }
    }
    
    @Transactional
    @KafkaListener(topics = "book.rollback.command", groupId = "book-service")
    public void handleRollback(String message) {
        try {
            log.info("📩 Evento recebido: book.rollback.command");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

            for (Map<String, Object> item : items) {
                Long bookId = Long.valueOf(item.get("bookId").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());

                Book book = bookRepo.findById(bookId)
                        .orElseThrow(() -> new RuntimeException("Livro não encontrado: " + bookId));

                int before = book.getQuantity();
                book.setQuantity(before + quantity);
                bookRepo.save(book);

                log.info("🔁 Livro ID {}: quantidade {} ➜ {}", bookId, before, book.getQuantity());
            }

            log.info("↩️ Rollback concluído: todas as quantidades restauradas com sucesso.");

        } catch (Exception e) {
            log.error("❌ Erro ao processar book.rollback.command", e);
        }
    }

    
}
