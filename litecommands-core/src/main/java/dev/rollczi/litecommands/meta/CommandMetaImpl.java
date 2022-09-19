package dev.rollczi.litecommands.meta;

import dev.rollczi.litecommands.command.amount.AmountValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

class CommandMetaImpl extends MetaData implements CommandMeta {

    private final Set<String> permissions = new HashSet<>();
    private final Set<String> excludedPermissions = new HashSet<>();
    private AmountValidator amountValidator = AmountValidator.NONE;

    @Override
    public CommandMetaImpl addPermission(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    @Override
    public CommandMetaImpl addPermission(Collection<String> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    @Override
    public CommandMetaImpl removePermission(String... permissions) {
        Arrays.asList(permissions).forEach(this.permissions::remove);
        return this;
    }

    @Override
    public CommandMetaImpl removePermission(Collection<String> permissions) {
        this.permissions.removeAll(permissions);
        return this;
    }

    @Override
    public CommandMetaImpl clearPermissions() {
        this.permissions.clear();
        return this;
    }

    @Override
    public Collection<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    @Override
    public CommandMetaImpl addExcludedPermission(String... permissions) {
        this.excludedPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    @Override
    public CommandMetaImpl addExcludedPermission(Collection<String> permissions) {
        this.excludedPermissions.addAll(permissions);
        return this;
    }

    @Override
    public CommandMetaImpl removeExcludedPermission(String... permissions) {
        Arrays.asList(permissions).forEach(this.excludedPermissions::remove);
        return this;
    }

    @Override
    public CommandMetaImpl removeExcludedPermission(Collection<String> permissions) {
        this.excludedPermissions.removeAll(permissions);
        return this;
    }

    @Override
    public CommandMetaImpl clearExcludedPermissions() {
        this.excludedPermissions.clear();
        return this;
    }

    @Override
    public Collection<String> getExcludedPermissions() {
        return Collections.unmodifiableSet(excludedPermissions);
    }

    @Override
    public CommandMetaImpl setAmountValidator(AmountValidator validator) {
        this.amountValidator = validator;
        return this;
    }

    @Override
    public CommandMetaImpl applyAmountValidator(Function<AmountValidator, AmountValidator> edit) {
        this.amountValidator = edit.apply(this.amountValidator);
        return this;
    }

    @Override
    public AmountValidator getAmountValidator() {
        return this.amountValidator;
    }

    @Override
    public <T> CommandMetaImpl set(MetaKey<T> key, T value) {
        super.set(key, value);
        return this;
    }

    @Override
    public CommandMetaImpl apply(Meta meta) {
        super.apply(meta);
        return this;
    }

    @Override
    public CommandMetaImpl applyCommandMeta(CommandMeta meta) {
        this.amountValidator = this.amountValidator.and(meta.getAmountValidator());
        this.permissions.addAll(meta.getPermissions());
        this.excludedPermissions.addAll(meta.getExcludedPermissions());
        this.apply(meta);
        return this;
    }

}
