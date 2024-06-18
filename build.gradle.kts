import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.8.0" // Specify the Kotlin version you are using
    id("org.jetbrains.compose") version "1.6.0" // Specify the Compose plugin version
}

group = "de.jackBeBack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    // JUnit 5 (Jupiter) dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(11)
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Mnist"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "de.jackBeBack.mnist"
            }
            windows {
                // Optional: Add specific configurations for Windows MSI
            }
            linux {
                // Optional: Add specific configurations for Linux DEB
            }
        }
    }
}
