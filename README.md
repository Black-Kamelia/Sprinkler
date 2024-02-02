<div align="center">

![Sprinkler logo](assets/img/Sprinkler_light.svg#gh-light-mode-only)
![Sprinkler logo](assets/img/Sprinkler_dark.svg#gh-dark-mode-only)

<h3><i>A non-intrusive set of extensions sprinkled over the Kotlin standard library.</i></h3>

[![Jenkins](https://shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.black-kamelia.com%2Fjob%2FSprinkler%2Fjob%2FSprinkler%2Fjob%2Fmaster%2F&label=Build)
](https://ci.black-kamelia.com/job/Sprinkler/job/Sprinkler/job/master/lastBuild/)
[![Jenkins Tests](https://shields.io/jenkins/tests?jobUrl=https%3A%2F%2Fci.black-kamelia.com%2Fjob%2FSprinkler%2Fjob%2FSprinkler%2Fjob%2Fmaster%2F&label=Tests)
](https://ci.black-kamelia.com/job/Sprinkler/job/Sprinkler/job/master/lastBuild/testReport/)
[![Jenkins Coverage](https://shields.io/jenkins/coverage/apiv4?jobUrl=https%3A%2F%2Fci.black-kamelia.com%2Fjob%2FSprinkler%2Fjob%2FSprinkler%2Fjob%2Fmaster%2F&label=Coverage)
](https://ci.black-kamelia.com/job/Sprinkler/job/Sprinkler/job/master/lastBuild/coverage/)

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
- ***[Utils](utils/README.md)***: Various utilities for Kotlin (and Java+Kotlin)
- ***[JVM Bridge](jvm-bridge/README.md)***: Utilities for JVM interoperability (Java+Kotlin)
- ***[i18n](i18n/README.md)***: Internationalization for the JVM

## Integration

*Do replace `{module}` with the module you want and `{latest-version}` with the latest version available on maven-central*

The available modules are:

- [`readonly-collections` ![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/readonly-collections)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/readonly-collections)
- [`binary-transcoders` ![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/binary-transcoders)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/binary-transcoders)
- [`utils` ![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/utils)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/utils)
- [`jvm-bridge` ![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/jvm-bridge)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/jvm-bridge)
- [`i18n` ![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/i18n)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/i18n)

### Maven

```XML
<dependencies>
  <dependency>
    <groupId>com.black-kamelia.sprinkler</groupId>
    <artifactId>{module}</artifactId>
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
  implementation("com.black-kamelia.sprinkler:{module}:{latest-version}")
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
  implementation 'com.black-kamelia.sprinkler:{module}:{latest-version}'
}
```
</p>
</details>

## License

[MIT License](LICENSE)
