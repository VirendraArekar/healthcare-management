package com.hospital.common.migration;

public final class MigrateMain {
  private MigrateMain() {}

  public static void main(String[] args) {
    org.flywaydb.core.Flyway.configure()
        .dataSource(required("DB_URL"), required("DB_USER"), required("DB_PASSWORD"))
        .locations("classpath:db/migration")
        .load()
        .migrate();
  }

  private static String required(String key) {
    String value = System.getenv(key);
    if (value == null || value.isBlank()) throw new IllegalArgumentException(key + " is required");
    return value;
  }
}
