package com.ercikWck.booking_service.domain;

import com.ercikWck.booking_service.repository.BookingRepository;
import com.ercikWck.booking_service.ticket.TicketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<Booking> submitOrder(String flightNumber, Integer quantity, CardDtoTransaction card) {

        return ticketClient.getBookingFlight(flightNumber, quantity)
                .map(booking -> buildPendingBooking(booking, quantity))
                .defaultIfEmpty(buildRejectBookingOrder(flightNumber, quantity))
                .flatMap(repository::save)
                .flatMap(savedBooking -> publishBookingAcceptedEventToKafka(savedBooking.bookingId(), card).thenReturn(savedBooking));

    }

    public Booking buildPendingBooking(Booking booking, int quantity) {

        return Booking.createBooking(booking, quantity, BookingStatus.PENDING);
    }

    public Booking buildRejectBookingOrder(String booking, int quantity) {
        return Booking.createRejectedBooking(booking, quantity);
    }

    public Booking buildBookingApproved(Booking existBooking) {
        return existBooking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Retryable(retryFor = QueryTimeoutException.class, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 1.1))
    public Mono<Void> publishBookingAcceptedEventToKafka(Long bookingId, CardDtoTransaction cardTransaction) {
        return reactiveKafka.send(topic, bookingId, cardTransaction)
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
