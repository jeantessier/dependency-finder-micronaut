package com.jeantessier.dependencyfinder.micronaut.controllers

import com.jeantessier.dependency.JSONPrinter
import com.jeantessier.dependencyfinder.micronaut.services.DependencyGraph
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/api/query")
class QueryController {

    private static final logger = LoggerFactory.getLogger(QueryController)

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
        logger.info("POST query")
        logger.info("    scope:  package: {}, class: {}, feature: {}, includes: \"{}\", excludes: \"{}\"", command.packageScope, command.classScope, command.featureScope, command.scopeIncludes, command.scopeExcludes)
        logger.info("    filter: package: {}, class: {}, feature: {}, includes: \"{}\", excludes: \"{}\"", command.packageFilter, command.classFilter, command.featureFilter, command.filterIncludes, command.filterExcludes)
        logger.info("    show: inbounds: {}, outbounds: {}, empty nodes: {}", command.showInbounds, command.showOutbounds, command.showEmptyNodes)

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
