import java.util.Base64
import java.util.Properties

plugins {
    val kotlinVersion: String by System.getProperties()
    val restriktVersion: String by System.getProperties()
    val koverVersion: String by System.getProperties()
    java
    `maven-publish`
    signing
    id("org.jetbrains.kotlinx.kover") version koverVersion
    kotlin("jvm") version kotlinVersion
    id("com.zwendo.restrikt") version restriktVersion
}

val jvmVersion: String by project
val rootProjectName = rootProject.name.toLowerCase()

group = findProp<String>("projectGroup")

val props = Properties().apply { load(file("gradle.properties").reader()) }

fun String.base64Decode() = String(Base64.getDecoder().decode(this))

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "com.zwendo.restrikt")
    apply(plugin = "kover")

    restrikt {
        enabled = findProp("enableRestrikt") ?: true
    }

    val projectName = project.name.toLowerCase()
    val projectVersion = findProp("$projectName.version") ?: "0.1.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        val junitVersion: String by project

        testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    signing {
        val signingKey = findProp<String?>("signingKey")
        val signingPassword = findProp<String?>("signingPassword")
        if (signingKey != null && signingPassword != null) {
            logger.info("Using in memory keys for signing")
            useInMemoryPgpKeys(signingKey.base64Decode(), signingPassword)
        } else {
            logger.info("Using local GPG keys for signing")
        }
        sign(publishing.publications)
    }

    tasks {
        test {
            useJUnitPlatform()
            ignoreFailures = true
            finalizedBy(koverVerify)
        }

        koverVerify {
            finalizedBy(koverXmlReport)
        }

        koverXmlReport {
            finalizedBy(koverHtmlReport)
        }

        setupJavaCompilation {
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }

        setupKotlinCompilation {
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

    }

    publishing {
        publications {
            val artifactName = "$rootProjectName-$projectName"
            create<MavenPublication>("maven-$artifactName") {
                groupId = findProp("projectGroup")
                artifactId = artifactName
                version = projectVersion
                from(components["java"])

                pom {
                    name.set(artifactName)
                    description.set("Sprinkler@${projectName} | Black Kamelia")
                    url.set("https://github.com/Black-Kamelia/Sprinkler")

                    developers {
                        findProp<String>("projectMembers").split(",").forEach {
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
                url = if (projectVersion.endsWith("SNAPSHOT")) {
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                } else {
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                }
            }
        }
    }
}

fun TaskContainerScope.setupJavaCompilation(block: JavaCompile.() -> Unit) {
    compileJava(block)
    compileTestJava(block)
}

fun TaskContainerScope.setupKotlinCompilation(block: org.jetbrains.kotlin.gradle.tasks.KotlinCompile.() -> Unit) {
    compileKotlin(block)
    compileTestKotlin(block)
}

@Suppress("UNCHECKED_CAST")
fun <T> Project.findProp(name: String): T = findProperty(name) as T
