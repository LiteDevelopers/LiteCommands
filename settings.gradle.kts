rootProject.name = "LiteCommands"

// core
include(":litecommands-core")
include(":litecommands-core-annotations")

// adventure
include(":litecommands-adventure")

// platforms
include(":litecommands-velocity")
include(":litecommands-bungee")
include(":litecommands-bukkit")
include(":litecommands-bukkit-adventure-platform")
include(":litecommands-paper", JavaVersion.VERSION_17)
include(":litecommands-minestom", JavaVersion.VERSION_17)

// examples
include(":examples:bukkit")

fun include(projectPath: String, version: JavaVersion) {
    if (!JavaVersion.current().isCompatibleWith(version)) {
        println("Skipping $projectPath because of incompatible Java version, required: $version")
        return
    }

    include(projectPath)
}
