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
import org.slf4j.LoggerFactory

@Singleton
class DependencyGraph {

    private static final logger = LoggerFactory.getLogger(DependencyGraph)

    def factory = new NodeFactory()
    def dispatcher
    def monitor

    @Value('${dependency.finder.name}')
    def name

    @Value('${dependency.finder.graph.mode:raw}')
    def mode

    def label

    def extractStart
    def extractDurationInMillis

    def updateStart
    def updateDurationInMillis

    def loadStart
    def loadDurationInMillis

    def getStats() {
        [
                name: name,
                label: label,
                extractStart: extractStart,
                extractDurationInMillis: extractDurationInMillis,
                updateStart: updateStart,
                updateDurationInMillis: updateDurationInMillis,
                loadStart: loadStart,
                loadDurationInMillis: loadDurationInMillis,
                nbPackages: factory.packages.size(),
                nbClasses: factory.classes.size(),
                nbFeatures: factory.features.size(),
        ]
    }

    def extract(List<String> sources, filterIncludes, filterExcludes, label) {
        logger.info("Extracting new graph from {}", sources)

        def start = new Date()

        factory = new NodeFactory()

        dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER)

        def filterCriteria = new RegularExpressionSelectionCriteria()
        filterCriteria.globalIncludes = filterIncludes
        filterCriteria.globalExcludes = filterExcludes

        def collector = new CodeDependencyCollector(factory, filterCriteria)
        def deletingVisitor = new DeletingVisitor(factory)

        monitor = new Monitor(collector, deletingVisitor)

        def loader = new TransientClassfileLoader(dispatcher)
        loader.addLoadListener(monitor)
        loader.load(sources)

        if (mode == "maximize") {
            new LinkMaximizer().traverseNodes(factory.packages.values())
        } else if (mode == "minimize") {
            new LinkMinimizer().traverseNodes(factory.packages.values())
        }

        def stop = new Date()

        this.label = label

        this.extractStart = start
        this.extractDurationInMillis = stop.time - start.time

        this.updateStart = null
        this.updateDurationInMillis = null
        this.loadStart = null
        this.loadDurationInMillis = null
    }

    def update(List<String> sources, filterIncludes, filterExcludes, label) {
        logger.info("Updating graph based on {}", sources)

        def start = new Date()

        dispatcher = dispatcher ?: new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER)

        def filterCriteria = new RegularExpressionSelectionCriteria()
        filterCriteria.globalIncludes = filterIncludes
        filterCriteria.globalExcludes = filterExcludes

        def collector = new CodeDependencyCollector(factory, filterCriteria)
        def deletingVisitor = new DeletingVisitor(factory)

        monitor = monitor ?: new Monitor(collector, deletingVisitor)

        def loader = new TransientClassfileLoader(dispatcher)
        loader.addLoadListener(monitor)
        loader.load(sources)

        if (mode == "maximize") {
            new LinkMaximizer().traverseNodes(factory.packages.values())
        } else if (mode == "minimize") {
            new LinkMinimizer().traverseNodes(factory.packages.values())
        }

        def stop = new Date()

        this.label = label

        this.extractStart = this.extractStart ?: start
        this.extractDurationInMillis = this.extractDurationInMillis ?: (stop.time - start.time)
        this.updateStart = start
        this.updateDurationInMillis = stop.time - start.time

        this.loadStart = null
        this.loadDurationInMillis = null
    }

    def load(List<String> files, label) {
        logger.info("Loading new graph from {}", files)

        def start = new Date()

        factory = new NodeFactory()

        def loader = new NodeLoader(factory)
        files.each { loader.load it }

        if (mode == "maximize") {
            new LinkMaximizer().traverseNodes(factory.packages.values())
        } else if (mode == "minimize") {
            new LinkMinimizer().traverseNodes(factory.packages.values())
        }

        def stop = new Date()

        this.label = label

        this.loadStart = start
        this.loadDurationInMillis = stop.time - start.time

        this.dispatcher = null
        this.monitor = null
        this.extractStart = null
        this.extractDurationInMillis = null
        this.updateStart = null
        this.updateDurationInMillis = null
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

        def dependenciesQuery = new GraphSummarizer(scopeCriteria, filterCriteria)
        if (mode == "maximize") {
            def strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria)
            dependenciesQuery = new GraphCopier(strategy)
        }

        dependenciesQuery.traverseNodes(factory.packages.values())

        dependenciesQuery
    }
}
