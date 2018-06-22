# Tiled World Authoring

This is a tool for SpatialOS world authoring using the open source [Tiled map editor](https://github.com/bjorn/tiled).

The objective is to be able to convert back and forth between a SpatialOS snapshot file and several Tiled map files.

## Setup

Dependencies:
* The `spatial` CLI tool (available for various OSes from [here](https://docs.improbable.io/reference/13.0/index))
* [Gradle 4.x or higher](https://gradle.org/install/) installed and on your PATH

Run `spatial worker build TiledWorker` to import the SpatialOS Java SDK dependencies and generate the schema generated code.

## Generating a snapshot from map files

Run `spatial local worker launch TiledWorker toSnapshot` to generate a snapshot outputted to `snapshots/test_world.snapshot`.

I'll fix this up to take better args soon, I promise.

## Generating a map from snapshot files

Run `spatial local worker launch TiledWorker toMaps` to get a `NotImplementedError` exception.


## Tiled Project Structure

At the moment, the snapshot generator expects a world resource directory that follows the following format (defining `maps`, `tilesets`, and `img` folders).

```
my_world/
--maps/
----map_with_trees.tmx
----map2.tmx
----map_test.tmx
--tilesets/
----tileset_forest.tsx
----tileset2.tsx
--img/
----forest_tiles.png
----lake_tiles.png
```

## Testing

Run `gradle test` from within `workers/tiled` or `workers/tiled/worker` to run the unit tests.

## IntelliJ Setup

`File -> New -> Project from existing sources...`
and open `SpatialTiled/workers/tiled`.

To run from within IntelliJ instead of through `spatial build` and `spatial local worker launch`, set up a run configuration with the main class `Launcher` and the arguments: `toSnapshot <path/to/SpatialTiled>`.

## Satire Warning

In order to aggravate my dear friend Fabian, this tool is written in Scala and deliberately makes use of dangerous features and antipatterns.

Highlights include:
* XML DSL
* Implicit classes for generated code
* Nested `for`-`yield` blocks
* `Any` in `match` clauses
* `assert` statements scattered everywhere
* `import scala.collection.JavaConversions._`

Please don't take this code seriously.
Please don't show it to my current or future employers.

## Todo List
* Clean up remote resource code
* Snapshot to .tmx conversion
* Layer properties
* Object layer support
