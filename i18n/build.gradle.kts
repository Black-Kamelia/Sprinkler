val orgJsonVersion: String by rootProject
val snakeYamlVersion: String by rootProject

dependencies {
    implementation(project(":utils"))
    implementation("org.json", "json", orgJsonVersion)
    implementation("org.yaml", "snakeyaml", snakeYamlVersion)
}
