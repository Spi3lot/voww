import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm")
}

group = "org.wuzl"
version = "1.0-SNAPSHOT"
val jakartaWebsocketVersion = "2.2.0"
val glassfishTyrusVersion = "2.1.5"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.glassfish.tyrus:tyrus-server:$glassfishTyrusVersion")
    implementation("org.glassfish.tyrus:tyrus-container-grizzly-server:$glassfishTyrusVersion")
    implementation("jakarta.websocket:jakarta.websocket-api:$jakartaWebsocketVersion")
    compileOnly("jakarta.websocket:jakarta.websocket-client-api:$jakartaWebsocketVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.wuzl.client.WebSocketClientKt"
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