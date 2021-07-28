rootProject.name = "Arc"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.protobuf") {
                useModule("com.google.protobuf:protobuf-gradle-plugin:${requested.version}")
            }
        }
    }
}

include("profiler")
include("minecraft")

include("minecraft:common")
findProject(":minecraft:common")?.name = "minecraft-common"
