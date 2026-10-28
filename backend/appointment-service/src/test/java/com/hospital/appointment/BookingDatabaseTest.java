package com.hospital.appointment;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

class BookingDatabaseTest {
  @Test
  void concurrentBookingUsesDatabaseConstraint() throws Exception {
    Assumptions.assumeTrue(
        DockerClientFactory.instance().isDockerAvailable(), "Docker unavailable");
    try (var pg = new PostgreSQLContainer<>("postgres:17.6-alpine")) {
      pg.start();
      try (var c =
          DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())) {
        String sql =
            new String(
                getClass().getResourceAsStream("/db/migration/V2__appointments.sql").readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8);
        c.createStatement().execute(sql);
        c.createStatement()
            .execute(
                "insert into slots"
                    + " values('00000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000002',now()+interval"
                    + " '1 day',now()+interval '1 day 30 minutes')");
      }
      var pool = Executors.newFixedThreadPool(2);
      var start = new CountDownLatch(1);
      Callable<Boolean> attempt =
          () -> {
            start.await();
            try (var c =
                DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())) {
              c.createStatement()
                  .execute(
                      "insert into bookings values('"
                          + UUID.randomUUID()
                          + "','00000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000003','BOOKED')");
              return true;
            } catch (SQLException e) {
              if (!"23505".equals(e.getSQLState())) throw e;
              return false;
            }
          };
      try {
        var a = pool.submit(attempt);
        var b = pool.submit(attempt);
        start.countDown();
        assertNotEquals(a.get(20, TimeUnit.SECONDS), b.get(20, TimeUnit.SECONDS));
      } finally {
        pool.shutdownNow();
      }
    }
  }
}
