package micronaut.depfind.test

import com.jeantessier.dependency.GraphSummarizer
import com.jeantessier.dependency.JSONPrinter
import com.jeantessier.dependency.RegularExpressionSelectionCriteria
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/closure")
class ClosureController {

    private static final logger = LoggerFactory.getLogger(ClosureController)

    final DependencyGraph graph

    @Inject
    ClosureController(DependencyGraph graph) {
        this.graph = graph
    }

    @Introspected
    @Serdeable.Deserializable
    static class ClosureCommand {
        def startIncludes = []
        def startExcludes = []

        def stopIncludes = []
        def stopExcludes = []

        def maximumInboundDepth = ""
        def maximumOutboundDepth = ""

        def scope = "feature"
        def filter = "feature"
    }

    @Post
    def index(@Body ClosureCommand command) {
        logger.info("POST closure")
        logger.info("    start:  includes: \"{}\", excludes: \"{}\"", command.startIncludes, command.startExcludes)
        logger.info("    stop: includes: \"{}\", excludes: \"{}\"", command.stopIncludes, command.stopExcludes)
        logger.info("    depth: max inbounds: {}, max outbounds: {}", command.maximumInboundDepth, command.maximumOutboundDepth)
        logger.info("    scope: {}", command.scope)
        logger.info("    filter: {}", command.filter)

        def dependenciesClosure = graph.closure(
                command.startIncludes,
                command.startExcludes,
                command.stopIncludes,
                command.stopExcludes,
                command.maximumInboundDepth,
                command.maximumOutboundDepth,
        )

        def scopeCriteria  = new RegularExpressionSelectionCriteria()
        scopeCriteria.matchingPackages = "package" == command.scope
        scopeCriteria.matchingClasses = "class" == command.scope
        scopeCriteria.matchingFeatures = "feature" == command.scope
        scopeCriteria.globalIncludes = "//"

        def filterCriteria = new RegularExpressionSelectionCriteria()
        filterCriteria.matchingPackages = "package" == command.filter
        filterCriteria.matchingClasses = "class" == command.filter
        filterCriteria.matchingFeatures = "feature" == command.filter
        filterCriteria.globalIncludes = "//"

        def summarizer = new GraphSummarizer(scopeCriteria, filterCriteria);
        summarizer.traverseNodes(dependenciesClosure.factory.packages.values());

        def out = new StringWriter()
        def printer = new JSONPrinter(new PrintWriter(out))

        printer.traverseNodes(summarizer.scopeFactory.packages.values())

        out.toString()
    }

}
