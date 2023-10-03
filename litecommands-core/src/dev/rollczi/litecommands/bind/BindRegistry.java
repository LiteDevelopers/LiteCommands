package dev.rollczi.litecommands.bind;

import dev.rollczi.litecommands.util.MapUtil;
import panda.std.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class BindRegistry {

    private final Map<Class<?>, Supplier<?>> instanceBindings = new HashMap<>();

    public <T> void bindInstance(Class<T> on, Supplier<T> bind) {
        this.instanceBindings.put(on, bind);
    }

    public void bindInstanceUnsafe(Class<?> on, Supplier<?> bind) {
        this.instanceBindings.put(on, bind);
    }

    @SuppressWarnings("unchecked")
    public <T> Result<T, String> getInstance(Class<T> clazz) {
        Optional<Supplier<?>> option = MapUtil.findByInstanceOf(clazz, this.instanceBindings);

        if (option.isPresent()) {
            Supplier<T> supplier = (Supplier<T>) option.get();

            return Result.ok(supplier.get());
        }

        return Result.error("Cannot find binding for " + clazz.getName());
    }

}
