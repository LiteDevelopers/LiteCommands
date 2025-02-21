plugins {
    `litecommands-java-17`
    `litecommands-repositories`
    `litecommands-publish`
    id("fabric-loom") version "1.10.1"
}

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    // LiteCommands
    api(project(":litecommands-framework"))
    include(project(":litecommands-framework"))
    include(project(":litecommands-core"))
    include(project(":litecommands-annotations"))
    include(project(":litecommands-programmatic"))

    // Minecraft and mappings
    minecraft("com.mojang:minecraft:${Versions.FABRIC_MINECRAFT}")
    mappings("net.fabricmc:yarn:${Versions.FABRIC_YARN_MAPPINGS}:v2")

    // Fabric loader and API
    modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
    modImplementation("net.fabricmc.fabric-api:fabric-command-api-v2:${Versions.FABRIC_COMMAND_API_V2}")
    modImplementation("net.fabricmc.fabric-api:fabric-command-api-v1:${Versions.FABRIC_COMMAND_API_V1}")
    modImplementation("net.fabricmc.fabric-api:fabric-lifecycle-events-v1:${Versions.FABRIC_LIFECYCLE_EVENTS_V1}")
}

litecommandsPublish {
    artifactId = "litecommands-fabric"
}