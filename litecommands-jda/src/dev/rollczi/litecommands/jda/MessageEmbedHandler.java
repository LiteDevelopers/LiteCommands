package dev.rollczi.litecommands.jda;

import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Optional;

class MessageEmbedHandler implements ResultHandler<User, MessageEmbed> {

    @Override
    public void handle(Invocation<User> invocation, MessageEmbed result, ResultHandlerChain<User> chain) {
        Optional<SlashCommandInteractionEvent> eventOption = invocation.context().get(SlashCommandInteractionEvent.class);

        if (eventOption.isEmpty()) {
            invocation.sender().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(result).queue());
            return;
        }

        SlashCommandInteractionEvent event = eventOption.get();
        event.replyEmbeds(result)
            .setEphemeral(true)
            .queue();
    }

}
