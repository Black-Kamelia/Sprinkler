subprojects {
    dependencies {
        implementation("org.openjdk.jmh", "jmh-core", "1.36")
        implementation("org.openjdk.jmh", "jmh-generator-annprocess", "1.36")
    }
}

allprojects {
    // Disable publishing for all projects
    tasks {
        publish.configure {
            enabled = false
        }
        publishToMavenLocal.configure {
            enabled = false
        }
    }
}
