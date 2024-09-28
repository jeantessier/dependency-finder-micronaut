package micronaut.depfind.test

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/load")
class LoadController {

    final DependencyGraph graph

    @Inject
    LoadController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        graph.stats
    }

    @Post
    def load() {
        graph.load()

        HttpResponse.redirect(new URI("/load"))
    }

}
