rootProject.name = "Arc"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.jpenilla.xyz/snapshots/")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.protobuf") {
                useModule("com.google.protobuf:protobuf-gradle-plugin:${requested.version}")
            }
        }
    }
}
