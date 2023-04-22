plugins {
    id("groovy-gradle-plugin")
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-gradle-plugin
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.6")
    // https://mvnrepository.com/artifact/io.spring.dependency-management/io.spring.dependency-management.gradle.plugin
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.0.11.RELEASE")
}