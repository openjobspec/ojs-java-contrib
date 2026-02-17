plugins {
    java
    `java-library`
}

description = "OJS Micronaut Integration"

dependencies {
    api("org.openjobspec:ojs-sdk:${property("ojsSdkVersion")}")
    api("io.micronaut:micronaut-context:${property("micronautVersion")}")
    api("io.micronaut:micronaut-inject:${property("micronautVersion")}")

    compileOnly("io.micronaut:micronaut-management:${property("micronautVersion")}")

    testImplementation("io.micronaut.test:micronaut-test-junit5:4.3.0")
    testImplementation("io.micronaut:micronaut-inject-java:${property("micronautVersion")}")
}
