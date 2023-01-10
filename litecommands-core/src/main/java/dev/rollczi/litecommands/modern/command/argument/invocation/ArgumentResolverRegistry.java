package dev.rollczi.litecommands.modern.command.argument.invocation;

import dev.rollczi.litecommands.modern.command.argument.ArgumentContext;
import dev.rollczi.litecommands.modern.command.argument.ArgumentKey;

import java.util.Optional;

public interface ArgumentResolverRegistry<SENDER> {

    <DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> void registerResolver(
        IndexKey<DETERMINANT, EXPECTED, CONTEXT> indexKey,
        ArgumentResolver<SENDER, DETERMINANT, EXPECTED, ? extends ArgumentContext<DETERMINANT, EXPECTED>> resolver
    );

    <DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> Optional<ArgumentResolver<SENDER, DETERMINANT, EXPECTED, CONTEXT>> getResolver(
        IndexKey<DETERMINANT, EXPECTED, CONTEXT> indexKey
    );

    class IndexKey<DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> {

        private final Class<DETERMINANT> determinantType;
        private final Class<EXPECTED> expectedType;
        private final Class<CONTEXT> contextType;
        private final ArgumentKey argumentKey;

        private IndexKey(Class<DETERMINANT> determinantType, Class<EXPECTED> expectedType, Class<CONTEXT> contextType, ArgumentKey argumentKey) {
            this.determinantType = determinantType;
            this.expectedType = expectedType;
            this.contextType = contextType;
            this.argumentKey = argumentKey;
        }

        public ArgumentKey argumentKey() {
            return argumentKey;
        }

        public Class<DETERMINANT> determinantType() {
            return determinantType;
        }

        public Class<EXPECTED> expectedType() {
            return expectedType;
        }

        public Class<CONTEXT> contextType() {
            return contextType;
        }


        public boolean isUniversal() {
            return ArgumentContext.class.equals(contextType) && Object.class.equals(determinantType);
        }

        public static <DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> IndexKey<DETERMINANT, EXPECTED, CONTEXT> from(CONTEXT context, ArgumentKey argumentKey) {
            return of(context.getDeterminantType(), context.getExpectedType(), context.getClass(), argumentKey);
        }

        public static <DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> IndexKey<DETERMINANT, EXPECTED, CONTEXT> from(CONTEXT context) {
            return of(context.getDeterminantType(), context.getExpectedType(), context.getClass(), ArgumentKey.DEFAULT);
        }

        public static <EXPECTED> IndexKey<Object, EXPECTED, ArgumentContext<Object, EXPECTED>> universal(
            Class<EXPECTED> expectedType
        ) {
            return IndexKey.of(Object.class, expectedType, ArgumentContext.class, ArgumentKey.DEFAULT);
        }

        public static <EXPECTED> IndexKey<Object, EXPECTED, ArgumentContext<Object, EXPECTED>> universal(
            Class<EXPECTED> expectedType,
            ArgumentKey argumentKey
        ) {
            return IndexKey.of(Object.class, expectedType, ArgumentContext.class, argumentKey);
        }

        public static <DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> IndexKey<DETERMINANT, EXPECTED, CONTEXT> of(
            Class<DETERMINANT> determinantType,
            Class<EXPECTED> expectedType,
            Class<?> contextType
        ) {
            return of(determinantType, expectedType, contextType, ArgumentKey.DEFAULT);
        }

        @SuppressWarnings("unchecked")
        public static <DETERMINANT, EXPECTED, CONTEXT extends ArgumentContext<DETERMINANT, EXPECTED>> IndexKey<DETERMINANT, EXPECTED, CONTEXT> of(
            Class<DETERMINANT> determinantType,
            Class<EXPECTED> expectedType,
            Class<?> contextType,
            ArgumentKey argumentKey
        ) {
            return new IndexKey<>(determinantType, expectedType, (Class<CONTEXT>) contextType, argumentKey);
        }

    }

}
