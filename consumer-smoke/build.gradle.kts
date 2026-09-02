plugins {
    java
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.openjobspec:ojs-spring:0.5.0")
    implementation("org.openjobspec:ojs-quarkus:0.5.0")
    implementation("org.openjobspec:ojs-micronaut:0.5.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
