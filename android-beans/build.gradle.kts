import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
	id("com.android.library")
	id("maven-publish")
	id("com.jfrog.bintray")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)

        versionCode = 1
        versionName = "${project.version}"
        // append version to android build-artifacts:
		libraryVariants.all { outputs.all { this as BaseVariantOutputImpl
			outputFileName = outputFileName.replace(base.archivesBaseName, "${base.archivesBaseName}-${version}")
		}}
		consumerProguardFile("proguard-rules.pro")
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
	implementation("net.sourceforge.streamsupport:streamsupport:1.7.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.1.0")

	testImplementation("junit:junit:4.12") {
        //exclude group: "org.hamcrest"
    }
    testImplementation("org.hamcrest:hamcrest:2.1")
    testImplementation("org.mockito:mockito-core:2.26.0")
    testImplementation("org.robolectric:robolectric:4.2.1")
}

val javadoc by tasks.registering(Javadoc::class) {
    source(android.sourceSets["main"].java.srcDirs)
    classpath += project.files(android.bootClasspath)
    android.libraryVariants.forEach {
        classpath += it.javaCompileProvider.get().classpath
    }
    options.memberLevel = JavadocMemberLevel.PROTECTED
    exclude("**/BuildConfig.class", "**/R.class", "**/R$*.class")
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(javadoc)
    from(javadoc.get().outputs)
    archiveClassifier.set("javadoc")
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
                }
            }

            afterEvaluate {
                artifact(tasks.getByName("bundleReleaseAar"))
                artifact(sourcesJar.get())
                artifact(javadocJar.get())
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
