plugins {
    id("me.champeau.jmh") version "0.7.1"
}

dependencies {
    implementation(project(":util"))
}

val jvmVersion: String by rootProject

tasks {
    compileJmhJava {
        sourceCompatibility = jvmVersion
    }

    compileJmhKotlin {
        kotlinOptions {
            jvmTarget = jvmVersion
        }
    }

    jmh {
        warmup.set("5s")
        timeOnIteration.set("5s")
        timeUnit.set("ms")

        warmupIterations.set(3)
        iterations.set(5)
        fork.set(1)
    }

}
