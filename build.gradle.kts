plugins {
    java
    application
}

group = "dev.wyalves"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("dev.wyalves.FlywayChecksumUtil")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.flywaydb:flyway-core:7.7.3")
}

tasks.test {
    useJUnitPlatform()
}
