import org.gradle.api.tasks.bundling.AbstractArchiveTask

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

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                pom {
                    name.set(project.name)
                    description.set(project.description)
                    url.set("https://github.com/openjobspec/ojs-java-contrib")
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            name.set("OpenJobSpec Contributors")
                            organization.set("OpenJobSpec")
                            organizationUrl.set("https://openjobspec.org")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/openjobspec/ojs-java-contrib.git")
                        developerConnection.set("scm:git:ssh://git@github.com/openjobspec/ojs-java-contrib.git")
                        url.set("https://github.com/openjobspec/ojs-java-contrib")
                    }
                }
            }
        }
    }
}
