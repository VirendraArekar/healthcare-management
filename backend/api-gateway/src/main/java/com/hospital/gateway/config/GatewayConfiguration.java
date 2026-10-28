package com.hospital.gateway.config;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfiguration {
  @Bean
  ReactiveJwtDecoder decoder(@Value("${JWT_SECRET}") String secret) {
    if (secret.length() < 32) throw new IllegalArgumentException("JWT_SECRET too short");
    var d =
        NimbusReactiveJwtDecoder.withSecretKey(
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    d.setJwtValidator(JwtValidators.createDefaultWithIssuer("hospital-platform"));
    return d;
  }

  @Bean
  SecurityWebFilterChain security(ServerHttpSecurity http) {
    return http.csrf(x -> x.disable())
        .authorizeExchange(
            x ->
                x.pathMatchers(
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password",
                        "/actuator/health/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(x -> x.jwt(j -> {}))
        .build();
  }

  @Bean
  KeyResolver clientKey() {
    return exchange ->
        exchange
            .getPrincipal()
            .map(p -> p.getName())
            .switchIfEmpty(
                Mono.fromSupplier(
                    () ->
                        Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                            .map(a -> a.getAddress().getHostAddress())
                            .orElse("unknown")));
  }

  @Bean
  WebFilter correlation() {
    return (exchange, chain) -> {
      String supplied = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
      String id =
          supplied != null && supplied.matches("[a-zA-Z0-9-]{1,64}")
              ? supplied
              : UUID.randomUUID().toString();
      exchange.getResponse().getHeaders().set("X-Correlation-ID", id);
      return chain.filter(
          exchange
              .mutate()
              .request(
                  exchange
                      .getRequest()
                      .mutate()
                      .headers(h -> h.set("X-Correlation-ID", id))
                      .build())
              .build());
    };
  }
}
