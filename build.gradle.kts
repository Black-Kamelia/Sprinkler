import java.util.*

plugins {
    val kotlinVersion: String by System.getProperties()
    val restriktVersion: String by System.getProperties()
    java
    `maven-publish`
    kotlin("jvm") version kotlinVersion
    id("com.zwendo.restrikt") version restriktVersion
}

val projectGroup: String by project
val projectGroupFqName: String by project
val projectMembers: String by project
val projectWebsite: String by project
val projectVersion: String by project

val kotlinVersion: String by System.getProperties()
val jvmVersion: String by project
val junitVersion: String by project

val rootProjectName = rootProject.name.toLowerCase()

group = projectGroup
version = projectVersion

val localProps = Properties().apply { load(file("gradle.local.properties").reader()) }

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.zwendo.restrikt")

    val projectName = project.name.toLowerCase()

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        compileJava {
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = jvmVersion
                freeCompilerArgs = listOf(
                    "-Xjvm-default=all",
                    "-Xlambdas=indy",
                    "-Xsam-conversions=indy",
                )
            }
        }

        jar {
            archiveBaseName.set("$rootProjectName-$projectName-${projectVersion}")
        }
    }

    publishing {
        publications {
            val artifactName = "$rootProjectName-$projectName"
            create<MavenPublication>("maven-$artifactName") {
                groupId = projectGroup
                artifactId = artifactName
                version = projectVersion

                pom {
                    name.set(artifactName)
                    description.set("Sprinkler@${projectName} | Black Kamelia")
                    url.set("https://github.com/Black-Kamelia/Sprinkler")

                    developers {
                        projectMembers.split(",").forEach {
                            developer {
                                id.set(it)
                            }
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/Black-Kamelia/Sprinkler.git")
                        developerConnection.set("scm:git:ssh://github.com/Black-Kamelia/Sprinkler.git")
                        url.set("https://github.com/Black-Kamelia/Sprinkler.git")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "GitHubPackages"
                setUrl("https://maven.pkg.github.com/Black-Kamelia/Sprinkler")
                credentials {
                    username = localProps["githubUsername"] as String? ?: "Unknown user"
                    password = localProps["githubPassword"] as String? ?: "Unknown password"
                }
            }
        }
    }
}
