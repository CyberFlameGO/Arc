import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    `java-library`
    id("com.google.protobuf") version "0.8.16"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.notom3ga.arc"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/java")
        }

        proto {
            srcDir("src/main/proto")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.17.3"
    }
}

bukkit {
    main = "me.notom3ga.arc.Arc"
    apiVersion = "1.17"
    authors = listOf("notOM3GA")

    permissions {
        register("arc.command") {
            default = Default.OP
        }
    }
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper:1.17-R0.1-SNAPSHOT")

    implementation("com.github.jvm-profiling-tools:async-profiler:v2.0")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("Arc.proto")
    }
}
