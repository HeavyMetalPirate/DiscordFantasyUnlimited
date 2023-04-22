plugins {
    id("fu-spring-web")
    id("fu-spring-jpa")
}

dependencies {
    implementation(project(":Database"))
    implementation(project(":ItemHandling"))
    implementation(project(":Battles"))
    implementation(project(":Library"))
    implementation(project(":Utils"))
}
