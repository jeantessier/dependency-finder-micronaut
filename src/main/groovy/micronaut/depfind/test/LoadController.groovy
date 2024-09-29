package micronaut.depfind.test

import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/load")
class LoadController {

    @Value('${dependency.finder.load.file}')
    String file

    final DependencyGraph graph

    @Inject
    LoadController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        [
                load: [
                        file: file,
                ],
                graph: graph.stats,
        ]
    }

    @Post
    def load(@Nullable label) {
        graph.load(file, label)

        HttpResponse.redirect(new URI("/load"))
    }

}
