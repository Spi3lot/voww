import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("java")
    kotlin("jvm")
}

group = "org.wuzl"
version = "1.0-SNAPSHOT"

object Version {

    const val JAKARTA_WEBSOCKET = "2.2.0"

    const val GLASSFISH_TYRUS = "2.1.5"

    const val JAVAFX = "21"

}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.openjfx:javafx-fxml:${Version.JAVAFX}")
    implementation("org.glassfish.tyrus:tyrus-server:${Version.GLASSFISH_TYRUS}")
    implementation("org.glassfish.tyrus:tyrus-container-grizzly-server:${Version.GLASSFISH_TYRUS}")
    implementation("jakarta.websocket:jakarta.websocket-api:${Version.JAKARTA_WEBSOCKET}")
    compileOnly("jakarta.websocket:jakarta.websocket-client-api:${Version.JAKARTA_WEBSOCKET}")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.wuzl.gui.WuzlGui"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}