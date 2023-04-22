plugins {
    id("foo-plugin")
}

dependencies {
    api(project(":Library"))
    api(project(":Database"))
}

tasks.named("bootJar") {
    enabled = false
}