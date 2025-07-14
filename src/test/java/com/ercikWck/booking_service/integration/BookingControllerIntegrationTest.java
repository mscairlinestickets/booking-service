package com.ercikWck.booking_service.integration;


import com.ercikWck.booking_service.TestContainerKafkaConfig;
import com.ercikWck.booking_service.TestContainersPostgresConfiguration;
import com.ercikWck.booking_service.WireMockContainerConfig;
import com.ercikWck.booking_service.controller.dto.BookingRequestPayload;
import com.ercikWck.booking_service.domain.Booking;
import com.ercikWck.booking_service.domain.BookingService;
import com.ercikWck.booking_service.domain.BookingStatus;
import com.ercikWck.booking_service.domain.CardDtoTransaction;
import com.ercikWck.booking_service.repository.BookingRepository;
import com.ercikWck.booking_service.ticket.TicketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestContainersPostgresConfiguration.class, TestContainerKafkaConfig.class})
public class BookingControllerIntegrationTest extends WireMockContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;


    @Autowired
    private BookingService bookingService;

    @Nested
    class SubmitOrderSuccessScenarios {

        @BeforeEach
        void setup() {
            WireMock.resetAllRequests();
            bookingRepository.deleteAll();
            setupArrangePostWireMock();
        }


        @Test
        @DisplayName("Deve criar reserva e chamar API externa com sucesso")
        void submitOrder() throws Exception {

            var card = buildCard();
            var request = new BookingRequestPayload("TK1933", 3, card);

            var result = webTestClient.post()
                    .uri("/bookings")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Booking.class).value(actual -> {
                        assertThat(actual).isNotNull();
                        assertThat(actual.status()).isEqualTo(BookingStatus.PENDING);
                    });

            WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/flights/TK1933/3")));

        }

        private static void setupArrangePostWireMock() {
            WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/flights/TK1933/3"))
                    .willReturn(WireMock.aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                      "flightNumber": "TK1933",
                                      "origin": "GRU",
                                      "destination": "JFK",
                                      "departureDateTime": "2025-08-12T18:30:00",
                                      "price": 1200.50,
                                      "quantity": 3,
                                      "airlineName": "Latam Airlines",
                                      "status": "PENDING",
                                      "icaoCode": "LAT",
                                      "aircraftModel": "Boeing 787"
                                    }
                                    """)));
        }

    }

    @Nested
    @DisplayName("Caso de falha onde o pedido nao foi aprovado")
    class SubmitOrderRejectedScenarios {
        @MockitoBean
        private TicketClient ticketClient;

        @BeforeEach
        void setup() {
            WireMock.resetAllRequests();
            bookingRepository.deleteAll();

        }

        @Test
        @DisplayName("Deve chamar a api e rejeitar o pedido.")
        void shouldRejectBookingWhenFlightNotFound() {
            String flightNumber = "TK9999";
            int quantity = 3;

            CardDtoTransaction card = buildCard();

            // Simula ausência de voo (não encontrado)
            given(ticketClient.getBookingFlight(flightNumber, quantity))
                    .willReturn(Mono.empty());

            webTestClient.post()
                    .uri("/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new BookingRequestPayload(flightNumber, quantity, card))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.flightNumber").isEqualTo(flightNumber)
                    .jsonPath("$.quantity").isEqualTo(quantity)
                    .jsonPath("$.status").isEqualTo("REJECTED");
        }

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
