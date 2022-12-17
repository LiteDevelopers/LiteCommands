
val coreArtifact by extra("core")
val velocityArtifact by extra("velocity")
val bukkitArtifact by extra("bukkit")
val bukkitAdventureArtifact by extra("bukkit-adventure")
val bungeeArtifact by extra("bungee")

plugins {
    id("idea")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    group = "dev.rollczi.litecommands"
    version = "2.7.0-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        withSourcesJar()
    }
}

subprojects {
    repositories {
        mavenCentral()

        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://repo.panda-lang.org/releases/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        repositories {
            mavenLocal()

            maven("panda", "https://repo.panda-lang.org", "MAVEN_USERNAME", "MAVEN_PASSWORD", false)

            maven("eternalcode", "https://repo.eternalcode.pl", "ETERNAL_CODE_MAVEN_USERNAME", "ETERNAL_CODE_MAVEN_PASSWORD")

            maven("minecodes", "https://repository.minecodes.pl", "MINE_CODES_MAVEN_USERNAME", "MINE_CODES_MAVEN_PASSWORD",)
        }
    }
}

fun RepositoryHandler.maven(name: String, url: String, username: String, password: String, deploySnapshots: Boolean = true) {
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")

    if (isSnapshot && !deploySnapshots) {
        return
    }

    this.maven {
        this.name =
            if (isSnapshot) "$name-snapshots"
            else "$name-releases"

        this.url =
            if (isSnapshot) uri("$url/snapshots")
            else uri("$url/releases")

        this.credentials {
            this.username = System.getenv(username)
            this.password = System.getenv(password)
        }
    }
}

idea {
    project {
        jdkName = "8"
    }
}
