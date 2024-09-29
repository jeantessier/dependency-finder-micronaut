package micronaut.depfind.test

import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/extract")
class ExtractController {

    @Value('${dependency.finder.extract.source}')
    def source

    @Value('${dependency.finder.extract.filter.includes://}')
    String filterIncludes

    @Value('${dependency.finder.extract.filter.excludes:}')
    String filterExcludes

    final DependencyGraph graph

    @Inject
    ExtractController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        [
                extract: [
                        source: source,
                        filterIncludes: filterIncludes,
                        filterExcludes: filterExcludes,
                ],
                graph: graph.stats,
        ]
    }

    @Post
    def extract(@Nullable label, @Nullable update) {
        if (graph.stats.extractStart && update) {
            graph.update(source, filterIncludes, filterExcludes, label)
        } else {
            graph.extract(source, filterIncludes, filterExcludes, label)
        }

        HttpResponse.redirect(new URI("/extract"))
    }

}
