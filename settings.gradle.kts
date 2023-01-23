rootProject.name = "LiteCommands"

include(":litecommands-core")
include(":litecommands-velocity")
include(":litecommands-bukkit")
include(":litecommands-bukkit-adventure")
include(":litecommands-bungee")
include(":litecommands-minestom", JavaVersion.VERSION_17)
include(":examples:bukkit")

fun include(projectPath: String, version: JavaVersion) {
    if (JavaVersion.current().isCompatibleWith(version)) {
        include(projectPath)
    }
}
