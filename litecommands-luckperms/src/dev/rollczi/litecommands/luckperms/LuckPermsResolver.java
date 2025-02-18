package dev.rollczi.litecommands.luckperms;

import dev.rollczi.litecommands.settings.PermissionResolver;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

public class LuckPermsResolver<SENDER> implements PermissionResolver<SENDER> {
    public static final UUID CONSOLE = new UUID(0, 0);

    private final Function<SENDER, UUID> uuidFunction;
    private final Supplier<UserManager> getUserManager = () -> LuckPermsProvider.get().getUserManager();

    public LuckPermsResolver(Function<SENDER, UUID> uuidFunction) {
        this.uuidFunction = uuidFunction;
    }

    @Override
    public boolean hasPermission(SENDER sender, String permission) {
        UUID uuid = uuidFunction.apply(sender);
        if (uuid == null) {
            return false;
        }
        if (CONSOLE.equals(uuid)) {
            return true;
        }
        User user;
        UserManager userManager = this.getUserManager.get();
        if (userManager.isLoaded(uuid)) {
            user = userManager.getUser(uuid);
        } else {
            try {
                user = userManager.loadUser(uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }
        if (user == null) {
            return false;
        }
        return user.getCachedData().getPermissionData(QueryOptions.nonContextual()).checkPermission(permission).asBoolean();
    }
}
