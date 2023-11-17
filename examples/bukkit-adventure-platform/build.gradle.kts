import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.2.0"
}

version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.panda-lang.org/releases/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")

    // implementation("dev.rollczi:litecommands-bukkit:3.0.3") // <-- uncomment in your project
    // implementation("dev.rollczi:litecommands-adventure-platform:3.0.3") // <-- uncomment in your project
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    implementation(project(":litecommands-bukkit")) // don't use this line in your build.gradle
    implementation(project(":litecommands-adventure-platform")) // don't use this line in your build.gradle
}

val pluginName = "ExampleAdventurePlugin"
val packageName = "dev.rollczi.example.bukkit.adventure"

bukkit {
    main = "$packageName.$pluginName"
    apiVersion = "1.13"
    author = "Rollczi"
    name = pluginName
    version = "${project.version}"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("$packageName v${project.version}.jar")

    listOf(
        "panda",
        "org.panda_lang",
        "dev.rollczi.litecommands",
        "net.kyori",
    ).forEach { relocate(it, "$packageName.libs.$it") }
}

sourceSets.test {
    java.setSrcDirs(emptyList<String>())
    resources.setSrcDirs(emptyList<String>())
}

tasks.runServer {
    minecraftVersion("1.20.2")
}
