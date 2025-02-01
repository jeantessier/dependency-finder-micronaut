package com.jeantessier.dependencyfinder.micronaut.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

import com.jeantessier.dependencyfinder.Version
import jakarta.inject.Inject

@Controller("/api/version")
class VersionController {

    final Version version

    @Inject
    VersionController(Version version) {
        this.version = version
    }

    @Get
    def index() {
        [
                implementation: [
                        title: version.implementationTitle,
                        version: version.implementationVersion,
                        vendor: version.implementationVendor,
                        date: version.implementationDate,
                        url: version.implementationURL,
                ],
                specification: [
                        title: version.specificationTitle,
                        version: version.specificationVersion,
                        vendor: version.specificationVendor,
                        date: version.specificationDate,
                ],
                copyright: [
                        date: version.copyrightDate,
                        holder: version.copyrightHolder,
                ],
        ]
    }

}
