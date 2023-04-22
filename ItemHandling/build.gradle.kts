buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("cz.habarta.typescript-generator:typescript-generator-gradle-plugin:2.36.1070")
    }
}

plugins {
    id("foo-plugin")
}
//apply plugin: 'cz.habarta.typescript-generator'

dependencies {
    implementation(project(":Library"))

    // https://mvnrepository.com/artifact/com.thoughtworks.xstream/xstream
    api("com.thoughtworks.xstream:xstream:1.4.20")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")

}

tasks.named("bootJar") {
    enabled = false
}