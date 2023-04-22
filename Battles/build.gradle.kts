plugins {
    id("foo-plugin")
    id("fu-spring-jpa")
}

dependencies {
    api(project(":Database"))
    api(project(":ItemHandling"))
    api(project(":Library"))
    implementation(project(":Utils"))

    implementation("org.springframework.boot:spring-boot-starter-websocket")
}

tasks.named("bootJar") {
    enabled = false
}