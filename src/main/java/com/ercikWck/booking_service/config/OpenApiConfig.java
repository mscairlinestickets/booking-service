package com.ercikWck.booking_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookingServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Serviço de Reservas de Passagens Aéreas")
                        .version("1.0")
                        .description("""
                                Microsserviço responsável pela gestão de reservas (bookings) de passagens aéreas.
                                
                                Ele orquestra a comunicação com o serviço de passagens (ticket-service), processa requisições de reserva, armazena o estado dos pedidos e publica eventos de confirmação ou rejeição. Também realiza integração assíncrona com serviços de pagamento, despacho e notificação via mensageria (RabbitMQ e Kafka).
                                
                                Projeto pessoal desenvolvido por Erick Silva, seguindo princípios de Clean Architecture, programação reativa com Spring WebFlux, e boas práticas de design distribuído.
                                """)
                        .contact(new Contact()
                                .name("Erick Silva")
                                .email("erickk.nsilva100@gmail.com")
                                .url("https://github.com/erickknsilva"))
                );
    }
}
