package com.ercikWck.booking_service.domain;


import com.ercikWck.booking_service.repository.BookingRepository;
import com.ercikWck.booking_service.ticket.TicketClient;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
   private ReactiveKafkaProducerTemplate<Long, CardDtoTransaction> reactiveKafka;

    @Mock
    private TicketClient ticketClient;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Cria um booking order com o status PENDING.")
    void shouldReturnBuildPendingBooking() {
        //arrange
        int quantity = 3;
        Booking booking = createBookingTest();
        booking.toBuilder().status(BookingStatus.PENDING).build();

        //act
        var bookingPending = bookingService.buildPendingBooking(booking, quantity);

        //assert
        assertThat(bookingPending).isNotNull();
        assertEquals(BookingStatus.PENDING, bookingPending.status());
    }

    @Test
    @DisplayName("Rejeita um booking order com o status REJECTED.")
    void shouldReturnBuildRejectBookingOrder() {
        int quantity = 2;
        String flightNumber = "FL1234";
        Booking booking = createBookingTest();

        //act
        var bookingRejected = bookingService.buildRejectBookingOrder(flightNumber, quantity);

        //assert
        assertEquals(BookingStatus.REJECTED, bookingRejected.status());
        assertEquals(booking.flightNumber(), bookingRejected.flightNumber());
    }

    @Test
    @DisplayName("Força o status REJECTED mesmo que o Ticket tenha outro status")
    void shouldForceRejectedStatusRegardlessOfTicket() {
        int quantity = 1;
        String flightNumber = "FL1234";
        Booking booking = createBookingTest();
        //act
        var bookingRejected = bookingService.buildRejectBookingOrder(flightNumber, quantity);
        bookingRejected.toBuilder().status(BookingStatus.REJECTED).build();

        //asser
        assertEquals(BookingStatus.REJECTED, bookingRejected.status());
        assertEquals(booking.flightNumber(), bookingRejected.flightNumber());
    }

    @Test
    @DisplayName("Atualizar o status para APRROVED, caso o cartao seja validado.")
    void shouldReturnBuildBookingApproved() {
        //arrange
        Booking booking = createBookingTest();

        //act
        var bookingApproved = bookingService.buildBookingApproved(booking);

        //assert
        assertThat(bookingApproved).isNotNull();
        validationsFieldsBookingApproved(booking, bookingApproved);
    }

    @Test
    @DisplayName("Força o status REJECTED mesmo que o Ticket tenha outro status")
    void shouldForceRejectedStatusRegardlessOfBooking() {
        //arrange
        Booking booking = Booking.builder().status(BookingStatus.REJECTED).build();

        //act
        var bookingApproved = bookingService.buildBookingApproved(booking);

        //assert
        assertThat(bookingApproved).isNotNull();
        assertEquals(BookingStatus.APPROVED, bookingApproved.status());

    }

    @Test
    void deveEnviarMensagemKafkaComSucesso() {
        // ARRANGE
        ReflectionTestUtils.setField(bookingService, "topic", "test-topic");

        var bookingCreate = createBookingTest();
        var booking = Booking.createBooking(bookingCreate, 1, BookingStatus.PENDING)
                .toBuilder()
                .bookingId(1L)
                .build();

        when(ticketClient.getBookingFlight(anyString(), anyInt()))
                .thenReturn(Mono.just(booking));

        when(bookingRepository.save(any()))
                .thenReturn(Mono.just(booking));

        var card = CardDtoTransaction.builder()
                .paymentId(1L)
                .cardholderName("Test")
                .amount(BigDecimal.valueOf(99.90))
                .type("credit")
                .cardNumber("1234567812345678")
                .expiryDate("122030")
                .cvv("123")
                .build();

        var senderResult = mock(SenderResult.class);
        var recordMetadata = mock(RecordMetadata.class);

        when(recordMetadata.partition()).thenReturn(1);
        when(recordMetadata.offset()).thenReturn(42L);
        when(senderResult.recordMetadata()).thenReturn(recordMetadata);

        when(reactiveKafka.send(eq("test-topic"), eq(1L), any(CardDtoTransaction.class)))
                .thenReturn(Mono.just(senderResult));

        // ACT
        var resultado = bookingService.submitOrder("AA123", 1, card).block();

        // ASSERT
        assertNotNull(resultado);
        assertEquals("FL1234", resultado.flightNumber());

        verify(reactiveKafka, times(1))
                .send(eq("test-topic"), eq(1L), any(CardDtoTransaction.class));
    }


    private static CardDtoTransaction createCard() {
        return CardDtoTransaction.builder()
                .paymentId(1L)
                .cardholderName("João Silva")
                .amount(new BigDecimal("150.00"))
                .type("credito")
                .cardNumber("1234567812345678")
                .expiryDate("122026")
                .cvv("123")
                .build();
    }

    private static void validationsFieldsBooking(Booking bookingApproved, Booking booking, int quantity) {
        LocalDateTime expectedDeparture = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);

        assertNotNull(bookingApproved);
        assertEquals(booking.status(), bookingApproved.status());
        assertEquals(booking.flightNumber(), bookingApproved.flightNumber());
        assertEquals(booking.icaoCode(), bookingApproved.icaoCode());
        assertEquals(booking.aircraftModel(), bookingApproved.aircraftModel());
        assertEquals(quantity, bookingApproved.quantity());
        assertEquals(booking.origin(), bookingApproved.origin());
        assertEquals(booking.destination(), bookingApproved.destination());
        assertEquals(expectedDeparture.toString(), bookingApproved.departureDateTime().toString());
    }

    private static void validationsFieldsBookingApproved(Booking booking, Booking bookingApproved) {
        LocalDateTime expectedDeparture = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);

        assertNotNull(booking);
        assertEquals(booking.status(), bookingApproved.status());
        assertEquals(booking.flightNumber(), bookingApproved.flightNumber());
        assertEquals(booking.icaoCode(), bookingApproved.icaoCode());
        assertEquals(booking.aircraftModel(), bookingApproved.aircraftModel());
        assertEquals(booking.quantity(), bookingApproved.quantity());
        assertEquals(booking.origin(), bookingApproved.origin());
        assertEquals(booking.destination(), bookingApproved.destination());
        assertEquals(booking.version(), bookingApproved.version());
        assertEquals(expectedDeparture.toString(), bookingApproved.departureDateTime().toString());
    }

    private Booking createBookingTest() {
        return Booking.builder()
                .bookingId(1L)
                .flightNumber("FL1234")
                .origin("GRU")
                .destination("JFK")
                .departureDateTime(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .price(new BigDecimal("1200.50"))
                .quantity(1)
                .airlineName("Latam Airlines")
                .status(BookingStatus.APPROVED)
                .icaoCode("LAT")
                .aircraftModel("Boeing 787")
                .createdDate(Instant.now())
                .lastModifiedDate(Instant.now())
                .version(1)
                .build();
    }

}
