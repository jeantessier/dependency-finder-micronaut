package micronaut.depfind.test

import com.jeantessier.text.RegularExpressionParser
import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/extract")
class ExtractController {

    private static final logger = LoggerFactory.getLogger(ExtractController)

    @Value('${dependency.finder.extract.source}')
    String source

    @Value('${dependency.finder.extract.filter.includes://}')
    String filterIncludes

    @Value('${dependency.finder.extract.filter.excludes:}')
    String filterExcludes

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
                        filterIncludes: RegularExpressionParser.parseRE(filterIncludes),
                        filterExcludes: RegularExpressionParser.parseRE(filterExcludes),
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
