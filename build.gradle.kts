import java.util.Base64
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    `maven-publish`
    signing
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.restrikt)
    alias(libs.plugins.kover)
}

val jvmVersion: String by project
val rootProjectName = rootProject.name.lowercase()

group = findProp<String>("projectGroup")!!

val props = Properties().apply { load(file("gradle.properties").reader()) }

fun String.base64Decode() = String(Base64.getDecoder().decode(this))

val restriktVersion: String by System.getProperties()

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "com.zwendo.restrikt2")
    apply(plugin = "org.jetbrains.kotlinx.kover")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.dokka-javadoc")

    val projectName = project.name.lowercase()
    val projectVersion = if (project != rootProject) {
        findProp<String>("$projectName.version")
    } else {
        "0.1.0"
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(rootProject.libs.restrikt.annotations)
        compileOnly(rootProject.libs.jetbrains.annotations)
        testImplementation(rootProject.testDependencies.bundles.implementation)
        testRuntimeOnly(rootProject.testDependencies.bundles.runtime)
    }

    java {
        withSourcesJar()
        modularity.inferModulePath.set(true)
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

    kover {
        currentProject {
            sources.excludedSourceSets = setOf("jmh")
        }
    }

    dokka {
        dokkaSourceSets {
            configureEach {
                val packageFiles = fileTree("src/main/kotlin") {
                    include("**/package.md") // Matches files recursively
                }.files
                includes.from(packageFiles)
                val moduleFile = project.projectDir.resolve("src/main/kotlin/module.md")
                if (moduleFile.exists()) {
                    includes.from(moduleFile)
                }
            }
        }
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

        compileJava {
            doFirst {
                options.compilerArgs.addAll(
                    listOf(
                        "--patch-module",
                        "com.black_kamelia.sprinkler.$projectName=${project.layout.buildDirectory.get().asFile}/classes/kotlin/main",
                    )
                )
            }
        }

        setupKotlinCompilation {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(jvmVersion))
                freeCompilerArgs = listOf(
                    "-Xjvm-default=all",
                    "-Xsam-conversions=indy",
                )
            }
        }

        jar {
            archiveBaseName.set("$rootProjectName-$projectName-$projectVersion")
        }

        register<Jar>("dokkaJavadocJar") {
            dependsOn(dokkaGeneratePublicationJavadoc)
            from(dokkaGenerateModuleJavadoc.get().outputDirectory)
            archiveClassifier.set("javadoc")
        }

        register<Jar>("htmlDocJar") {
            dependsOn(dokkaGeneratePublicationHtml)
            from(dokkaGeneratePublicationHtml.get().outputDirectory)
            archiveClassifier.set("html-doc")
        }

    }

    publishing {
        publications {
            create<MavenPublication>("maven-$projectName") {
                groupId = findProp<String>("projectGroup")
                // fix this: This is a workaround for the util project
                artifactId = if (projectName == "util") "utils" else projectName
                version = projectVersion
                from(components["java"])
                artifact(tasks.named("dokkaJavadocJar"))
                artifact(tasks.named("htmlDocJar")) {
                    classifier = "html-doc"
                }


                pom {
                    name.set(projectName)
                    description.set("Sprinkler@${projectName} | Black Kamelia")
                    url.set("https://github.com/Black-Kamelia/Sprinkler")

                    developers {
                        findProp<String>("projectMembers")!!.split(",").forEach {
                            developer {
                                id.set(it)
                            }
                        }
                    }

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
                url = if (projectVersion!!.endsWith("SNAPSHOT")) {
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

inline fun <reified T> Project.findProp(name: String): T? {
    val strProp = findProperty(name) as String? ?: return null
    return when (T::class) {
        Boolean::class -> strProp.toBoolean() as T
        Int::class -> strProp.toInt() as T
        else -> strProp as T
    }
}
