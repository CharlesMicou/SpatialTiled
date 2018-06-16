package common

/**
  * These will be stored in either configs or map files eventually...
  */
object MagicConstants {
    // The dimensions of a tile in SpatialOS world units.
    val TILE_X_DIMENSION = 1.0f
    val TILE_Z_DIMENSION = 1.0f

    // The maximum chunk sizes for map entities
    val MAX_X_CHUNK = 5
    val MAX_Z_CHUNK = 5

    // The attributes for map chunk entities
    val READ_ATTRIBUTES: Seq[String] = Seq("shoveler", "loadtest", "gamex")
    val WRITE_ATTRIBUTE: Option[String] = Option.empty
}
