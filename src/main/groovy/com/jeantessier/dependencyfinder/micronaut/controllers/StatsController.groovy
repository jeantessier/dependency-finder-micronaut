package com.jeantessier.dependencyfinder.micronaut.controllers

import com.jeantessier.dependencyfinder.micronaut.services.DependencyGraph
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject

@Controller("/api/stats")
class StatsController {

    final DependencyGraph graph

    @Inject
    StatsController(DependencyGraph graph) {
        this.graph = graph
    }

    @Get
    def index() {
        graph.stats
    }

}
