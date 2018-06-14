# Tiled World Authoring

This is a tool for SpatialOS world authoring using the open source [Tiled map editor](https://github.com/bjorn/tiled).

The objective is to be able to convert back and forth between a SpatialOS snapshot file and several Tiled map files.

## Setup

Dependencies:
* The `spatial` CLI tool (available for various OSes from [here](https://docs.improbable.io/reference/13.0/index))
* [Gradle 4.x or higher](https://gradle.org/install/) installed and on your PATH

Run `spatial worker build TiledWorker` to generate the SpatialOS Java SDK dependencies.

## Tiled Project Structure

At the moment, the snapshot generator expects to be passed a world resource directory that follows the following format (defining `maps`, `tilesets`, and `img` folders).

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

## Todo List
* Resources: should be more than an ID
* Coordinate offset per map
* Multiple map support
* Snapshot to .tmx conversion