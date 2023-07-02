rootProject.name = "Sprinkler"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include(
    "readonly-collections",
    "binary-transcoders",
    "util",
    "benchmarks",
)
