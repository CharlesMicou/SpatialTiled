# Tiled World Authoring

## Setup

Dependencies:
* The `spatial` CLI tool
* [Gradle](https://gradle.org/install/) installed and on your PATH

Run `spatial worker build TiledWorker` to generate the SpatialOS Java SDK dependencies.

## Doing things

This is more of a braindump for now.

Need to experiment with the best structure for a Tiled directory.

```
tiled_mapset/
--maps/
----map1.tmx
----map2.tmx
----map3.tmx
--tilesets/
----tileset1.tsx
----tileset2.tsx
--img/
----forest_tiles.png
----lake_tiles.png
```
