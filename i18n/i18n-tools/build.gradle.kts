plugins {
    kotlin("jvm") version "2.1.0"
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.18.3")
}

tasks{
    test {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(21)
}
