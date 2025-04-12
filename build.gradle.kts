import com.android.build.gradle.internal.cxx.logging.warnln

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.kotlin)
        classpath(libs.android.build.tools.gradle)
    }
}

plugins {
    id("org.sonarqube") version "6.1.0.5360"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

allprojects {
    group = "rocks.frieler.android"
    version = "0.9.1"

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

nexusPublishing {
    packageGroup = project.group as String
    this.repositories {
        sonatype {
            stagingProfileId = System.getenv("SONATYPE_STAGING_PROFILE_ID")
            username = System.getenv("SONATYPE_USERNAME")
            password = System.getenv("SONATYPE_PASSWORD")
        }
    }
}
