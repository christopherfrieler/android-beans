import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
	id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka-android")
	id("maven-publish")
	id("com.jfrog.bintray")
}

android {
    sourceSets {
        maybeCreate("main").java.srcDirs("src/main/kotlin/")
        maybeCreate("test").java.srcDirs("src/test/kotlin/")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")
    kotlinOptions {
        val options = this as org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
        options.jvmTarget = "1.8"
    }

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)

        versionCode = 1
        versionName = "${project.version}"
        // append version to android build-artifacts:
		libraryVariants.all { outputs.all { this as BaseVariantOutputImpl
			outputFileName = outputFileName.replace(base.archivesBaseName, "${base.archivesBaseName}-${version}")
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
		}
    }
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:${Dependencies.kotlin_version}")
    api("org.jetbrains.kotlin:kotlin-reflect:${Dependencies.kotlin_version}")
    implementation("net.sourceforge.streamsupport:streamsupport:1.7.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

	testImplementation("junit:junit:4.13")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")
    testImplementation("org.mockito:mockito-core:2.26.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.robolectric:robolectric:4.3.1")
}

val kdocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokka)
    from("${buildDir}/dokka")
    archiveClassifier.set("kdoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create("maven", MavenPublication::class) {
            pom.withXml {
				asNode().appendNode("name", "android-beans")
				asNode().appendNode("description", "A dependency injection library for Java Android apps.")
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

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_API_KEY")

    with(pkg) {
        repo = "android"
        name = project.name
        userOrg = "christopherfrieler"
        setLicenses("MIT")
        desc = "A dependency injection library for Java Android apps."
        vcsUrl = "https://github.com/christopherfrieler/android-beans.git"
        githubRepo = "christopherfrieler/android-beans"

        version.name = "${project.version}"
    }
    publish = true

    setPublications("maven")
}
