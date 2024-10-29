val orgJsonVersion: String by rootProject
val snakeYamlVersion: String by rootProject

dependencies {
    implementation(project(":utils"))
    api(project(":jvm-bridge"))
    implementation("org.json", "json", orgJsonVersion)
    implementation("org.yaml", "snakeyaml", snakeYamlVersion)
    implementation("org.jsoup", "jsoup", "1.18.1")
}
