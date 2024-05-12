package dev.rollczi.litecommands.handler.result.standard;

import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;

public class ArrayHandler<SENDER> implements ResultHandler<SENDER, Object[]> {

    @Override
    public void handle(Invocation<SENDER> invocation, Object[] result, ResultHandlerChain<SENDER> chain) {
        for (Object object : result) {
            chain.resolve(invocation, object);
        }
    }

}
