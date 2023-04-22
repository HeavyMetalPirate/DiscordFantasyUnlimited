plugins {
    id("foo-plugin")
}

dependencies {
    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // probably MariaDB @ Dahoamstation? https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.4")

    // H2 for unit testing
    testImplementation("com.h2database:h2:2.1.212")
}