val name: String by settings
rootProject.name = name

rootProject.apply {
    file("gradle/license.gradle.kts")
}
