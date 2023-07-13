plugins {
    id("me.champeau.jmh") version "0.7.1"
}

dependencies {
    implementation(project(":util"))
}

tasks {
    jmh {
        warmup.set("5s")
        timeOnIteration.set("5s")
        timeUnit.set("s")

        warmupIterations.set(3)
        iterations.set(5)
        fork.set(1)
    }
}