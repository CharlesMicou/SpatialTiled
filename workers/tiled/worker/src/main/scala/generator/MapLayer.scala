package generator

import common.CoordinatesHelper._
import common.{GridMap, MagicConstants}
import improbable.Coordinates

case class MapLayer(name: String, id: Int, tileData: GridMap)

object MapLayer {
    def merge(parts: Map[Coordinates, MapLayer]): MapLayer = {
        val name = parts.head._2.name
        val id = parts.head._2.id

        val topLeft = parts.keys.toSeq.sortWith((c1, c2) => c1.isTopLeftOf(c2)).head
        val bottomRight = parts.keys.toSeq.sortWith((c1, c2) => c1.isBottomRightOf(c2)).head

        val width: Int = ((bottomRight.getX - topLeft.getX) / MagicConstants.TILE_X_DIMENSION).toInt
        val height: Int = ((topLeft.getZ - bottomRight.getZ) / MagicConstants.TILE_X_DIMENSION).toInt

        val mergedGridData = parts.flatMap(pair => {
            val relativeOrigin = pair._1 - topLeft
            val xGridOffset = (relativeOrigin.getX / MagicConstants.TILE_X_DIMENSION).toInt
            val zGridOffset = -(relativeOrigin.getZ / MagicConstants.TILE_Z_DIMENSION).toInt

            pair._2.tileData.data.map(f => {
                (xGridOffset + f._1._1, zGridOffset + f._1._2) -> f._2
            })
        })

        // All parts should agree on layer name and ID
        MapLayer(name, id, new GridMap(mergedGridData, width, height))
    }
}
