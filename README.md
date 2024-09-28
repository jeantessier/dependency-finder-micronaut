# Micronaut Dependency Finder Test

A sample application that uses the Dependency Finder library.

The application is configured in `application.properties`.

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
  "name": "Dependency Finder",
  "start": "2024-09-28T04:59:28.038Z",
  "duration": 534,
  "nbPackages": 42,
  "nbClasses": 872,
  "nbFeatures": 8710
}
```

A `POST` request to `/extract` will extract a graph from the
JAR files and redirect to `/extract` to get statistics

```bash
http --follow POST :8080/extract
```

```json
{
  "name": "Dependency Finder",
  "start": "2024-09-28T04:59:28.038Z",
  "duration": 534,
  "nbPackages": 42,
  "nbClasses": 872,
  "nbFeatures": 8710
}
```

### To Load a Graph From a File

List your XML files in the `dependency.finder.graph.file` config.

A `GET` request to `/load` returns details about the graph
currently in memory.

```bash
http :8080/load
```

```json
{
  "name": "Dependency Finder",
  "start": "2024-09-28T04:59:28.038Z",
  "duration": 534,
  "nbPackages": 42,
  "nbClasses": 872,
  "nbFeatures": 8710
}
```

A `POST` request to `/load` will load a graph from the
XML files and redirect to `/load` to get statistics

```bash
http --follow POST :8080/load
```

```json
{
  "name": "Dependency Finder",
  "start": "2024-09-28T04:59:28.038Z",
  "duration": 534,
  "nbPackages": 42,
  "nbClasses": 872,
  "nbFeatures": 8710
}
```

## To Query a Graph

A `POST` request to `/query` will query the graph and return
the resulting subgraph as a JSON object.

```bash
http :8080/query \
  packageScope=on \
  packageFilter=on \
  scopeIncludes=/^com.jeantessier/ \
  filterIncludes=/^com.jeantessier/ \
  showInbounds= \
  showEmptyNodes=
```

```json
[
    {
        "classes": [],
        "confirmed": "true",
        "inbound": [],
        "name": "com.jeantessier.classreader",
        "outbound": [
            {
                "confirmed": "true",
                "name": "com.jeantessier.classreader.impl",
                "type": "package"
            },
            {
                "confirmed": "true",
                "name": "com.jeantessier.text",
                "type": "package"
            }
        ],
        "type": "package"
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


