package com.ercikWck.booking_service.ticket;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.Random.class)
public class TicketClientTest {

    private TicketClient ticketCLient;
    private MockWebServer mockWebServer;

    @BeforeEach
    void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        mockWebServer.start();

        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build();

    }
}
