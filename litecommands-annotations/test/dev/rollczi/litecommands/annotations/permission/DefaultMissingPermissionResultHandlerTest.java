package dev.rollczi.litecommands.annotations.permission;

import dev.rollczi.litecommands.unit.annotations.LiteTestSpec;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultMissingPermissionResultHandlerTest extends LiteTestSpec {

    static LiteTestConfig config = builder -> builder
        .message(LiteMessages.MISSING_PERMISSIONS, Message.of(missingPermissions -> "missing permissions " + missingPermissions.asJoinedText(", ")))
        .result(String.class, (invocation, result, chain) -> invocation.sender().sendMessage(result));

    @Command(name = "test")
    @Permission("test.permission")
    static class TestCommand {

        @Execute
        @Permission("test.permission.2")
        void execute() {}

    }

    @Test
    @DisplayName("Test missing permission handler")
    void test() {
        platform.execute("test")
            .assertMessage("missing permissions test.permission.2, test.permission");
    }

}
