import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(binaryTranscodersLibs.plugins.jmh)
}

dependencies {
    implementation(project(":util"))
    api(project(":jvm-bridge"))
}

val jvmVersion: String by rootProject

tasks {
    compileJmhJava {
        sourceCompatibility = jvmVersion
    }

    compileJmhKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(jvmVersion))
        }
    }

    jmh {
        warmup.set("10s")
        timeOnIteration.set("10s")
        timeUnit.set("ms")

        warmupIterations.set(2)
        iterations.set(2)
        fork.set(1)
    }

}
