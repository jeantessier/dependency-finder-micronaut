package micronaut.depfind.test

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

import com.jeantessier.dependencyfinder.Version

@Controller("/version")
class VersionController {

    @Get("/")
    def index() {
        def version = new Version()

        [
            title: version.implementationTitle,
            version: version.implementationVersion,
            date: version.copyrightDate,
            holder: version.copyrightHolder,
        ]
    }

}
