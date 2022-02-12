package dev.rollczi.litecommands

import dev.rollczi.litecommands.annotations.parser.LiteAnnotationParser
import dev.rollczi.litecommands.component.LiteComponent
import dev.rollczi.litecommands.component.LiteComponentFactory
import org.panda_lang.utilities.inject.DependencyInjection
import org.panda_lang.utilities.inject.Injector
import java.util.function.BiFunction
import java.util.logging.Logger

open class LiteCommandsSpec {

    protected var contextCreator : BiFunction<String, Array<String>, LiteComponent.ContextOfResolving> = BiFunction {
            command, args -> LiteComponent.ContextOfResolving.create(LiteInvocation(command,
        LiteTestSender(), args))
    }

    protected var injector: Injector = DependencyInjection.createInjector()
    protected var factory = LiteComponentFactory(
        Logger.getLogger("LiteCommandsSpec"),
        injector,
        LiteAnnotationParser(mapOf(Pair(
            LiteTestSender::class.java,
            setOf(LiteTestSenderArgument())
        )))
    )

}
