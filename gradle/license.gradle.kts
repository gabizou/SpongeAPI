import net.minecrell.gradle.licenser.LicenseExtension

buildscript {
    repositories {
        maven(url ="https://plugins.gradle.org/m2")
    }

    dependencies {
        classpath("gradle.plugin.net.minecrell:licenser:0.3")
    }
}
val api : Project by extra

allprojects {
    apply {
        plugin("net.minecrell.licenser")
    }

    configure<LicenseExtension> {
        header = api.file("HEADER.txt")
        include("**/*.java")
        newLine = false
        val url :String by project.properties
        val organization :String by project.properties
        val name :String by project.properties
        if (this is ExtensionAware) { // By default, extensions that are being configured are not according to kotlin-dsl
            val extensionCon: ExtraPropertiesExtension = (this as ExtensionAware).extra
            extensionCon.set("url", url)
            extensionCon.set("organization", organization)
            extensionCon.set("name", name)
        }

    }

    tasks.getByName<ProcessResources>("processResources") {
        from("LICENSE.txt")
    }

}
