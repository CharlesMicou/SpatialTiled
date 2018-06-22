package common

import tiled.map.TileId

class GridMap(data: Map[(Int, Int), TileId], width: Int, height: Int) {
    def subSection(fromX: Int, fromZ: Int, untilX: Int, untilZ: Int): GridMap = {
        assert(fromX >= 0, s"Supplied a fromX $fromX out of bounds")
        assert(fromZ >= 0, s"Supplied a fromZ $fromZ out of bounds")
        assert(untilX <= width, s"Supplied a toX $untilX out of bounds $width")
        assert(untilZ <= height, s"Supplied a toZ $untilZ out of bounds $height")

        val mappedData = for {
            i <- fromX until untilX
            j <- fromZ until untilZ
        } yield {
            (i - fromX, j - fromZ) -> data(i, j)
        }

        new GridMap(mappedData.toMap, untilX - fromX, untilZ - fromZ)
    }

    def toRaw: Seq[TileId] = {
        val length = width * height
        val result = new Array[TileId](length)
        for {
            i <- 0 until width
            j <- 0 until height
        } result(i + j * width) = data(i, j)
        result
    }

    def getTile(x: Int, z: Int): Option[TileId] = {
        data.get((x, z))
    }
}

object GridMap {
    // Assumes the following sequence convention:
    // 0 1 2 3
    // 4 5 6 7
    def fromRaw(data: Seq[TileId], width: Int, height: Int): GridMap = {
        val mappedData = for {
            i <- 0 until width
            j <- 0 until height
        } yield {
            (i, j) -> data(i + j * width)
        }
        new GridMap(mappedData.toMap, width, height)
    }

    def fromRowsAndCols(data: Array[Array[TileId]]): GridMap = {
        val height = data.length
        val width = data.last.length
        data.foreach(row =>
            assert(row.length == width, "fromRowsAndCols requires rectangular arrays"))
        fromRaw(data.flatten.toSeq, width, height)
    }
}