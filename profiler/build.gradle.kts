import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.google.protobuf") version "0.8.16"
}

java {
    targetCompatibility = JavaVersion.toVersion(11)
    sourceCompatibility = JavaVersion.toVersion(11)
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

dependencies {
    compileOnly("com.google.protobuf:protobuf-java:3.17.3")
    compileOnly("com.eclipsesource.minimal-json:minimal-json:0.9.5")
    compileOnly("com.github.oshi:oshi-core-java11:5.8.0")
}

tasks {
    jar {
        enabled = false
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("arc.proto")
    }
}
