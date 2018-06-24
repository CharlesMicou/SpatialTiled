package generator

import common.CoordinatesHelper._
import common.MagicConstants.{TILE_X_DIMENSION, TILE_Z_DIMENSION}
import common.{GridMap, MagicConstants}
import improbable.Coordinates

case class MapLayer(name: String, id: Int, tileData: GridMap)

object MapLayer {
    /** Merge several map layers with coordinate offsets into a single map layer */
    def merge(parts: Map[Coordinates, MapLayer]): MapLayer = {
        val name = parts.head._2.name
        val id = parts.head._2.id

        val topLeft = parts.keys.toSeq.sortWith((c1, c2) => c1.isTopLeftOf(c2)).head
        val bottomRightPair = parts.toSeq.sortWith((f1, f2) => f1._1.isBottomRightOf(f2._1)).head
        val bottomRight = bottomRightPair._1 + makeCoordinates(
            TILE_X_DIMENSION * bottomRightPair._2.tileData.width,
            0,
            TILE_Z_DIMENSION * bottomRightPair._2.tileData.height)

        val width: Int = ((bottomRight.getX - topLeft.getX) / TILE_X_DIMENSION).toInt
        val height: Int = ((topLeft.getZ - bottomRight.getZ) / TILE_Z_DIMENSION).toInt

        val mergedGridData = parts.flatMap(pair => {
            val relativeOrigin = pair._1 - topLeft
            val xGridOffset = (relativeOrigin.getX / TILE_X_DIMENSION).toInt
            val zGridOffset = -(relativeOrigin.getZ / TILE_Z_DIMENSION).toInt

            pair._2.tileData.data.map(f => {
                (xGridOffset + f._1._1, zGridOffset + f._1._2) -> f._2
            })
        })

        // All parts should agree on layer name and ID
        MapLayer(name, id, new GridMap(mergedGridData, width, height))
    }
}
