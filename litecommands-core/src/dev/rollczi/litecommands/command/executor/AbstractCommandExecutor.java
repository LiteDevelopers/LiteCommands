package dev.rollczi.litecommands.command.executor;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.requirement.BindRequirement;
import dev.rollczi.litecommands.requirement.ContextRequirement;
import dev.rollczi.litecommands.meta.Meta;
import dev.rollczi.litecommands.meta.MetaHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommandExecutor<SENDER> implements CommandExecutor<SENDER> {

    protected final CommandRoute<SENDER> parent;
    protected final List<Argument<?>> arguments = new ArrayList<>();
    protected final List<ContextRequirement<?>> contextRequirements = new ArrayList<>();
    protected final List<BindRequirement<?>> bindRequirements = new ArrayList<>();
    protected final Meta meta = Meta.create();

    protected AbstractCommandExecutor(
        CommandRoute<SENDER> parent,
        Collection<Argument<?>> arguments,
        Collection<ContextRequirement<?>> contextRequirements,
        Collection<BindRequirement<?>> bindRequirements
    ) {
        this.parent = parent;
        this.arguments.addAll(arguments);
        this.contextRequirements.addAll(contextRequirements);
        this.bindRequirements.addAll(bindRequirements);
    }

    @Override
    public Meta meta() {
        return meta;
    }

    @Override
    public MetaHolder parentMeta() {
        return parent;
    }

    @Override
    public CommandRoute<SENDER> getParent() {
        return parent;
    }

    @Override
    public List<Argument<?>> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public List<ContextRequirement<?>> getContextRequirements() {
        return Collections.unmodifiableList(contextRequirements);
    }

    @Override
    public List<BindRequirement<?>> getBindRequirements() {
        return Collections.unmodifiableList(bindRequirements);
    }

}
