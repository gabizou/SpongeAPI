// Shared Gradle configuration for the Sponge projects
import net.minecrell.gradle.licenser.LicenseExtension
import net.minecrell.gradle.licenser.Licenser
import org.apache.tools.ant.taskdefs.optional.extension.Specification
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.plugins.ide.idea.model.IdeaModel

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
apply {
    plugin("java")
    plugin("eclipse")
    plugin("idea")
    plugin("org.spongepowered.gradle")
    from("gradle/deploy.gradle.kts")
    plugin("net.minecrell.licenser")
    plugin("checkstyle")
}

defaultTasks("licenseFormat", "build")

group = "org.spongepowered"


// This is a hacky way to set compatibility levels
// we can't configure plugins statically in imported scripts,
// nor can we depend on java since this is to configure all projects.
configure<ExtraPropertiesExtension> {
    if (convention is JavaPluginConvention) {
        (convention as JavaPluginConvention).sourceCompatibility = JavaVersion.VERSION_1_8
        (convention as JavaPluginConvention).targetCompatibility = JavaVersion.VERSION_1_8
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    "testCompile"("junit:junit:4.12")
    "testCompile"("org.hamcrest:hamcrest-library:1.3")
    "testCompile"("org.mockito:mockito-core:2.8.47")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:path", "-parameters"))
    options.isDeprecation = true
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

configure<IdeaModel> {
    module {
        inheritOutputDirs = true
    }
}
val api: Project by extra

tasks.withType<Jar> {
    manifest {
        attributes(
                mapOf(
                        "Specification-Title" to api.name,
                        "Specification-Version" to api.version,
                        "Specification-Vendor" to api.extra["organization"],
                        "Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"
                )
        )
    }
}

afterEvaluate {
    tasks.getByName<Jar>("jar") {
        val commit: String? = api.extra["commit"] as? String
        val branch: String? = api.extra["branch"] as? String
        commit?.let { manifest { attributes(mapOf("Git-Commit" to commit)) } }
        branch?.let { manifest { attributes(mapOf("Git-Branch" to branch)) } }
    }
    tasks.withType<AbstractArchiveTask> {
        if (duplicatesStrategy == DuplicatesStrategy.INCLUDE) {
            setDuplicatesStrategy(DuplicatesStrategy.FAIL)
        }
    }
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
    options {
        encoding = "UTF-8"
        if (options is StandardJavadocDocletOptions) {
            (options as StandardJavadocDocletOptions).links(
                    "http://www.slf4j.org/apidocs/",
                    "https://google.github.io/guava/releases/21.0/api/docs/",
                    "https://google.github.io/guice/api-docs/4.1/javadoc/",
                    "https://zml2008.github.io/configurate/configurate-core/apidocs/",
                    "https://zml2008.github.io/configurate/configurate-hocon/apidocs/",
                    "https://flowpowered.com/math/",
                    "https://flowpowered.com/noise/",
                    "http://asm.ow2.org/asm50/javadoc/user/",
                    "https://docs.oracle.com/javase/8/docs/api/"
            ).addStringOption("Xdoclint:none", "-quiet")
        }
    }
}

task<Jar>("javadocJar") {
    val javadoc = tasks.getByName("javadoc", Javadoc::class)
    dependsOn(javadoc)
    classifier = "javadoc"
    from(javadoc.destinationDir)
}

configure<LicenseExtension> {
    header = api.file("HEADER.txt")
    include("**/*.java")
    newLine = false
}
