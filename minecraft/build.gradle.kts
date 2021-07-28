subprojects {
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        if (project.name !== "minecraft-universal") {
            if (project.name !== "minecraft-common") {
                compileOnly(project(":minecraft:minecraft-common"))
            }

            compileOnly("com.google.protobuf:protobuf-java:3.17.3")
            compileOnly("com.eclipsesource.minimal-json:minimal-json:0.9.5")
        }
    }

    java {
        targetCompatibility = JavaVersion.toVersion(16)
        sourceCompatibility = JavaVersion.toVersion(16)
    }
}

tasks {
    jar {
        enabled = false
    }
}
