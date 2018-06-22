package common

import improbable.Coordinates

/**
  * These will be stored in either configs or map files eventually...
  */
object MagicConstants {
    // Project structure folders
    val tilesetFolder = "tilesets"
    val imgFolder = "img"
    val mapFolder = "maps"

    // The dimensions of a tile in SpatialOS world units.
    val TILE_X_DIMENSION = 1.0f
    val TILE_Z_DIMENSION = 1.0f

    // The maximum chunk sizes for map entities
    val MAX_X_CHUNK = 5
    val MAX_Z_CHUNK = 5

    // The attributes for map chunk entities
    val READ_ATTRIBUTES: Seq[String] = Seq("gamex")
    val WRITE_ATTRIBUTE: Option[String] = Option.empty

    // The world location to put resource entities.
    val RESOURCE_COORDINATES: Coordinates = CoordinatesHelper.makeCoordinates(-499, -499, -499)
}
