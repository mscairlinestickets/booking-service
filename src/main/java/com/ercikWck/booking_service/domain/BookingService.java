package com.ercikWck.booking_service.domain;

import com.ercikWck.booking_service.controller.dto.BookingRequestPayload;
import com.ercikWck.booking_service.repository.BookingRepository;
import com.ercikWck.booking_service.ticket.TicketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class BookingService {

    @Value(value = "${message.topic}")
    private String topic;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final TicketClient ticketClient;
    private final BookingRepository repository;
    private final ReactiveKafkaProducerTemplate<Long, CardDtoTransaction> reactiveKafka;

    public BookingService(TicketClient ticketClient, BookingRepository repository, ReactiveKafkaProducerTemplate<Long, CardDtoTransaction> reactiveKafka) {
        this.ticketClient = ticketClient;
        this.repository = repository;
        this.reactiveKafka = reactiveKafka;
    }

    @Transactional(transactionManager = "connectionFactoryTransactionManager")
    public Mono<Booking> submitOrder(BookingRequestPayload payload, CardDtoTransaction card) {

        return ticketClient.getBookingFlight(payload)
                .map(booking -> buildPendingBooking(booking, payload.quantity()))
                .defaultIfEmpty(buildRejectBookingOrder(payload.flightNumber(), payload.quantity()))
                .flatMap(repository::save);
//                .flatMap(savedBooking -> publishBookingAcceptedEventToKafka(savedBooking.bookingId(), card)
//                        .thenReturn(savedBooking));

    }

    public Booking buildPendingBooking(Booking booking, int quantity) {

        return Booking.createBooking(booking, quantity, BookingStatus.PENDING);
    }

    public Booking buildRejectBookingOrder(String flightNumber, int quantity) {
        return Booking.createRejectedBooking(flightNumber, quantity);
    }

    public Booking buildBookingApproved(Booking existBooking) {
        return existBooking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();
    }

    public Mono<Void> publishBookingAcceptedEventToKafka(Long bookingId, CardDtoTransaction cardTransaction) {
        return reactiveKafka.send(topic, bookingId, cardTransaction)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(100)).maxBackoff(Duration.ofSeconds(2)))
                .doOnSuccess(voidSenderResult -> log.info("Enviando ao Kafka: {}", voidSenderResult))
                .doOnError(error -> log.error("Erro ao enviar mensagem par ao kafka", error))
                .doOnSuccess(result -> log.info("Transação enviada com sucesso: {}", cardTransaction))
                .doOnNext(result -> {
                    var metadata = result.recordMetadata();
                    log.info("Mensagem enviada para partição {} com offset {}", metadata.partition(), metadata.offset());
                })
                .then();
    }

}
