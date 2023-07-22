plugins {
    val jmhPluginVersion: String by System.getProperties()
    id("me.champeau.jmh") version jmhPluginVersion
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
        warmup.set("5s") // 60s
        timeOnIteration.set("5s") // 60s
        timeUnit.set("ms")

        warmupIterations.set(3) // 5
        iterations.set(5)
        fork.set(1) // 5
    }

}
