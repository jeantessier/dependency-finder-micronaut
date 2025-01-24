package micronaut.depfind.test

import com.jeantessier.dependency.JSONMetricsReport
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Controller("/metrics")
class MetricsController {

    private static final logger = LoggerFactory.getLogger(MetricsController)

    final DependencyGraph graph

    @Inject
    MetricsController(DependencyGraph graph) {
        this.graph = graph
    }

    @Introspected
    @Serdeable.Deserializable
    static class MetricsCommand {
        def packageScope = false
        def classScope = false
        def featureScope = false
        def scopeIncludes = []
        def scopeExcludes = []

        def packageFilter = false
        def classFilter = false
        def featureFilter = false
        def filterIncludes = []
        def filterExcludes = []

        def listElements = false
        def histograms = false
        def chart = false
    }

    @Post
    def index(@Body MetricsCommand command) {
        logger.info("POST metrics")
        logger.info("    scope:  package: {}, class: {}, feature: {}, includes: \"{}\", excludes: \"{}\"", command.packageScope, command.classScope, command.featureScope, command.scopeIncludes, command.scopeExcludes)
        logger.info("    filter: package: {}, class: {}, feature: {}, includes: \"{}\", excludes: \"{}\"", command.packageFilter, command.classFilter, command.featureFilter, command.filterIncludes, command.filterExcludes)
        logger.info("    list: elements: {}, histograms {}, chart {}", command.listElements, command.histograms, command.chart)

        def dependenciesMetrics = graph.metrics(
                command.packageScope,
                command.classScope,
                command.featureScope,
                command.scopeIncludes,
                command.scopeExcludes,
                command.packageFilter,
                command.classFilter,
                command.featureFilter,
                command.filterIncludes,
                command.filterExcludes,
        )

        def out = new StringWriter()
        def reporter = new JSONMetricsReport(new PrintWriter(out))
        reporter.listingElements = command.listElements
        reporter.showingClassesPerPackageChart = command.chart
        reporter.showingFeaturesPerClassChart = command.chart
        reporter.showingInboundsPerPackageChart = command.chart
        reporter.showingOutboundsPerPackageChart = command.chart
        reporter.showingInboundsPerClassChart = command.chart
        reporter.showingOutboundsPerClassChart = command.chart
        reporter.showingInboundsPerFeatureChart = command.chart
        reporter.showingOutboundsPerFeatureChart = command.chart
        reporter.showingClassesPerPackageHistogram = command.histograms
        reporter.showingFeaturesPerClassHistogram = command.histograms
        reporter.showingInboundsPerPackageHistogram = command.histograms
        reporter.showingOutboundsPerPackageHistogram = command.histograms
        reporter.showingInboundsPerClassHistogram = command.histograms
        reporter.showingOutboundsPerClassHistogram = command.histograms
        reporter.showingInboundsPerFeatureHistogram = command.histograms
        reporter.showingOutboundsPerFeatureHistogram = command.histograms

        reporter.process(dependenciesMetrics)

        out.toString()
    }

}
