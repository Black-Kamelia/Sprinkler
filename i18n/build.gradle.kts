dependencies {
    implementation(project(":utils"))

    compileOnly(rootProject.i18nLibs.bundles.loading)
    testRuntimeOnly(rootProject.i18nLibs.bundles.loading)
}
