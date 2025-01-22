package micronaut.depfind.test

import com.jeantessier.dependency.JSONCyclePrinter
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/cycles")
class CyclesController {

    private static final logger = LoggerFactory.getLogger(CyclesController)

    final DependencyGraph graph

    @Inject
    CyclesController(DependencyGraph graph) {
        this.graph = graph
    }

    @Introspected
    @Serdeable.Deserializable
    static class CyclesCommand {
        def packageScope = false
        def classScope = false
        def featureScope = false
        def scopeIncludes = []
        def scopeExcludes = []

        def maximumCycleLength = ""
    }

    @Post
    def index(@Body CyclesCommand command) {
        logger.info("POST cycles")
        logger.info("    scope:  package: {}, class: {}, feature: {}, includes: \"{}\", excludes: \"{}\"", command.packageScope, command.classScope, command.featureScope, command.scopeIncludes, command.scopeExcludes)
        logger.info("    length: max cycle length: {}", command.maximumCycleLength)

        def cycles = graph.cycles(
                command.packageScope,
                command.classScope,
                command.featureScope,
                command.scopeIncludes,
                command.scopeExcludes,
                command.maximumCycleLength,
        )

        def out = new StringWriter()
        def printer = new JSONCyclePrinter(new PrintWriter(out))

        printer.visitCycles(cycles)

        out.toString()
    }

}
