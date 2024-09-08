package dev.rollczi.litecommands.argument;

import java.util.Objects;

public class ArgumentKey {

    public static final String UNIVERSAL_NAMESPACE = Argument.class.getName();

    private static final ArgumentKey DEFAULT_UNIVERSAL = of("");

    private final String namespace;
    private final String key;

    private ArgumentKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getNamespace() {
        return namespace;
    }

    boolean isDefault() {
        return this.key.isEmpty();
    }

    public ArgumentKey withKey(String key) {
        return new ArgumentKey(this.namespace, key);
    }


    public <A extends Argument<?>> ArgumentKey withNamespace(Class<A> argumentType) {
        return new ArgumentKey(argumentType.getName(), this.key);
    }

    public static ArgumentKey of(String key) {
        return ArgumentKey.typed(Argument.class, key);
    }

    public static ArgumentKey of() {
        return DEFAULT_UNIVERSAL;
    }

    public static <A extends Argument<?>> ArgumentKey typed(Class<A> argumentType, String key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        return new ArgumentKey(argumentType.getName(), key);
    }

    public boolean isUniversal() {
        return this.namespace.equals(UNIVERSAL_NAMESPACE);
    }

    public static <A extends Argument<?>> ArgumentKey typed(Class<A> argumentType) {
        return typed(argumentType, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ArgumentKey that = (ArgumentKey) o;
        return namespace.equals(that.namespace) && key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

}
