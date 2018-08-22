
// Shared deployment configuration for the Sponge projects

apply {
    plugin("maven")
}

val buildNumber: String by extra {
    System.getenv()["BUILD_NUMBER"].plus('0')
}
val ciSystem: String? by extra { System.getenv()["CI_SYSTEM"] }
val commit: String? by extra { System.getenv()["GIT_COMMIT"] }
val branch: String? by extra { System.getenv()["GIT_BRANCH"] }

// Environment variables for the build set by the build server
tasks {
    "uploadArchives"(Upload::class) {

    }
}