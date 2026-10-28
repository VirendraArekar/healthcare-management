# Dependency baseline

Java 21 target; Spring Boot 4.0.9; Spring Cloud 2025.1.4; Jackson 3 BOM 3.1.7; Netty 4.2.19.Final; Tomcat 11.0.27; PostgreSQL JDBC 42.7.14. Explicit dependency management pins LZ4 1.12.1 and Bouncy Castle 1.86. Version availability was checked against Maven Central metadata and the complete Maven reactor was built/tested after updates.

React 19.1.1, React Router 7.18.4, Axios 1.21.0, Vite 7.3.7, TypeScript 5.9.2, React Icons 5.7.0 and Vitest 5.0.5 are pinned in frontend/package.json and package-lock.json. Use npm ci, not dependency resolution without the lockfile.

JUnit, Mockito, Spring Security test and Testcontainers dependencies are test-scoped; they are excluded from application runtime JARs. The Kafka runtime under tests/runtime is an isolated integration utility, not a deployable business service.

See SECURITY-REPORT for the final resolved SBOM scan. The previous Spring 6.2 advisories were addressed through the approved Spring 7 migration. Compatible patch updates do not establish a vulnerability-free or certified production deployment.
