package com.hospital.common;

import static org.junit.jupiter.api.Assertions.*;

import com.hospital.common.client.DirectoryClient;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class DirectoryEncodingTest {
  @Test
  void availabilityEncodesTimestampExactlyOnceAndForwardsIdentity() throws Exception {
    var received = new AtomicReference<String>();
    var bearer = new AtomicReference<String>();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/",
        exchange -> {
          received.set(exchange.getRequestURI().getRawQuery());
          bearer.set(exchange.getRequestHeaders().getFirst("Authorization"));
          var body = "{\"available\":true}".getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().set("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, body.length);
          exchange.getResponseBody().write(body);
          exchange.close();
        });
    server.start();
    try {
      var jwt =
          Jwt.withTokenValue("synthetic-test-token")
              .header("alg", "none")
              .subject(UUID.randomUUID().toString())
              .build();
      SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
      var url = "http://127.0.0.1:" + server.getAddress().getPort();
      new DirectoryClient(url, url, url)
          .available(
              UUID.randomUUID(),
              Instant.parse("2030-01-01T10:00:00Z"),
              Instant.parse("2030-01-01T10:30:00Z"));
      assertEquals(
          "startsAt=2030-01-01T10%3A00%3A00Z&endsAt=2030-01-01T10%3A30%3A00Z", received.get());
      assertEquals("Bearer synthetic-test-token", bearer.get());
    } finally {
      SecurityContextHolder.clearContext();
      server.stop(0);
    }
  }
}
