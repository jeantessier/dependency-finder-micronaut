package com.jeantessier.dependencyfinder.micronaut.controllers

import com.jeantessier.dependencyfinder.micronaut.services.DependencyGraph
import com.jeantessier.text.RegularExpressionParser
import com.jeantessier.text.SimpleRegularExpressionParser
import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/api/extract")
class ExtractController {

    private static final logger = LoggerFactory.getLogger(ExtractController)

    @Value('${dependency.finder.extract.source}')
    String source

    @Value('${dependency.finder.extract.filter.includes://}')
    String filterIncludes

    @Value('${dependency.finder.extract.filter.excludes:}')
    String filterExcludes

    final RegularExpressionParser parser = new SimpleRegularExpressionParser()

    final DependencyGraph graph

    def getSources() {
        source.split(/,\s*/) as List
    }

    @Inject
    ExtractController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        [
                extract: [
                        sources: sources,
                        filterIncludes: parser.parseRE(filterIncludes),
                        filterExcludes: parser.parseRE(filterExcludes),
                ],
                graph: graph.stats,
        ]
    }

    @Post
    def extract(@Nullable String label, @Nullable Boolean update) {
        logger.info("POST extract")
        logger.info("    label: {}", label)
        logger.info("    update: {}", update)

        if (graph.stats.extractStart && update) {
            graph.update(sources, filterIncludes, filterExcludes, label)
        } else {
            graph.extract(sources, filterIncludes, filterExcludes, label)
        }

        HttpResponse.temporaryRedirect(new URI("/extract"))
    }

}
