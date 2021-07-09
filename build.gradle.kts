import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.util.constants.*
import io.papermc.paperweight.util.registering
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    `java-library`
    id("com.google.protobuf") version "0.8.16"
    id("io.papermc.paperweight.patcher") version "1.1.9-SNAPSHOT"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("xyz.jpenilla.run-paper") version "1.0.3"
}

group = "me.notom3ga.arc"
version = "1.0"

java {
    targetCompatibility = JavaVersion.toVersion(16)
    sourceCompatibility = JavaVersion.toVersion(16)
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

runPaper {
    disablePluginJarDetection()
}

val mojangMappedServer: Configuration by configurations.creating
configurations.compileOnly {
    extendsFrom(mojangMappedServer)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.quiltmc.org/repository/release/")
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    mojangMappedServer("io.papermc.paper:paper:1.17.1-R0.1-SNAPSHOT:mojang-mapped")
    remapper("org.quiltmc:tiny-remapper:0.4.1")

    implementation("com.squareup.okhttp3:okhttp:3.14.1")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-mojang-mapped.jar")
        archiveClassifier.set("mojang-mapped")
        minimize()
    }

    val productionMappedJar by registering<RemapJar> {
        inputJar.set(shadowJar.flatMap { it.archiveFile })
        outputJar.set(project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar"))
        mappingsFile.set(project.layout.projectDirectory.file("gradle/mappings-1.17.1.tiny"))
        fromNamespace.set(DEOBF_NAMESPACE)
        toNamespace.set(SPIGOT_NAMESPACE)
        remapper.from(project.configurations.remapper)
        remapClasspath.from(mojangMappedServer)
    }

    build {
        dependsOn(productionMappedJar)
    }

    jar {
        enabled = false
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("arc.proto")
    }

    runServer {
        minecraftVersion("1.17.1")
        pluginJars(productionMappedJar.flatMap { it.outputJar })
        jvmArgs = listOf("-XX:+UnlockDiagnosticVMOptions", "-XX:+DebugNonSafepoints")
    }
}
