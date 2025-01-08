rootProject.name = "Sprinkler"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("testDependencies") {
            from(files("gradle/test.versions.toml"))
        }
        create("i18nLibs") {
            from(files("i18n/libs.versions.toml"))
        }
        create("binaryTranscodersLibs") {
            from(files("binary-transcoders/libs.versions.toml"))
        }
    }
}

include(
    "utils",
    "jvm-bridge",
    "readonly-collections",
    "binary-transcoders",
    "i18n",
)
