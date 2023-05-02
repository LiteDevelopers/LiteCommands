package dev.rollczi.litecommands.wrapper.implementations;

import dev.rollczi.litecommands.wrapper.ValueToWrap;
import dev.rollczi.litecommands.wrapper.Wrapped;
import dev.rollczi.litecommands.wrapper.WrappedExpectedFactory;
import dev.rollczi.litecommands.wrapper.WrapperFormat;

import java.util.function.Supplier;

public class ValueWrappedExpectedFactory implements WrappedExpectedFactory {

    @Override
    public <EXPECTED> Wrapped<EXPECTED> create(ValueToWrap<EXPECTED> valueToWrap, WrapperFormat<EXPECTED, ?> info) {
        Class<EXPECTED> expectedType = info.getParsedType();

        return new ValueWrapped<>(expectedType, valueToWrap);
    }

    @Override
    public Class<?> getWrapperType() {
        return Void.class;
    }

    private static class ValueWrapped<EXPECTED> implements Wrapped<EXPECTED> {

        private final Class<EXPECTED> expectedType;
        private final Supplier<EXPECTED> expectedSupplier;

        public ValueWrapped(Class<EXPECTED> expectedType, Supplier<EXPECTED> expectedSupplier) {
            this.expectedType = expectedType;
            this.expectedSupplier = expectedSupplier;
        }

        @Override
        public EXPECTED unwrap() {
            return this.expectedSupplier.get();
        }

        @Override
        public Class<EXPECTED> getExpectedType() {
            return this.expectedType;
        }

    }

}
