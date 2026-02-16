plugins {
    java
    `java-library`
}

description = "OJS Quarkus Extension"

dependencies {
    api("org.openjobspec:ojs-sdk:${property("ojsSdkVersion")}")
    api("io.quarkus:quarkus-core:${property("quarkusVersion")}")
    api("io.quarkus:quarkus-arc:${property("quarkusVersion")}")

    compileOnly("io.smallrye:smallrye-health-api:4.0.4")
    compileOnly("org.eclipse.microprofile.health:microprofile-health-api:4.0.1")

    testImplementation("io.quarkus:quarkus-junit5:${property("quarkusVersion")}")
}
