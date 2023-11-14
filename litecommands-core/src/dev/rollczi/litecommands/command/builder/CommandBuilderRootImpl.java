package dev.rollczi.litecommands.command.builder;

import dev.rollczi.litecommands.command.CommandExecutorProvider;
import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.meta.Meta;
import dev.rollczi.litecommands.meta.MetaHolder;
import dev.rollczi.litecommands.util.StringUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CommandBuilderRootImpl<SENDER> extends CommandBuilderChildrenBase<SENDER> implements CommandBuilder<SENDER> {

    private final Meta meta = Meta.create();
    private final Map<String, Meta> childrenMeta = new HashMap<>();

    @Override
    public @NotNull CommandBuilder<SENDER> name(String name) {
        throw new UnsupportedOperationException("Cannot set name for root command");
    }

    @Override
    public String name() {
        return StringUtil.EMPTY;
    }

    @Override
    public @NotNull CommandBuilder<SENDER> aliases(List<String> aliases) {
        throw new UnsupportedOperationException("Cannot set aliases for root command");
    }

    @Override
    public @NotNull CommandBuilder<SENDER> aliases(String... aliases) {
        throw new UnsupportedOperationException("Cannot set aliases for root command");
    }

    @Override
    public boolean isNameOrAlias(String name) {
        return name.isEmpty();
    }

    @Override
    public boolean hasSimilarNames(CommandBuilder<SENDER> context) {
        return context.name().isEmpty();
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> names() {
        return Collections.singletonList(this.name());
    }

    @Override
    public @NotNull CommandBuilder<SENDER> enable() {
        throw new UnsupportedOperationException("Cannot enable root command");
    }

    @Override
    public @NotNull CommandBuilder<SENDER> enabled(boolean enabled) {
        throw new UnsupportedOperationException("Cannot enable root command");
    }

    @Override
    public @NotNull CommandBuilder<SENDER> disable() {
        throw new UnsupportedOperationException("Cannot disable root command");
    }

    @Override
    public CommandBuilder<SENDER> appendExecutor(CommandExecutorProvider<SENDER> executor) {
        throw new UnsupportedOperationException("Cannot append executor to root command");
    }

    @Override
    public Collection<CommandExecutorProvider<SENDER>> executors() {
        throw new UnsupportedOperationException("Cannot get executors from root command");
    }

    @Override
    public CommandBuilder<SENDER> applyMeta(UnaryOperator<Meta> operator) {
        throw new UnsupportedOperationException("Cannot apply meta to root command");
    }

    @Override
    public Meta meta() {
        return meta;
    }

    @Override
    public @Nullable MetaHolder parentMeta() {
        return null;
    }

    @Override
    public CommandBuilder<SENDER> routeName(String name) {
        throw new UnsupportedOperationException("Cannot set name for root command");
    }

    @Override
    public CommandBuilder<SENDER> routeAliases(List<String> aliases) {
        throw new UnsupportedOperationException("Cannot set aliases for root command");
    }

    @Override
    public CommandBuilder<SENDER> applyOnRoute(UnaryOperator<CommandBuilder<SENDER>> apply) {
        throw new UnsupportedOperationException("Cannot apply on route for root command");
    }

    @Override
    public CommandBuilder<SENDER> getRealRoute() {
        return this;
    }

    @Override
    public CommandBuilder<SENDER> shortRoutes(List<String> aliases) {
        throw new UnsupportedOperationException("Cannot set short aliases for root command");
    }

    @Override
    public void meagre(CommandBuilder<SENDER> context) {
        for (CommandBuilder<SENDER> child : context.children()) {
            if (children.containsKey(child.name())) {
                children.get(child.name()).meagre(child);

                if (childrenMeta.containsKey(child.name())) {
                    childrenMeta.get(child.name()).apply(context.meta());
                }
                else {
                    childrenMeta.put(child.name(), context.meta().copy().apply(this.meta));
                }

            }
            else {
                children.put(child.name(), child);
                childrenMeta.put(child.name(), context.meta().copy());
            }


        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean buildable() {
        return true;
    }

    @Override
    public @Nullable CommandBuilder<SENDER> parent() {
        return null;
    }

    @Override
    public Collection<CommandRoute<SENDER>> build(CommandRoute<SENDER> parent) {
        return this.children.values().stream()
            .map(senderCommandEditorContext -> senderCommandEditorContext.build(parent))
            .flatMap(Collection::stream)
            .peek(route -> route.meta().apply(this.childrenMeta.getOrDefault(route.getName(), this.meta)))
            .collect(Collectors.toList());
    }

}
