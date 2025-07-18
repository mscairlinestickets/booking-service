package com.ercikWck.booking_service.integration;

import com.ercikWck.booking_service.TestContainerKafkaConfig;
import com.ercikWck.booking_service.TestContainersPostgresConfiguration;
import com.ercikWck.booking_service.domain.BookingService;
import com.ercikWck.booking_service.domain.CardDtoTransaction;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import({TestContainerKafkaConfig.class, TestContainersPostgresConfiguration.class})
public class BookingServiceIntegrationTest {

    @Value("${message.topic}")
    private String topic;

    @Autowired
    private BookingService bookingService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Test
    void deveEnviarMensagemParaKafkaComSucesso() {
        Long bookingId = 123L;
        CardDtoTransaction card = buildCard();

        Mono<Void> resultado = bookingService.publishBookingAcceptedEventToKafka(bookingId, card);

        StepVerifier.create(resultado)
                .verifyComplete();
        verificarMensagemRecebida(bookingId, card);
    }


    // Cria o KafkaReceiver configurado para consumir do tópico
    private KafkaReceiver<Long, CardDtoTransaction> criarKafkaReceiver() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.deserializer", LongDeserializer.class.getName());
        props.put("value.deserializer", JsonDeserializer.class.getName());
        props.put("spring.json.trusted.packages", "*");
        props.put("group.id", UUID.randomUUID().toString());
        props.put("auto.offset.reset", "earliest");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CardDtoTransaction.class.getName());

        ReceiverOptions<Long, CardDtoTransaction> receiverOptions = ReceiverOptions.<Long, CardDtoTransaction>create(props)
                .subscription(Collections.singleton(topic));

        return KafkaReceiver.create(receiverOptions);
    }

    // Verifica se o Kafka recebeu e leu a mensagem corretamente
    private void verificarMensagemRecebida(Long bookingId, CardDtoTransaction esperado) {
        KafkaReceiver<Long, CardDtoTransaction> kafkaReceiver = criarKafkaReceiver();

        StepVerifier.create(
                        kafkaReceiver.receive()
                                .map(record -> record.value())
                                .filter(recebido -> recebido.paymentId().equals(esperado.paymentId()))
                                .take(1)
                )
                .expectNextMatches(recebido ->
                        recebido.amount().compareTo(esperado.amount()) == 0 &&
                                recebido.cardholderName().equals(esperado.cardholderName())
                )
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }

    private CardDtoTransaction buildCard() {
        return CardDtoTransaction.builder()
                .paymentId(1L)
                .cardholderName("Test")
                .amount(BigDecimal.valueOf(99.90))
                .type("credit")
                .cardNumber("1234567812345678")
                .expiryDate("122030")
                .cvv("123")
                .build();
    }


}
