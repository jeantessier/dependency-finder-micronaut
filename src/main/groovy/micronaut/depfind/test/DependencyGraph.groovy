package micronaut.depfind.test

import com.jeantessier.classreader.ClassfileLoaderEventSource
import com.jeantessier.classreader.ModifiedOnlyDispatcher
import com.jeantessier.classreader.Monitor
import com.jeantessier.classreader.TransientClassfileLoader
import com.jeantessier.dependency.CodeDependencyCollector
import com.jeantessier.dependency.DeletingVisitor
import com.jeantessier.dependency.GraphCopier
import com.jeantessier.dependency.GraphSummarizer
import com.jeantessier.dependency.LinkMaximizer
import com.jeantessier.dependency.LinkMinimizer
import com.jeantessier.dependency.NodeFactory
import com.jeantessier.dependency.NodeLoader
import com.jeantessier.dependency.RegularExpressionSelectionCriteria
import com.jeantessier.dependency.SelectiveTraversalStrategy
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton

@Singleton
class DependencyGraph {
    def factory = new NodeFactory()
    def dispatcher
    def monitor

    @Value('${dependency.finder.name}')
    def name

    @Value('${dependency.finder.graph.source}')
    def source

    @Value('${dependency.finder.graph.filter.includes://}')
    String filterIncludes

    @Value('${dependency.finder.graph.filter.excludes:}')
    String filterExcludes

    @Value('${dependency.finder.graph.file}')
    String file

    @Value('${dependency.finder.graph.mode:raw}')
    def mode

    def start
    def duration

    def getStats() {
        [
                name: name,
                start: start,
                duration: duration,
                nbPackages: factory.packages.size(),
                nbClasses: factory.classes.size(),
                nbFeatures: factory.features.size(),
        ]
    }

    def extract() {
        def start = new Date()

        def sources = source.split(/,/) as List

        def factory = new NodeFactory()

        def dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER)

        def filterCriteria = new RegularExpressionSelectionCriteria()
        filterCriteria.globalIncludes = filterIncludes
        filterCriteria.globalExcludes = filterExcludes

        def collector = new CodeDependencyCollector(factory, filterCriteria)
        def deletingVisitor = new DeletingVisitor(factory)

        def monitor = new Monitor(collector, deletingVisitor)

        def loader = new TransientClassfileLoader(dispatcher)
        loader.addLoadListener(monitor)
        loader.load(sources)

        if (mode == "maximize") {
            new LinkMaximizer().traverseNodes(factory.packages.values())
        } else if (mode == "minimize") {
            new LinkMinimizer().traverseNodes(factory.packages.values())
        }

        def stop = new Date()

        this.start = start
        this.duration = stop.time - start.time
        this.factory = factory
        this.dispatcher = dispatcher
        this.monitor = monitor
    }

    def load() {
        def start = new Date()

        def files = file.split(/,/)

        def factory = new NodeFactory()
        def loader = new NodeLoader(factory)

        files.each { loader.load(it) }

        if (mode == "maximize") {
            new LinkMaximizer().traverseNodes(factory.packages.values())
        } else if (mode == "minimize") {
            new LinkMinimizer().traverseNodes(factory.packages.values())
        }

        def stop = new Date()

        this.start = start
        this.duration = stop.time - start.time
        this.factory = factory
    }
    
    def query(packageScope, classScope, featureScope, scopeIncludes, scopeExcludes, packageFilter, classFilter, featureFilter, filterIncludes, filterExcludes) {
        def scopeCriteria  = new RegularExpressionSelectionCriteria()
        def filterCriteria = new RegularExpressionSelectionCriteria()

        scopeCriteria.matchingPackages = packageScope
        scopeCriteria.matchingClasses = classScope
        scopeCriteria.matchingFeatures = featureScope
        scopeCriteria.globalIncludes = scopeIncludes
        scopeCriteria.globalExcludes = scopeExcludes

        filterCriteria.matchingPackages = packageFilter
        filterCriteria.matchingClasses = classFilter
        filterCriteria.matchingFeatures = featureFilter
        filterCriteria.globalIncludes = filterIncludes
        filterCriteria.globalExcludes = filterExcludes

        def dependenciesQuery = new GraphSummarizer(scopeCriteria, filterCriteria);
        if (mode == "maximize") {
            def strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);
            dependenciesQuery = new GraphCopier(strategy);
        }

        dependenciesQuery.traverseNodes(factory.packages.values());

        dependenciesQuery
    }
}
