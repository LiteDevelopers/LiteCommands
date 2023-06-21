package dev.rollczi.litecommands.wrapper.std;

import dev.rollczi.litecommands.wrapper.WrapFormat;
import dev.rollczi.litecommands.wrapper.ValueToWrap;
import dev.rollczi.litecommands.wrapper.Wrap;
import dev.rollczi.litecommands.wrapper.Wrapper;

import java.util.Optional;
import java.util.function.Supplier;

abstract class AbstractWrapper<WRAPPER> implements Wrapper {

    private final Class<WRAPPER> wrapperType;

    protected AbstractWrapper(Class<WRAPPER> wrapperType) {
        this.wrapperType = wrapperType;
    }

    @Override
    public Class<WRAPPER> getWrapperType() {
        return this.wrapperType;
    }

    @Override
    public final <EXPECTED> Wrap<EXPECTED> create(EXPECTED valueToWrap, WrapFormat<EXPECTED, ?> info) {
        this.check(info);

        return new TypeSafeWrap<>(info.getParsedType(), this.wrapValue(valueToWrap, info));
    }

    protected abstract <EXPECTED> Supplier<WRAPPER> wrapValue(EXPECTED valueToWrap, WrapFormat<EXPECTED, ?> info);

    @Override
    public final <EXPECTED> Wrap<EXPECTED> createEmpty(WrapFormat<EXPECTED, ?> info) {
        this.check(info);

        return new TypeSafeWrap<>(info.getParsedType(), this.emptyValue(info));
    }

    protected <EXPECTED> Supplier<WRAPPER> emptyValue(WrapFormat<EXPECTED, ?> info) {
        throw new UnsupportedOperationException("Cannot create empty value for " + wrapperType);
    }

    private void check(WrapFormat<?, ?> info) {
        if (wrapperType == Object.class && !info.hasOutType()) {
            return;
        }
        
        if (!info.hasOutType() || !info.getOutType().equals(wrapperType)) {
            throw new IllegalArgumentException("Wrapper type mismatch");
        }
    }

    private class TypeSafeWrap<EXPECTED> implements Wrap<EXPECTED> {

        private final Class<EXPECTED> expectedType;
        private final Supplier<WRAPPER> wrapperSupplier;

        TypeSafeWrap(Class<EXPECTED> expectedType, Supplier<WRAPPER> wrapperSupplier) {
            this.expectedType = expectedType;
            this.wrapperSupplier = wrapperSupplier;
        }

        @Override
        public WRAPPER unwrap() {
            return wrapperSupplier.get();
        }

        @Override
        public Class<EXPECTED> getExpectedType() {
            return expectedType;
        }

    }

}
