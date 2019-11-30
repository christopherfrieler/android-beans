# Developing

Android Beans uses gradle and contains the gradle-wrapper. Hence, you can use the following gradle-commands:


## build

build the .aar-file: ``./gradlew assembleRelease``


## test

run the unit-tests: ``./gradlew testReleaseUnitTest`` 


## documentation

generate kdoc-documentation (includes javadoc): `./gradlew kdocJar`

## publish

publish build-artifacts to your local maven-repository: ``./gradlew publishToMavenLocal``

Publishing to other destinations should not be done manually, but only by the CI-build.
