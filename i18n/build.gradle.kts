import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(i18nLibs.plugins.shadowJar)
}

dependencies {
    implementation(project(":utils"))

    compileOnly(i18nLibs.bundles.loading)
    testRuntimeOnly(i18nLibs.bundles.loading)
    testRuntimeOnly(testDependencies.junit.platform.console)
}

tasks {
    register<ShadowJar>("testJar") {
        group = "verification"

        archiveClassifier.set("tests")
        from(sourceSets.main.get().output, sourceSets.test.get().output)
        configurations = listOf(project.configurations.testRuntimeClasspath.get())
    }

    register<JavaExec>("runJarTests") {
        group = "verification"
        dependsOn("testJar", "koverFindJar")

        val testJarPath = layout.buildDirectory.file("libs/${project.name}-tests.jar").get().asFile.absolutePath

        mainClass.set("org.junit.platform.console.ConsoleLauncher")
        classpath = files(testJarPath)

        val buildDir = layout.buildDirectory.asFile.get()
        args = listOf(
            "--classpath", testJarPath.toString(),
            "--scan-classpath",
            "--include-tag", "jar-test",
            "--reports-dir", file("$buildDir/test-results").toString()
        )

//        val agentPath = "$buildDir/kover/kover-jvm-agent-${libs.plugins.kover.get().version}.jar"
        val agentPath = koverFindJar.get().outputs.files.files.iterator().next()
        val agentConfigPath = layout.projectDirectory.file("kover-agent-jar-config.args").asFile
        jvmArgs("-javaagent:$agentPath=file:$agentConfigPath")

        doFirst {
            println("Running tests from: $testJarPath")
        }
    }

    test {
        useJUnitPlatform {
            excludeTags("jar-test")
        }
        finalizedBy("runJarTests")
    }
}
