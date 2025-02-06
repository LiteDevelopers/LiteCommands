package dev.rollczi.example.fabric.server.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.quoted.Quoted;
import dev.rollczi.litecommands.platform.PlatformSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

@Command(name = "example", aliases = "tutorial")
public class ExampleCommand {
    @Execute(name = "kick")
    void sendMessage(@Arg("player") ServerPlayerEntity player, @Join("reason") String reason) {
        player.networkHandler.disconnect(Text.of(reason));
    }

    @Execute(name = "message", aliases = "msg")
    Text sendMessage(@Quoted @Arg String message) {
        return Text.of("You saied: " + message);
    }

    @Execute(name = "thread1")
    String thread1() {
        return Thread.currentThread().getName();
    }

    @Execute(name = "thread2")
    @Async
    String thread2() {
        return Thread.currentThread().getName();
    }

    @Execute(name = "testPermission")
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "3.12.0")
    String testPermission(@Sender PlatformSender sender, @Arg String permission) {
        return permission + ": " + sender.hasPermission(permission);
    }
}
