plugins {
    java
    id("io.quarkus") version "3.11.0"
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
    implementation("org.openjobspec:ojs-quarkus:0.1.0")
    implementation("io.quarkus:quarkus-rest-jackson:3.11.0")
    implementation("io.quarkus:quarkus-smallrye-health:3.11.0")
}
