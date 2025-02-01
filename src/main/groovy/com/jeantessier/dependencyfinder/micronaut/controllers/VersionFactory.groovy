package com.jeantessier.dependencyfinder.micronaut.controllers

import com.jeantessier.dependencyfinder.Version
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class VersionFactory {

    @Singleton
    Version version() {
        new Version()
    }

}
