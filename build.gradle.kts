
// Gradle plugins
buildscript {
    repositories {
        maven(url ="https://plugins.gradle.org/m2")
    }

    dependencies {
        classpath("gradle.plugin.net.minecrell:licenser:0.3")
        classpath("com.github.jengelman.gradle.plugins:shadow:1.2.4")
        classpath("gradle.plugin.org.spongepowered:spongegradle:0.8.1")
        classpath("gradle.plugin.org.spongepowered:event-impl-gen:5.0.2")
    }
    val url :String by project.properties
    extra["url"] = url
    val organization :String by project.properties
    extra["organization"] = organization
    val name :String by project.properties
    extra["name"] = name
}

val api : Project by extra {
    this
}


plugins {
    java
}
apply {
    from("gradle/sponge.gradle.kts")
    plugin("org.spongepowered.meta")
}
