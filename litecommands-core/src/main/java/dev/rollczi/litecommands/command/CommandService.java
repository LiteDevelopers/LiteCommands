package dev.rollczi.litecommands.command;

import dev.rollczi.litecommands.command.section.CommandSection;
import dev.rollczi.litecommands.handle.ExecuteResultHandler;
import dev.rollczi.litecommands.platform.RegistryPlatform;

import java.util.HashMap;
import java.util.Map;

public class CommandService<SENDER> {

    private final RegistryPlatform<SENDER> platform;
    private final Map<String, CommandSection> commands = new HashMap<>();
    private final ExecuteResultHandler<SENDER> handler;

    public CommandService(RegistryPlatform<SENDER> platform, ExecuteResultHandler<SENDER> handler) {
        this.platform = platform;
        this.handler = handler;
    }

    public void register(CommandSection section) {
        commands.put(section.getName(), section);

        for (String label : section.getAliases()) {
            commands.put(label, section);
        }

        platform.registerListener(
                section,
                (sender, invocation) -> {
                    ExecuteResult result = section.execute(invocation, 0);

                    this.handler.handle(sender, invocation, result);
                    return result;
                },
                (sender, invocation) -> section.completion(invocation)
        );
    }

    public void execute(LiteInvocation invocation) {
        CommandSection commandSection = commands.get(invocation.label());

        if (commandSection == null) {
            throw new IllegalArgumentException("Command not found: " + invocation.label());
        }

        commandSection.execute(invocation, 0);
    }

}
