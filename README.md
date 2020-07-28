# Android Beans

[![Build-Pipeline](https://github.com/christopherfrieler/android-beans/workflows/Build-Pipeline/badge.svg)](https://github.com/christopherfrieler/android-beans/actions?query=branch%3Amaster)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=christopherfrieler_android-beans&metric=alert_status)](https://sonarcloud.io/dashboard?id=christopherfrieler_android-beans)

A dependency injection library for Android apps, that aims for:
- **Power** - Provide the full power of dynamic dependency injection at runtime to allow e.g. injection of all implementations of a certain interface or injecting your custom implementation into another library.
- **Cleanness** - Your beans are just plain Kotlin or Java objects. No need to implement interfaces or pollute them with annotations from the framework.
- **Simplicity** - Allow users to define and inject their beans straight forward without the need for too complex structures as e.g. the components, modules and scopes seen with other DI frameworks.
- **Kotlin- and Java-Support** - Although Android Beans itself is written in Kotlin, it also offers a convenient API for Java apps going beyond the Kotlin's Java interoperability. You can even use it apps with mixed sources or during migrations from Java to Kotlin.


## Usage

To use Android Beans in your app, follow the [User-Manual](USAGE.md).


## Contributing and Developing 

If you have an idea, problem or question about Android Beans you can open an [issue](https://github.com/christopherfrieler/android-beans/issues).

If you want to clone the repository and work on the code have a look at the [DEVELOPING.md](DEVELOPING.md).
