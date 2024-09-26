package micronaut.depfind.test

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

import com.jeantessier.dependencyfinder.Version
import jakarta.inject.Inject

@Controller("/version")
class VersionController {

    final Version version

    @Inject
    VersionController(Version version) {
        this.version = version
    }

    @Get("/")
    def index() {
        [
            title: version.implementationTitle,
            version: version.implementationVersion,
            date: version.copyrightDate,
            holder: version.copyrightHolder,
        ]
    }

}
