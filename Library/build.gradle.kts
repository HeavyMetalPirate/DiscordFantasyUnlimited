plugins {
    id("fu-spring-jpa")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("com.thoughtworks.xstream:xstream:1.4.20")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
}

tasks.named("bootJar") {
    enabled = false
}