plugins {
    java
    `java-library`
    `maven-publish`
}

allprojects {
    group = property("group") as String
    version = property("version") as String

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    // Skip example projects from library configuration
    if (name == "examples") return@subprojects

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        api("org.openjobspec:ojs-sdk:${property("ojsSdkVersion")}")

        testImplementation("org.junit.jupiter:junit-jupiter:${property("junitVersion")}")
        testImplementation("org.mockito:mockito-core:${property("mockitoVersion")}")
        testImplementation("org.mockito:mockito-junit-jupiter:${property("mockitoVersion")}")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
    }

    tasks.test {
        useJUnitPlatform()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                pom {
                    url.set("https://github.com/openjobspec/ojs-java-contrib")
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                }
            }
        }
    }
}
