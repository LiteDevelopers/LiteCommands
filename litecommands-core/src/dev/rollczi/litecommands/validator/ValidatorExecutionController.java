package dev.rollczi.litecommands.validator;

import dev.rollczi.litecommands.command.executor.event.CommandPreExecutionEvent;
import dev.rollczi.litecommands.event.EventListener;
import dev.rollczi.litecommands.event.Subscriber;
import dev.rollczi.litecommands.flow.Flow;

public class ValidatorExecutionController<SENDER> implements EventListener {

    private final ValidatorService<SENDER> validatorService;

    public ValidatorExecutionController(ValidatorService<SENDER> validatorService) {
        this.validatorService = validatorService;
    }

    @Subscriber
    public void onEvent(CommandPreExecutionEvent<SENDER> event) {
        Flow flow = this.validatorService.validate(event.getInvocation(), event.getExecutor());

        if (flow.isTerminate()) {
            event.stopFlow(flow.failedReason());
        }

        if (flow.isStopCurrent()) {
            event.skipFlow(flow.failedReason());
        }
    }

}
