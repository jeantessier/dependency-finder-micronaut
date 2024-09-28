package micronaut.depfind.test

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/extract")
class ExtractController {

    final DependencyGraph graph

    @Inject
    ExtractController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        graph.stats
    }

    @Post
    def extract() {
        graph.extract()

        HttpResponse.redirect(new URI("/extract"))
    }

}
