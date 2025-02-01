package micronaut.depfind.test

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(propertySources = "LoadControllerSpec.properties")
class LoadControllerSpec extends Specification {

    @Serdeable
    static class LoadMetadata {
        List<String> files
    }

    @Serdeable
    static class LoadResponse {
        LoadMetadata load
        GraphStats graph
    }

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client("/")
    HttpClient client

    void "metadata"() {
        when:
        def body = client.toBlocking().retrieve(HttpRequest.GET("/api/load"), LoadResponse)

        then:
        body.load.files == [ "src/test/resources/df.test.xml" ]

        and:
        body.graph.name == "Dependency Finder"
        body.graph.nbPackages == 0
        body.graph.nbClasses == 0
        body.graph.nbFeatures == 0
    }

}
