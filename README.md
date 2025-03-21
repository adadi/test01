[ARO] Upgrade Java & Spring Boot – Security & API Communication Update

ASIWANTSOTHAT

As a developer,
I want to upgrade the project from Spring Boot 2 to Spring Boot 3 and from Java 11 to Java 21,
So that I can improve performance, security, and compatibility with modern libraries and frameworks.

⸻

Description

The project needs to be upgraded to Spring Boot 3 and Java 21. During this upgrade, some breaking changes require modifications, including:
	1.	Remove Keycloak library because it is not compatible with Spring Boot 3.
	2.	Update security configuration by replacing Keycloak with the Nabil security library.
	3.	Ensure API communication with the authentication server works properly using Nabil library.
	4.	Upgrade dependencies including Lombok and other required libraries.
	5.	Upgrade Spring Boot and Java versions for the Common-Market-Data project.
	6.	Integrate Swagger UI and test the application using Swagger after the upgrade.

⸻

Request Actions
	•	Upgrade Java from 11 to 21 and ensure compatibility with all dependencies.
	•	Upgrade Spring Boot from 2 to 3, resolving all breaking changes.
	•	Remove Keycloak dependency and replace authentication logic with Nabil security library.
	•	Update API communication with the authentication server using the Nabil library.
	•	Upgrade Lombok and other necessary dependencies to their latest compatible versions.
	•	Upgrade Spring Boot and Java for the Common-Market-Data project.
	•	Add Swagger UI to expose API documentation and ensure all endpoints work correctly.

⸻

Design & Implementation

1. Remove Keycloak & Update Security Configuration
	•	Delete Keycloak dependency from pom.xml:

<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-boot-starter</artifactId>
    <version>...</version>
</dependency>


	•	Replace security configuration with the Nabil security library:

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .build();
    }
}



2. Update API Communication with Auth Server
	•	Use the Nabil library to authenticate API calls:

@Component
public class ApiAuthService {
    public String getAccessToken() {
        // Logic to retrieve and return token using Nabil library
    }
}



3. Upgrade Dependencies (Lombok & Others)
	•	Update pom.xml with the latest Lombok version:

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>


	•	Upgrade Spring Boot version:

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>



4. Upgrade Spring & Java for Common-Market-Data Project
	•	Update Java version in pom.xml:

<properties>
    <java.version>21</java.version>
</properties>


	•	Ensure all dependencies in the Common-Market-Data project are compatible with Java 21 and Spring Boot 3.

5. Add Swagger for API Documentation
	•	Add Swagger dependencies:

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>


	•	Enable Swagger in the application:

@OpenAPIDefinition(info = @Info(title = "API Documentation", version = "1.0"))
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working!");
    }
}



⸻

Acceptance Criteria / BDD

Scenario: Application Upgrade & API Testing
	•	Given: The application is upgraded to Spring Boot 3 and Java 21.
	•	When: A user opens Swagger UI (https://your-api-url/swagger).
	•	And: Clicks “Authorize”, enters the token (retrieved via cURL), and clicks “Authorize”.
	•	And: Selects an API endpoint, clicks “Try it out”, fills in parameters or request body if needed, and clicks “Execute”.
	•	Then: The API should return a valid response with expected data and status codes.

⸻

This document outlines the necessary upgrades and testing procedures after migrating to Spring Boot 3 and Java 21 while ensuring secure authentication and API communication. Let me know if you need any modifications!