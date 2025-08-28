package com.ercikWck.booking_service.config;


import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "ticket")
public record ClientProperties(
        @NotNull
        URI catalogFlightUri
) {


}
