package micronaut.depfind.test

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(propertySources = "ExtractControllerSpec.properties")
class ExtractControllerSpec extends Specification {

    @Serdeable
    static class ExtractMetadata {
        List<String> sources
        List<String> filterIncludes
        List<String> filterExcludes
    }

    @Serdeable
    static class ExtractResponse {
        ExtractMetadata extract
        GraphStats graph
    }

    @Inject
    ObjectMapper objectMapper

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client("/")
    HttpClient client

    void "metadata"() {
        when:
        def body = client.toBlocking().retrieve(HttpRequest.GET("/api/extract"), ExtractResponse)

        then:
        body.extract.sources == [ "src/test/resources/test.jar" ]

        and:
        body.extract.filterIncludes == [ "//" ]
        body.extract.filterExcludes == [ "/^java/", "/^org/" ]

        and:
        body.graph.name == "Dependency Finder"
        body.graph.nbPackages == 0
        body.graph.nbClasses == 0
        body.graph.nbFeatures == 0
    }

}
