import com.ercikWck.booking_service.domain.BookingService;
import com.ercikWck.booking_service.domain.CardDtoTransaction;
import com.ercikWck.booking_service.repository.BookingRepository;
import com.ercikWck.booking_service.ticket.TicketClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderOptions;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class bookingServiceIntegrationTest {

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void setupProps(DynamicPropertyRegistry registry) {
        kafka.start();
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("message.topic", () -> "test-topic");
    }

    @Value("${message.topic}")
    private String topic;

    @Autowired
    private KafkaProperties kafkaProperties; // ✅ injeta como campo

    private BookingService bookingService;

    @BeforeEach
    void setup() {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();

        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");

        SenderOptions<Long, CardDtoTransaction> senderOptions = SenderOptions.create(props);
        ReactiveKafkaProducerTemplate<Long, CardDtoTransaction> reactiveKafkaProducer =
                new ReactiveKafkaProducerTemplate<>(senderOptions);

        var mockRepository = mock(BookingRepository.class);
        var mockTicketClient = mock(TicketClient.class);

        bookingService = new BookingService(mockTicketClient, mockRepository, reactiveKafkaProducer);
    }

    @Test
    void deveEnviarMensagemParaKafkaComSucesso() {
        Long bookingId = 123L;
        CardDtoTransaction card = CardDtoTransaction.builder()
                .paymentId(1L)
                .cardholderName("Test")
                .amount(BigDecimal.valueOf(99.90))
                .type("credit")
                .cardNumber("1234567812345678")
                .expiryDate("122030")
                .cvv("123")
                .build();

        Mono<Void> resultado = bookingService.publishBookingAcceptedEventToKafka(bookingId, card);

        StepVerifier.create(resultado)
                .verifyComplete(); // Verifica que o Mono foi concluído sem erro
    }
}
