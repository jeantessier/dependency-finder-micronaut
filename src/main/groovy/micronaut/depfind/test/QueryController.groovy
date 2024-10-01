package micronaut.depfind.test

import com.jeantessier.dependency.JSONPrinter
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject

@Controller("/query")
class QueryController {

    final DependencyGraph graph

    @Inject
    QueryController(DependencyGraph graph) {
        this.graph = graph
    }

    @Introspected
    @Serdeable.Deserializable
    static class QueryCommand {
        def packageScope = false
        def classScope = false
        def featureScope = false
        def scopeIncludes = []
        def scopeExcludes = []

        def packageFilter = false
        def classFilter = false
        def featureFilter = false
        def filterIncludes = []
        def filterExcludes = []

        def showInbounds = false
        def showOutbounds = false
        def showEmptyNodes = false
    }

    @Post
    def index(@Body QueryCommand command) {
        def dependenciesQuery = graph.query(
                command.packageScope,
                command.classScope,
                command.featureScope,
                command.scopeIncludes,
                command.scopeExcludes,
                command.packageFilter,
                command.classFilter,
                command.featureFilter,
                command.filterIncludes,
                command.filterExcludes,
        )

        def out = new StringWriter()
        def printer = new JSONPrinter(new PrintWriter(out))
        printer.showInbounds = command.showInbounds
        printer.showOutbounds = command.showOutbounds
        printer.showEmptyNodes = command.showEmptyNodes

        printer.traverseNodes(dependenciesQuery.scopeFactory.packages.values())

        out.toString()
    }

}
