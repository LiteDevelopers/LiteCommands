package dev.rollczi.example.bukkit;

import dev.rollczi.example.bukkit.argument.GameModeArgument;
import dev.rollczi.example.bukkit.argument.LocationArgument;
import dev.rollczi.example.bukkit.argument.WorldArgument;
import dev.rollczi.example.bukkit.command.ConvertCommand;
import dev.rollczi.example.bukkit.command.KickCommand;
import dev.rollczi.example.bukkit.command.TeleportCommand;
import dev.rollczi.example.bukkit.handler.InvalidUsage;
import dev.rollczi.example.bukkit.handler.PermissionMessage;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "example-plugin")
            // Arguments
            .argumentMultilevel(Location.class, new LocationArgument())
            .argument(World.class, new WorldArgument(this.getServer()))
            .argument(GameMode.class, new GameModeArgument())
            .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), "&cNie ma takiego gracza!"))

            // Contextual Bind
            .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("&cKomenda tylko dla gracza!"))

            // Commands
            .command(TeleportCommand.class, KickCommand.class, ConvertCommand.class)

            // Handlers
            .invalidUsageHandler(new InvalidUsage())
            .permissionHandler(new PermissionMessage())

            .register();
    }

    @Override
    public void onDisable() {
        this.liteCommands.getPlatform().unregisterAll();
    }

}
