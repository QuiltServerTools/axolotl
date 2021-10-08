import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")

    id("com.github.jakemarsden.git-hooks")
    id("com.github.johnrengelman.shadow")
    id("io.gitlab.arturbosch.detekt")
}

group = "io.github.quiltservertools"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }

    maven {
        name = "FabricMC"
        url = uri("https://maven.fabricmc.net/")
    }

    maven {
        name = "QuiltMC (Releases)"
        url = uri("https://maven.quiltmc.org/repository/release/")
    }

    maven {
        name = "QuiltMC (Snapshots)"
        url = uri("https://maven.quiltmc.org/repository/snapshot/")
    }

    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io/")
    }

    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    detektPlugins(libs.detekt)

    implementation(kotlin("stdlib-jdk8"))

    implementation(libs.kord.extensions)
    implementation(libs.kord.extra.mappings)

    // Logging dependencies
    implementation(libs.groovy)
    implementation(libs.logback)
    implementation(libs.logging)

    // Tags
    implementation(libs.kotlinx.serialization)
    implementation(libs.kaml)
    implementation(libs.jgit)
}

application {
    // This is deprecated, but the Shadow plugin requires it
    mainClassName = "io.github.quiltservertools.bot.AppKt"
}

gitHooks {
    setHooks(
        mapOf("pre-commit" to "detekt")
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"

    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "template.AppKt"
        )
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

detekt {
    buildUponDefaultConfig = true
    config = rootProject.files("detekt.yml")
}
