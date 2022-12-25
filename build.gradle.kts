import java.util.Base64
import java.util.Properties

plugins {
    val kotlinVersion: String by System.getProperties()
    val restriktVersion: String by System.getProperties()
    java
    `maven-publish`
    signing
    jacoco
    kotlin("jvm") version kotlinVersion
    id("com.zwendo.restrikt") version restriktVersion
}

val projectGroup: String by project
val projectGroupFqName: String by project
val projectMembers: String by project
val projectWebsite: String by project

val kotlinVersion: String by System.getProperties()
val jvmVersion: String by project
val junitVersion: String by project

val rootProjectName = rootProject.name.toLowerCase()

group = projectGroup

val localProps = Properties().apply { load(file("gradle.local.properties").reader()) }
val props = Properties().apply { load(file("gradle.properties").reader()) }

fun String.base64Decode() = String(Base64.getDecoder().decode(this))

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "com.zwendo.restrikt")
    apply(plugin = "jacoco")

    val projectName = project.name.toLowerCase()
    val projectVersion = props["$projectName.version"] as? String ?: "0.1.0"

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

    signing {
        val signingKey = findProperty("signingKey") as String? ?: ""
        val signingPassword = findProperty("signingPassword") as String? ?: ""
        useInMemoryPgpKeys(signingKey.base64Decode(), signingPassword)
        sign(publishing.publications)
    }

    tasks {
        test {
            useJUnitPlatform()
            ignoreFailures = true
            finalizedBy(jacocoTestReport)
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
            archiveBaseName.set("$rootProjectName-$projectName-$projectVersion")
        }

        jacocoTestReport {
            reports {
                xml.required.set(true)
                csv.required.set(false)
                html.required.set(true)
            }
        }
    }

    publishing {
        publications {
            val artifactName = "$rootProjectName-$projectName"
            create<MavenPublication>("maven-$artifactName") {
                groupId = projectGroup
                artifactId = artifactName
                version = projectVersion
                from(components["java"])

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
                name = "mavenCentral"
                credentials(PasswordCredentials::class)
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
        }
    }
}
