plugins {
    val jmhPluginVersion: String by System.getProperties()
    id("me.champeau.jmh") version jmhPluginVersion
}

dependencies {
    implementation(project(":utils"))
    api(project(":jvm-bridge"))
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
        warmup.set("60s")
        timeOnIteration.set("60s")
        timeUnit.set("ms")

        warmupIterations.set(5)
        iterations.set(5)
        fork.set(5)
    }

}
