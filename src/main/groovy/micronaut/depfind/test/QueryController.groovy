package micronaut.depfind.test

import com.jeantessier.dependency.JSONPrinter
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpParameters
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
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

    @Get
    def get(HttpParameters parameters) {
        def dependenciesQuery = graph.query(
                parameters.get("packageScope"),
                parameters.get("classScope"),
                parameters.get("featureScope"),
                parameters.getAll("scopeIncludes"),
                parameters.getAll("scopeExcludes"),
                parameters.get("packageFilter"),
                parameters.get("classFilter"),
                parameters.get("featureFilter"),
                parameters.getAll("filterIncludes"),
                parameters.getAll("filterExcludes"),
        )

        renderQuery(dependenciesQuery, parameters.get("showInbounds"), parameters.get("showOutbounds"), parameters.get("showEmptyNodes"))
    }

    @Introspected
    @Serdeable.Deserializable
    static class QueryCommand {
        def packageScope = false
        def classScope = false
        def featureScope = false
        def scopeIncludes = ["//"]
        def scopeExcludes = []

        def packageFilter = false
        def classFilter = false
        def featureFilter = false
        def filterIncludes = ["//"]
        def filterExcludes = []

        def showInbounds = "on"
        def showOutbounds = "on"
        def showEmptyNodes = "on"
    }

    @Post
    def post(@Body QueryCommand command) {
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

        renderQuery(dependenciesQuery, command.showInbounds, command.showOutbounds, command.showEmptyNodes)
    }

    def renderQuery(dependenciesQuery, showInbounds, showOutbounds, showEmptyNodes) {
        def out = new StringWriter()
        def printer = new JSONPrinter(new PrintWriter(out));

        if (showInbounds != null || showOutbounds != null || showEmptyNodes != null) {
            printer.showInbounds = showInbounds == "on" || Boolean.valueOf(showInbounds)
            printer.showOutbounds = showOutbounds == "on" || Boolean.valueOf(showOutbounds)
            printer.showEmptyNodes = showEmptyNodes == "on" || Boolean.valueOf(showEmptyNodes)
        }

        printer.traverseNodes(dependenciesQuery.getScopeFactory().getPackages().values());

        out.toString()
    }

}
