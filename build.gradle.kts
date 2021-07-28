plugins {
    `java-library`
}

allprojects {
    group = "me.notom3ga.arc"
    version = "1.0"
    description = "An advanced profiler to detect and stop lag"
}

subprojects {
    apply<JavaLibraryPlugin>()

    java {
        targetCompatibility = JavaVersion.toVersion(11)
        sourceCompatibility = JavaVersion.toVersion(11)
    }

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
    }
}
