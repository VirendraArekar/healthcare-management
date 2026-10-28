# Spring Boot compatibility migration

The initial requested baseline was Spring Boot 3.x. A resolved runtime SBOM scan found four critical Spring 6.2.19 advisories with fixes listed in Spring 7. After the user authorized the necessary compatibility work, the application was upgraded to Spring Boot 4.0.9, Spring Framework 7.0.10 and Spring Cloud 2025.1.4, retaining Java 21 and the service architecture.

The migration updates modular web MVC, Kafka, Flyway and OAuth resource-server starters, EntityScan and MVC-test imports, explicit servlet security configuration, Jackson 3 JSON mapping, and Springdoc 3.0.3. The application does not depend on the deprecated Jackson 2 compatibility module.

These choices follow the [official Spring Boot migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) and the [Springdoc compatibility matrix](https://springdoc.org/). See the final test and security reports for actual verification; a version change alone is not a successful migration.
