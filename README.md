# Micronaut Dependency Finder Test

A sample application that uses the Dependency Finder library.

The application is configured in `application.properties`.

## Running the App

The default configuration in `application.properties` expects to extract 
dependencies from `./lib/DependencyFinder-SNAPSHOT.jar`.  It also expects to 
load a dependency graph from `./df.xml`.  You can change these expectations 
by changing in the configuration in `application.properties`.

The app will run on port `8080`.

### In Local Shell

```bash
./gradlew run
```

### In Docker

First, build the image.  See the
[Micronaut Docker documentation](https://guides.micronaut.io/latest/micronaut-docker-image-gradle-groovy.html)
for more options.

```bash
./gradlew dockerBuild
```

Once you have a Docker image, you can create a container.

```bash
docker run \
  --name dependencyfinder
  --detach \
  --publish "8080:8080" \
  --volume ./lib/DependencyFinder-SNAPSHOT.jar:/home/app/lib/DependencyFinder-SNAPSHOT.jar:ro \ 
  --volume ./df.xml:/home/app/df.xml:ro \ 
  micronaut-depfind-test
```

The `--volume` params will mount the source and graph files the app expects, 
according to `application.properties`.  If you change the configuration, you 
will need to adjust these params accordingly.

To clean up Docker:

```bash
docker stop dependencyfinder
docker rm dependencyfinder
docker rmi micronaut-depfind-test
```

## Getting a Graph in Memory

You can either extract a graph from compiled Java code, or you can
load a graph that has already been extracted.

### To Extract a Graph From Code

List your JAR files in the `dependency.finder.graph.source` config.

A `GET` request to `/extract` returns details about the graph
currently in memory.

```bash
http :8080/extract
```

```json
{
  "extract": {
    "source": "lib/DependencyFinder-SNAPSHOT.jar",
    "filterIncludes": "//",
    "filterExcludes": "/^java/, /^org/"
  },
  "graph": {
    "name": "Dependency Finder",
    "label": null,
    "extractStart": "2024-09-29T23:30:35.728Z",
    "extractDurationInMillis": 548,
    "updateStart": null,
    "updateDurationInMillis": null,
    "loadStart": null,
    "loadDurationInMillis": null,
    "nbPackages": 23,
    "nbClasses": 691,
    "nbFeatures": 8108
  }
}
```

A `POST` request to `/extract` will extract a graph from the
JAR files and redirect to `/extract` to get statistics

```bash
http --follow POST :8080/extract
```

```json
{
  "extract": {
    "source": "lib/DependencyFinder-SNAPSHOT.jar",
    "filterIncludes": "//",
    "filterExcludes": "/^java/, /^org/"
  },
  "graph": {
    "name": "Dependency Finder",
    "label": null,
    "extractStart": "2024-09-29T23:30:35.728Z",
    "extractDurationInMillis": 548,
    "updateStart": null,
    "updateDurationInMillis": null,
    "loadStart": null,
    "loadDurationInMillis": null,
    "nbPackages": 23,
    "nbClasses": 691,
    "nbFeatures": 8108
  }
}
```

You can supply an optional `label` parameter that will be saved
along with the graph.  You can use this label to qualify the version
of the code the graph is being extracted from.

### To Update an Extracted Graph After Code Changes

If you make changes and recompile your code, you can update the graph
through the `/extract` endpoint by adding an `update` parameter.

```bash
http --follow POST :8080/extract update=true
```

```json
{
  "extract": {
    "source": "lib/DependencyFinder-SNAPSHOT.jar",
    "filterIncludes": "//",
    "filterExcludes": "/^java/, /^org/"
  },
  "graph": {
    "name": "Dependency Finder",
    "label": null,
    "extractStart": "2024-09-29T23:30:35.728Z",
    "extractDurationInMillis": 548,
    "updateStart": "2024-09-29T23:32:41.475Z",
    "updateDurationInMillis": 26,
    "loadStart": null,
    "loadDurationInMillis": null,
    "nbPackages": 23,
    "nbClasses": 691,
    "nbFeatures": 8108
  }
}
```

You can update the label associated with the graph with the
`label` parameter.  If you don't supply a `label`, it will be
removed from the graph.

### To Load a Graph From a File

List your XML files in the `dependency.finder.graph.file` config.

A `GET` request to `/load` returns details about the graph
currently in memory.

```bash
http :8080/load
```

```json
{
  "load": {
    "file": "df.xml"
  },
  "graph": {
    "name": "Dependency Finder",
    "label": null,
    "extractStart": null,
    "extractDurationInMillis": null,
    "updateStart": null,
    "updateDurationInMillis": null,
    "loadStart": "2024-09-29T23:51:50.206Z",
    "loadDurationInMillis": 192,
    "nbPackages": 42,
    "nbClasses": 872,
    "nbFeatures": 8710
  }
}
```

A `POST` request to `/load` will load a graph from the
XML files and redirect to `/load` to get statistics

```bash
http --follow POST :8080/load
```

```json
{
  "load": {
    "file": "df.xml"
  },
  "graph": {
    "name": "Dependency Finder",
    "label": null,
    "extractStart": null,
    "extractDurationInMillis": null,
    "updateStart": null,
    "updateDurationInMillis": null,
    "loadStart": "2024-09-29T23:51:50.206Z",
    "loadDurationInMillis": 192,
    "nbPackages": 42,
    "nbClasses": 872,
    "nbFeatures": 8710
  }
}
```

You can supply an optional `label` parameter that will be saved
along with the graph.  You can use this label to qualify the version
of the code the graph is being extracted from.

## To Query a Graph

A `POST` request to `/query` will query the graph and return
the resulting subgraph as a JSON object.

```bash
http :8080/query \
  packageScope:=true \
  packageFilter:=true \
  scopeIncludes=/^com.jeantessier/ \
  filterIncludes=/^com.jeantessier/ \
  showInbounds:=true \
  showEmptyNodes:=true
```

In this example, the request will look like:

```json
{
    "filterIncludes": "/^com.jeantessier/",
    "packageFilter": true,
    "packageScope": true,
    "scopeIncludes": "/^com.jeantessier/",
    "showEmptyNodes": true,
    "showInbounds": true
}
```

And the response will be shaped like:

```json
[
    {
        "type": "package",
        "confirmed": "true",
        "name": "com.jeantessier.classreader",
        "outbound": [
            {
                "type": "package",
                "confirmed": "true",
                "name": "com.jeantessier.classreader.impl"
            },
            {
                "type": "package",
                "confirmed": "true",
                "name": "com.jeantessier.text"
            }
        ],
        "inbound": [],
        "classes": []
    },
    ...
]
```

## Micronaut 4.6.2 Documentation

- [User Guide](https://docs.micronaut.io/4.6.2/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.6.2/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.6.2/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)


## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


