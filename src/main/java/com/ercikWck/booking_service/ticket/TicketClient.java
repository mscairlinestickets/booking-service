package com.ercikWck.booking_service.ticket;

import com.ercikWck.booking_service.controller.dto.BookingRequestPayload;
import com.ercikWck.booking_service.domain.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Component
public class TicketClient {

    private static final String API_FLIGHTS_ROOT = "/api/flights/orderBooking";
    private final WebClient webClient;

    public TicketClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public Mono<Booking> getBookingFlight(BookingRequestPayload payload) {


        return webClient
                .post()
                .uri(String.format(API_FLIGHTS_ROOT))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Booking.class)
                .timeout(Duration.ofSeconds(3), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorResume(Exception.class, ex -> {
                    log.warn("Erro ao consultar ticket-service para {}: {}", payload.flightNumber(), ex.getMessage());
                    return Mono.empty();
                });
    }

}
