buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.kotlin)
        classpath(libs.android.build.tools.gradle)
        classpath(libs.maven.publish.plugin)
    }
}

plugins {
    id("org.sonarqube") version "7.2.3.7755"
}

allprojects {
    group = "rocks.frieler.android"
    version = "0.10.1"

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
        property("sonar.issue.ignore.multicriteria", "allowLogicalDependencyGrouping")
        property("sonar.issue.ignore.multicriteria.allowLogicalDependencyGrouping.ruleKey", "kotlin:S6629")
        property("sonar.issue.ignore.multicriteria.allowLogicalDependencyGrouping.resourceKey" ,"**/*")
        when (val analysisType = System.getenv("SONAR_ANALYSIS_TYPE")) {
            "branch" -> property("sonar.branch.name", System.getenv("SONAR_BRANCH_NAME"))
            "pull_request" -> property("sonar.pullrequest.key", System.getenv("SONAR_PULLREQUEST_KEY"))
            else -> logger.warn("unknown SONAR_ANALYSIS_TYPE: '$analysisType'")
        }
    }
}
