plugins {
    java
    `java-library`
}

description = "OJS Spring Boot Starter"

dependencies {
    api("org.openjobspec:ojs-sdk:${property("ojsSdkVersion")}")
    api("org.springframework.boot:spring-boot-starter:${property("springBootVersion")}")

    compileOnly("org.springframework.boot:spring-boot-actuator:${property("springBootVersion")}")
    compileOnly("org.springframework:spring-tx:6.1.8")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
    testImplementation("org.springframework:spring-tx:6.1.8")
}
