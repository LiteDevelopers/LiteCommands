plugins {
    id("java")
    id("fabric-loom") version "1.5-SNAPSHOT"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://repo.panda-lang.org/releases/")
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.4")
    mappings("net.fabricmc:yarn:1.20.4+build.3:v2")

    modImplementation("net.fabricmc:fabric-loader:0.15.6")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.96.1+1.20.4")


    // modImplementation("dev.rollczi:litecommands-minestom:3.3.4+minecraft_version") // <-- uncomment in your project
    modImplementation(project(":litecommands-fabric")){
        isTransitive = true
    }

}

tasks.ideaSyncTask {
    dependsOn(":litecommands-fabric:build")
}

sourceSets.test {
    java.setSrcDirs(emptyList<String>())
    resources.setSrcDirs(emptyList<String>())
}
