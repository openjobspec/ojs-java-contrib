rootProject.name = "ojs-java-contrib"

include("ojs-spring")
include("ojs-quarkus")
include("ojs-micronaut")

// Example projects use framework-specific plugins and should be built independently.
// To build an example: ./gradlew -p ojs-spring/examples build
// include("ojs-spring:examples")
// include("ojs-quarkus:examples")
// include("ojs-micronaut:examples")
