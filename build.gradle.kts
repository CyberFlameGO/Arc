plugins {
    `java-library`
}

allprojects {
    group = "me.notom3ga.arc"
    version = "1.0"
    description = "An advanced profiler to detect and stop lag"

    apply<JavaLibraryPlugin>()

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
        }

        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
    }
}

tasks {
    jar {
        enabled = false
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
}
