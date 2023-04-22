plugins {
    id("fu-spring-jpa")
}

dependencies {
    api(project(":ItemHandling"))
    api(project(":Library"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.core:jackson-annotations")

}

tasks.named("bootJar") {
    enabled = false
}