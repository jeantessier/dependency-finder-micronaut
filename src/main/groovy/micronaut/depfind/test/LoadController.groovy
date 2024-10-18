package micronaut.depfind.test

import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/load")
class LoadController {

    private static final logger = LoggerFactory.getLogger(LoadController)

    @Value('${dependency.finder.load.file}')
    String file

    final DependencyGraph graph

    def getFiles() {
        file.split(/,\\s*/) as List
    }

    @Inject
    LoadController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        [
                load: [
                        files: files,
                ],
                graph: graph.stats,
        ]
    }

    @Post
    def load(@Nullable String label) {
        logger.info("POST load")
        logger.info("    label: {}", label)

        graph.load(files, label)

        HttpResponse.temporaryRedirect(new URI("/load"))
    }

}
