package dev.rollczi.example.bukkit;

import dev.rollczi.example.bukkit.util.ChatUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import dev.rollczi.litecommands.argument.joiner.Joiner;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;

@Section(route = "kick")
@Permission("dev.rollczi.kick")
public class KickCommand {

    @Execute(min = 2)
    public void kickPlayer(Player sender, @Arg @Name("target") Player target, @Joiner String reason) {
        target.kickPlayer(ChatUtil.color(reason));
        Bukkit.getServer().broadcastMessage(ChatUtil.color("&cPlayer &7" + target.getName() + " &chas been kicked!"));
    }

}
