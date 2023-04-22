plugins {
    id("foo-plugin")
}

dependencies {
    // Spring Data Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
}