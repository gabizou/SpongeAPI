import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.xerces.dom.events.EventImpl
import org.spongepowered.eventimplgen.EventImplGenPlugin
import org.spongepowered.eventimplgen.EventImplGenTask
import org.spongepowered.eventimplgen.jdt.internal.core.JavaProject

// Gradle plugins
buildscript {
    repositories {
        maven(url ="https://plugins.gradle.org/m2")
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:1.2.4")
        classpath("gradle.plugin.org.spongepowered:spongegradle:0.8.1")
        classpath("gradle.plugin.org.spongepowered:event-impl-gen:5.0.2")
    }
}

extra["api"] = this

allprojects {
    apply {
        plugin("java")
    }
}

plugins {
    java
}

apply {
    from("gradle/sponge.gradle.kts")
    plugin("org.spongepowered.meta")
    plugin("org.spongepowered.event-impl-gen")
    plugin("com.github.johnrengelman.shadow")
}

java {
    sourceSets.create("ap") {
        compileClasspath += sourceSets["main"].compileClasspath + sourceSets["main"].output
    }
}

dependencies {

    // Logging
    compile("org.slf4j:slf4j-api:1.7.25")

    // Dependencies provided by Minecraft
    compile("com.google.guava:guava:21.0")
    compile("com.google.errorprone:error_prone_annotations:2.0.15")
    compile("com.google.code.gson:gson:2.8.0")
    compile("org.apache.commons:commons-lang3:3.5")
    // Only included in server
    compile("com.google.code.findbugs:jsr305:3.0.1")

    // Dependency injection
    compile("com.google.inject:guice:4.1.0")

    // Java 8 high performance cache (+ wrapper for Guava)
    compile("com.github.ben-manes.caffeine:caffeine:2.5.4")
    compile("com.github.ben-manes.caffeine:guava:2.5.4") {
        exclude("com.google.guava", "guava")
    }

    // Plugin meta
    compile("org.spongepowered:plugin-meta:0.4.1")

    // Configuration
    compile("org.spongepowered:configurate-hocon:3.6")
    compile("org.spongepowered:configurate-gson:3.6")
    compile("org.spongepowered:configurate-yaml:3.6")

    compile("com.flowpowered:flow-math:1.0.3")
    compile("com.flowpowered:flow-noise:1.0.1-SNAPSHOT")

    // Event generation
    compile("org.ow2.asm:asm:5.2")
}

val genEventImpl = tasks.getByName<EventImplGenTask>("genEventImpl") {
    outputFactory = "org.spongepowered.api.event.SpongeEventFactory"
    include("org/spongepowered/api/event/*/**/*")
    exclude("org/spongepowered/api/event/cause/")
    exclude("org/spongepowered/api/event/filter/")
    exclude("org/spongepowered/api/event/impl/")
}

// Define the setupDecompWorkspace so it runs when setting up the implementation workspace.
// in FG 3, we'll need to make this a different hook of sorts.
task("setupDecompWorkspace") {
    dependsOn(genEventImpl)
}

tasks.getByName<ShadowJar>("shadowJar") {
    classifier = "shaded"
    from(java.sourceSets["main"].allSource)
    from(java.sourceSets["ap"].allSource)
}

