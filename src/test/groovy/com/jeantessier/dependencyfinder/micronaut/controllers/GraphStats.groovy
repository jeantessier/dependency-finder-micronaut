package com.jeantessier.dependencyfinder.micronaut.controllers

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class GraphStats {

    String label
    String name

    Long nbClasses
    Long nbFeatures
    Long nbPackages

    Long extractDurationInMillis
    String extractStart

    Long updateDurationInMillis
    String updateStart

    Long loadDurationInMillis
    String loadStart

}
