package com.ercikWck.booking_service.integration;


import com.ercikWck.booking_service.TestContainerKafkaConfig;
import com.ercikWck.booking_service.TestContainersPostgresConfiguration;
import com.ercikWck.booking_service.WireMockContainerConfig;
import com.ercikWck.booking_service.controller.dto.BookingRequestPayload;
import com.ercikWck.booking_service.domain.*;
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

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
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

            var paymentDtoTransaction = buildpaymentDtoTransaction();
            var request = new BookingRequestPayload("TK1933", 3, PaymentType.CREDITO, paymentDtoTransaction);

            var result = webTestClient.post()
                    .uri("/bookings")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Booking.class).value(actual -> {
                        assertThat(actual).isNotNull();
                        assertThat(actual.status()).isEqualTo(BookingStatus.PENDING);
                    })
                    .consumeWith(System.out::println);

            WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/api/flights/orderBooking"))
                    .withRequestBody(matchingJsonPath("$.flightNumber", equalTo("TK1933")))
                    .withRequestBody(matchingJsonPath("$.quantity", equalTo("3")))
                    .withRequestBody(matchingJsonPath("$.paymentTransaction.cardholderName", equalTo("João da Silva"))));
        }

        private static void setupArrangePostWireMock() {
            WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/flights/orderBooking"))
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

            PaymentDtoTransaction paymentDtoTransaction = buildpaymentDtoTransaction();
            BookingRequestPayload payload = new BookingRequestPayload(flightNumber, quantity, PaymentType.BOLETO, paymentDtoTransaction);
            // Simula ausência de voo (não encontrado)
            given(ticketClient.getBookingFlight(payload))
                    .willReturn(Mono.empty());

            webTestClient.post()
                    .uri("/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new BookingRequestPayload(flightNumber, quantity, PaymentType.PIX, paymentDtoTransaction))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.flightNumber").isEqualTo(flightNumber)
                    .jsonPath("$.quantity").isEqualTo(quantity)
                    .jsonPath("$.status").isEqualTo("REJECTED");
        }

    }

    private PaymentDtoTransaction buildpaymentDtoTransaction() {
        return PaymentDtoTransaction.builder()
                .name("Test")
                .cardholderName("João da Silva")
                .amount(BigDecimal.valueOf(99.90))
                .type("CREDITO")
                .cardNumber("1234567812345678")
                .expiryDate("122030")
                .cvv("123")
                .build();
    }

}
