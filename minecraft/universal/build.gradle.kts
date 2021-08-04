plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    implementation(project(":profiler"))
    implementation(project(":minecraft:minecraft-common"))
    implementation(project(":minecraft:minecraft-paper"))

    implementation("com.google.protobuf:protobuf-java:3.17.3")
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-Minecraft-${rootProject.version}.jar")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))

        listOf(
                "com.eclipsesource.json",
                "com.google.protobuf",
                "com.sun.jna",
                "one.profiler",
                "org.slf4j"
        ).forEach { relocate(it, "me.notom3ga.arc.libs.$it") }

        minimize {
            exclude(project(":profiler"))
            exclude(project(":minecraft:minecraft-common"))
            exclude(project(":minecraft:minecraft-paper"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
