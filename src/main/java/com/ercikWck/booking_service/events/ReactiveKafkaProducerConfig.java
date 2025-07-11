package com.ercikWck.booking_service.events;


import com.ercikWck.booking_service.domain.CardDtoTransaction;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class ReactiveKafkaProducerConfig {


    @Bean
    public ReactiveKafkaProducerTemplate<Long, CardDtoTransaction> reactiveKafkaProducerTemplate(final KafkaProperties properties){
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<Long, CardDtoTransaction>(SenderOptions.create(props));

    }


}
