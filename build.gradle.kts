import com.android.build.gradle.internal.cxx.logging.warnln

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

plugins {
    id("org.sonarqube") version "4.2.1.3168"
}

allprojects {
    group = "rocks.frieler.android"
    version = "0.8.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(layout.buildDirectory)
    }
}

sonar {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "christopherfrieler")
        property("sonar.projectName", "android-beans")
        property("sonar.projectKey", "christopherfrieler_android-beans")
        when (val analysisType = System.getenv("SONAR_ANALYSIS_TYPE")) {
            "branch" -> property("sonar.branch.name", System.getenv("SONAR_BRANCH_NAME"))
            "pull_request" -> property("sonar.pullrequest.key", System.getenv("SONAR_PULLREQUEST_KEY"))
            else -> warnln("unknown SONAR_ANALYSIS_TYPE: '%s'", analysisType)
        }
    }
}
