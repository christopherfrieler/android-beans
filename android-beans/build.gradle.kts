import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
	id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka") version "2.0.0"
    id("jacoco")
	id("maven-publish")
	id("signing")
}

android {
    sourceSets {
        maybeCreate("main").java.srcDirs("src/main/kotlin/")
        maybeCreate("test").java.srcDirs("src/test/kotlin/")
    }
    namespace = "rocks.frieler.android.beans"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    compileSdk = 34
    buildToolsVersion = "36.0.0"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        minSdk = 21

        // append version to android build-artifacts:
		libraryVariants.all { outputs.all { this as BaseVariantOutputImpl
			outputFileName = outputFileName.replace(base.archivesName.get(), "${base.archivesName.get()}-${version}")
		}}
		fileTree("proguard/").forEach(defaultConfig::consumerProguardFile)
    }

    buildTypes {
		getByName("release") {
			isMinifyEnabled = false

			// strip "-release"-qualifier from artifact file-names, because release is the default:
			android.libraryVariants.matching { name == "release" }.all { outputs.all { this as BaseVariantOutputImpl
				outputFileName = outputFileName.replace("-release", "") }
			}
            testBuildType = this.name
		}
    }

    publishing {
        singleVariant("release")
    }
}

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    coreLibraryDesugaring(libs.android.tools.desugar.jdk.libs)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertk.jvm)
    testImplementation(libs.mockito.kotlin)

    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.robolectric)
}

val kdocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaGenerate)
    from("${layout.buildDirectory}/dokka")
    archiveClassifier.set("kdoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

tasks.withType(Test::class) {
    useJUnitPlatform()
    with(extensions.getByType(JacocoTaskExtension::class)) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*") // workaround for https://github.com/gradle/gradle/issues/5184
    }
}
val jacocoReport by tasks.registering(JacocoReport::class) {
    group = "verification"
    dependsOn(tasks.getByName("testReleaseUnitTest"))
    classDirectories.from("${layout.buildDirectory}/tmp/kotlin-classes/release")
    executionData(files("${layout.buildDirectory}/jacoco/testReleaseUnitTest.exec"))
    reports {
        xml.required.set(true)
        html.required.set(false)
    }
}
rootProject.tasks["sonar"].dependsOn(jacocoReport)

publishing {
    publications {
        create("maven", MavenPublication::class) {
            pom.withXml {
				asNode().appendNode("name", "android-beans")
				asNode().appendNode("description", "A dependency injection library for Java Android apps.")
				asNode().appendNode("url", "https://github.com/christopherfrieler/android-beans")
                asNode().appendNode("licenses").appendNode("license")
                        .appendNode("name", "MIT").parent()
                        .appendNode("url", "https://opensource.org/licenses/MIT").parent()
                asNode().appendNode("scm")
						.appendNode("url", "https://github.com/christopherfrieler/android-beans").parent()
                asNode().appendNode("developers").appendNode("developer")
                        .appendNode("name", "Christopher Frieler").parent()
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.getByName("api").allDependencies.configureEach {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", this.group)
                    dependencyNode.appendNode("artifactId", this.name)
                    dependencyNode.appendNode("version", this.version)
                    dependencyNode.appendNode("scope", "compile")
                }
                configurations.getByName("implementation").allDependencies.configureEach {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", this.group)
                    dependencyNode.appendNode("artifactId", this.name)
                    dependencyNode.appendNode("version", this.version)
                    dependencyNode.appendNode("scope", "runtime")
                }
            }

            afterEvaluate {
                artifact(tasks.getByName("bundleReleaseAar"))
                artifact(sourcesJar.get())
                artifact(kdocJar.get())
            }
        }
    }
}

signing {
    sign(publishing.publications)

    System.getenv("SIGNING_KEY_ID")?.also { signingKeyId ->
        project.setProperty("signing.keyId", signingKeyId)
        project.setProperty("signing.secretKeyRingFile", rootProject.file("$signingKeyId.gpg"))
        project.setProperty("signing.password", System.getenv("SIGNING_KEY_PASSWORD"))
    }
}
