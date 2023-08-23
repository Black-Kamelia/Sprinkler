rootProject.name = "Sprinkler"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include(
    "utils",
    "jvm-bridge",
    "readonly-collections",
    "binary-transcoders",
    "i18n",
)
