plugins {
    java
    id("io.micronaut.application") version "4.4.0"
}

group = "com.example"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.openjobspec:ojs-micronaut:0.1.0")
    implementation("io.micronaut:micronaut-http-server-netty:4.5.0")
    implementation("io.micronaut:micronaut-management:4.5.0")
}

application {
    mainClass = "com.example.Application"
}
