package com.ercikWck.booking_service.controller;

import com.ercikWck.booking_service.controller.dto.BookingRequestPayload;
import com.ercikWck.booking_service.domain.Booking;
import com.ercikWck.booking_service.domain.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Listar todas as reservas", description = "Retorna uma lista com todas as reservas cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Booking.class)))
    })
    @GetMapping
    public Flux<Booking> findAll() {
        return null;
    }

    @Operation(summary = "Buscar reserva por ID", description = "Retorna os dados de uma reserva específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Booking>> findById(@PathVariable Long id) {
        return null;
    }

    @Operation(summary = "Criar nova reserva", description = "Cria uma nova reserva com os dados do voo e do pagamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou incompletos",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Booking> createBooking(@RequestBody @Valid BookingRequestPayload payload) {
        return bookingService.submitOrder(payload, payload.card());
    }

}
