[![Build Status](https://github.com/mscairlinestickets/booking-service/actions/workflows/commit-stage.yml/badge.svg)](https://github.com/mscairlinestickets/booking-service/actions/workflows/commit-stage.yml)
![License](https://img.shields.io/github/license/mscairlinestickets/booking-service)
![Coverage](https://img.shields.io/badge/coverage-85%25-green)


# ✈️ booking-service

O **booking-service** é um microsserviço responsável por processar **reservas de passagens aéreas**. Ele se comunica com o serviço externo `ticket-service` para validar e efetuar as reservas.

O projeto é construído com **Spring WebFlux** e segue um modelo **reativo e não bloqueante**, utilizando **Kafka**, **PostgreSQL com R2DBC**, **auditoria JDBC**, testes automatizados (unitários e de integração), além de **CI/CD** e documentação via **Springdoc OpenAPI**.

---

## 🧪 Estratégia de Desenvolvimento

O projeto foi desenvolvido utilizando a abordagem Code First, onde o código Java é implementado primeiro e, em seguida, o contrato OpenAPI é gerado automaticamente utilizando ferramentas como o Springdoc e exportado via curl. O desenvolvimento é guiado por testes (TDD), garantindo confiança no código e qualidade nas entregas.

Benefícios dessa abordagem

- Clareza e controle total sobre a implementação da API
- Geração automática e consistente da documentação OpenAPI
- Facilidade para versionamento e rastreamento da evolução dos endpoints
- Integração contínua com Swagger para visualização e exportação do contrato

  💡 Embora a documentação esteja disponível como se fosse "API First", o modelo seguido aqui é Code First com exportação posterior do contrato.
---

## 🔧 Tecnologias utilizadas

* **Java 21**
* **Spring Boot 3.5.3**
* **Spring WebFlux**
* **Spring Data R2DBC**
* **Reactive Kafka (reactor-kafka)**
* **Flyway** para versionamento de banco
* **Testcontainers** (PostgreSQL, Kafka, WireMock)
* **Springdoc OpenAPI** (documentação automática)
* **CI/CD** via pipelines com `Gradle`
* **Docker** (build de imagem com `bootBuildImage`)

---

## 📦 Funcionalidades principais

* Criar reservas de passagens aéreas com validação de disponibilidade no `ticket-service`
* Comunicação reativa com serviço externo via `WebClient`
* Publicação de eventos Kafka para transações de cartão
* Repositório reativo com R2DBC e transações com `connectionFactoryTransactionManager`
* Retry automático em chamadas externas e envio de eventos Kafka
* Documentação OpenAPI disponível em `/docs`

---

## 📁 Estrutura do projeto

| Camada     | Pacote           | Responsabilidade                          |
| ---------- | ---------------- | ----------------------------------------- |
| Controller | `controller`     | Endpoints HTTP REST (`/bookings`)         |
| Domain     | `domain`         | Lógica de negócio e modelo de domínio     |
| Gateway    | `ticket`         | Cliente WebClient para `ticket-service`   |
| Mensageria | `events`         | Configuração e envio de mensagens Kafka   |
| Repository | `repository`     | Acesso a dados via R2DBC                  |
| DTOs       | `controller.dto` | Representações de entrada para o endpoint |

```bash
src/main/java
└── com
    └── ercikWck
        └── booking_service
            ├── BookingServiceApplication.java
            ├── config
            │   ├── ClientConfig.java
            │   ├── ClientProperties.java
            │   ├── ConfigAuditData.java
            │   └── OpenApiConfig.java
            ├── controller
            │   ├── BookingController.java
            │   └── dto
            │       ├── BookingDtoRequest.java
            │       └── BookingRequestPayload.java
            ├── domain
            │   ├── Booking.java
            │   ├── BookingService.java
            │   ├── BookingStatus.java
            │   └── CardDtoTransaction.java
            ├── events
            │   └── ReactiveKafkaProducerConfig.java
            ├── repository
            │   └── BookingRepository.java
            └── ticket
                ├── TicketClient.java
                └── Ticket.java

```
---
### 📮 Endpoints da API

#### ✈️ Reserva (`/bookings`)

| Método   | Caminho                       | Descrição                                 | Status Esperado          | Corpo de Resposta                   |
|----------|-------------------------------|-------------------------------------------|--------------------------|-------------------------------------|
| `POST`    | `/bookings`                  | Cria uma reserva                          | `201 OK`                 | Objeto `Booking`                    |
| `GET`    | `/bookings/{id}`              | Busca uma reserva pelo Id                 | `200 OK` / `404`         | Objeto `Booking` ou NotFound        |

## 🚀 Como executar localmente

### Pré-requisitos

* Docker (para Kafka e PostgreSQL)
* Java 21
* Gradle Wrapper (`./gradlew`)

### Rodando a aplicação

```bash
./gradlew bootRun
```

---

## 🧪 Executando os testes

Este projeto inclui testes:

* Unitários com WebTestClient e Mockito
* De integração com:

  * **PostgreSQL via Testcontainers**
  * **Kafka via Testcontainers**
  * **WireMock para simulação do ticket-service**

```bash
./gradlew test
```

---

## 📄 Documentação da API

A documentação interativa pode ser acessada após subir a aplicação:

* Swagger UI: [http://localhost:9002/docs](http://localhost:9002/docs)
* OpenAPI YAML: [http://localhost:9002/v3/api-docs.yaml](http://localhost:9002/v3/api-docs.yaml)

---

## 📤 Exportar contrato OpenAPI

Após a aplicação estar rodando, você pode exportar o contrato:

```bash
curl http://localhost:9002/v3/api-docs.yaml -o openapi.yaml
```

---

## 📊 Observabilidade & Métricas
  - Micrometer + Prometheus – coleta de métricas
  - OpenTelemetry (OTel Java Agent) – rastreabilidade distribuída (tracing)

## 🐳 Infraestrutura & DevOps
  - Docker – containerização da aplicação
  - GitHub Actions – CI/CD com build, testes e publicação de imagem
  - GHCR (GitHub Container Registry) – armazenamento da imagem gerada
> ⚠️ Configure as variáveis `registryUsername`, `registryToken` e `registryUrl` no Gradle para publicação da imagem.

## 🛠️ CI/CD

A aplicação pode ser integrada com CI para:

* Build automatizado via Gradle
* Geração e validação do contrato OpenAPI
* Testes (unitários e de integração)
* Build de imagem com PacketoBuildPacks `./gradlew bootBuildImage`
  
  Voce pode encontra a imagem publicada neste link abaixo:  
  [Container Registry](https://github.com/mscairlinestickets?tab=packages) ou na [branch main](https://github.com/mscairlinestickets/booking-service)

---

## 🚀 Comunicação com serviços externos

* 🔁 **`ticket-service`**: Consome endpoint POST `/api/flights/{flight}/{quantity}` para validar disponibilidade de voo e reservar.
* 📤 **Kafka**: Publica mensagens no tópico `${message.topic}` com os dados da transação de pagamento.

---

## 📃 Variáveis importantes (`application.yml`)

```yaml
server.port: 9002
spring.r2dbc.url: r2dbc:postgresql://localhost:5432/airlinedb_booking
ticket.catalog-flight-uri: http://localhost:9001
message.topic: booking-accepted
```

---

## 📉 Observações

* O sistema implementa lógica de fallback e retries para chamadas externas e publicação Kafka.
* O banco é versionado com Flyway, mas as conexões são reativas via R2DBC.
* É possível adaptar para suportar testes de contrato futuramente (via Pact ou Spring Cloud Contract).

---

## 👨‍💼 Autor

**Erick Nunes da Silva**
Booking Microservice — Julho de 2025

📎 [LinkedIn](https://www.linkedin.com/in/erick-silva-414098225/)  
💻 [GitHub](https://github.com/erickknsilva)
