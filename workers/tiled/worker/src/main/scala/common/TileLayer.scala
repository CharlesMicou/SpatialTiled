package common

import tiled.map.TileId

class TileLayer(data: Map[(Int, Int), TileId], width: Int, height: Int) {
    def subSection(fromX: Int, fromZ: Int, toX: Int, toZ: Int): TileLayer = {
        assert(fromX >= 0, s"Supplied a fromX $fromX out of bounds")
        assert(fromZ >= 0, s"Supplied a fromZ $fromZ out of bounds")
        assert(toX <= width, s"Supplied a toX $toX out of bounds $width")
        assert(toZ <= height, s"Supplied a toZ $toZ out of bounds $height")

        val mappedData = for {
            i <- fromX to toX
            j <- fromZ to toZ
        } yield {
            (i - fromX, j - fromZ) -> data(i, j)
        }

        new TileLayer(mappedData.toMap, toX - fromX, toZ - fromZ)
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
}

object TileLayer {
    // Assumes the following sequence convention:
    // 0 1 2 3
    // 4 5 6 7
    def fromRaw(data: Seq[TileId], width: Int, height: Int): TileLayer = {
        val mappedData = for {
            i <- 0 until width
            j <- 0 until height
        } yield {
            (i, j) -> data(i + j * width)
        }
        new TileLayer(mappedData.toMap, width, height)
    }

    def fromRowsAndCols(data: Array[Array[TileId]]): TileLayer = {
        val height = data.length
        val width = data.last.length
        data.foreach(row =>
            assert(row.length == width, "fromRowsAndCols requires rectangular arrays"))
        fromRaw(data.flatten, width, height)
    }
}