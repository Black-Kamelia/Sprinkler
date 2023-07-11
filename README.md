<div align="center">

![Sprinkler logo](assets/img/Sprinkler_light.svg#gh-light-mode-only)
![Sprinkler logo](assets/img/Sprinkler_dark.svg#gh-dark-mode-only)

<h3><i>A non-intrusive set of extensions sprinkled over the Kotlin standard library.</i></h3>

[![Build status](https://ci.black-kamelia.com/buildStatus/icon?subject=Build&job=Sprinkler%2Fmaster)](https://ci.black-kamelia.com/job/Sprinkler/)
</div>

## Summary

- [Summary](#summary)
- [What is it?](#what-is-it)
- [Modules and Documentation](#modules-and-documentation)
- [Integration](#integration)
- [License](#license)

## What is it?

**Sprinkler** is an open-source set of extensions to the **[Kotlin](https://kotlinlang.org/)** standard library, in the
same vein as projects such as ***kotlinx***, that is mainly used in **[Black Kamelia](https://black-kamelia.com)**.

## Modules and Documentation

*Click the name of a module to go to its documentation.*

- ***[ReadOnly Collections](readonly-collections/README.md)***: Cast-safe read-only collections for Kotlin
- ***[Binary Transcoders](binary-transcoders/README.md)***: Composable binary encoders and decoders builders
- ***[Utils](util/README.md)***: Various utilities for Kotlin (and Java+Kotlin)

## Integration

*Do replace `{latest-version}` with the latest version available on maven-central*
*and {module} with the module you want*

The available modules are:

- `readonly-collections` [![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia/sprinkler-readonly-collections.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.black-kamelia%22%20AND%20a:%22sprinkler-readonly-collections%22)
- `binary-transcoders` [![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia/sprinkler-binary-transcoders.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.black-kamelia%22%20AND%20a:%22sprinkler-binary-transcoders%22)
- `util` [![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia/sprinkler-util.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.black-kamelia%22%20AND%20a:%22sprinkler-util%22)

### Maven

```XML
<dependencies>
  <dependency>
    <groupId>com.black-kamelia</groupId>
    <artifactId>sprinkler-{module}</artifactId>
    <version>{latest-version}</version>
  </dependency>
</dependencies>
```

### Gradle

<details open>
<summary>Kotlin DSL</summary>
<p>

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.black-kamelia:sprinkler-{module}:{latest-version}")
}
```
</p>
</details>

<details>
<summary>Groovy DSL</summary>
<p>

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.black-kamelia:sprinkler-{module}:{latest-version}'
}
```
</p>
</details>

## License

[MIT License](LICENSE)
