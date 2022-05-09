package dev.rollczi.litecommands.implementation;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.OneArgument;
import dev.rollczi.litecommands.argument.block.Block;
import dev.rollczi.litecommands.argument.block.BlockArgument;
import dev.rollczi.litecommands.argument.flag.Flag;
import dev.rollczi.litecommands.argument.flag.FlagArgument;
import dev.rollczi.litecommands.argument.joiner.Joiner;
import dev.rollczi.litecommands.argument.joiner.JoinerArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.amount.Between;
import dev.rollczi.litecommands.command.amount.Max;
import dev.rollczi.litecommands.command.amount.Min;
import dev.rollczi.litecommands.command.amount.Required;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.ExecutedPermissions;
import dev.rollczi.litecommands.command.permission.Permissions;
import dev.rollczi.litecommands.command.section.Section;
import panda.std.Result;

public final class LiteFactory {

    private LiteFactory() {
    }

    public static <SENDER> LiteCommandsBuilder<SENDER> builder(Class<SENDER> senderClass) {
        return LiteCommandsBuilderImpl.<SENDER>builder()
                .configureFactory(factory -> {
                    factory.annotationResolver(Section.RESOLVER);
                    factory.annotationResolver(Execute.RESOLVER);
                    factory.annotationResolver(Permissions.RESOLVER);
                    factory.annotationResolver(ExecutedPermissions.RESOLVER);
                    factory.annotationResolver(Min.RESOLVER);
                    factory.annotationResolver(Max.RESOLVER);
                    factory.annotationResolver(Required.RESOLVER);
                    factory.annotationResolver(Between.RESOLVER);

                    factory.argument(Arg.class, String.class, (OneArgument<String>) (invocation, argument) -> argument);
                    factory.argument(Flag.class, boolean.class, new FlagArgument());
                    factory.argument(Joiner.class, String.class, new JoinerArgument());
                    factory.argument(Block.class, Object.class, new BlockArgument());
                })
                .contextualBind(LiteInvocation.class, (sender, invocation) -> Result.ok(invocation))
                .contextualBind(senderClass, (sender, invocation) -> Result.ok(sender));
    }

}
