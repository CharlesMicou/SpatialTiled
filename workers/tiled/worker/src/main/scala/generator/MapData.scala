package generator

import java.io.File

import improbable.Coordinates
import tiled.map.{MapChunk, TileId}
import CoordinatesHelper._

import scala.xml.XML

// Data is (row)(column) indexed
case class MapLayer(name: String, id: Int, data: Array[Array[TileId]])

class MapData(name: String, width: Int, height: Int, layers: Seq[MapLayer], origin: Coordinates) {
    def toMapChunks(maxChunkWidth: Int, maxChunkHeight: Int): Seq[MapChunk] = {
        val chunkX = 1
        val chunkZ = 1
        for {
            i <- 0 until maxChunkWidth
            j <- 0 until maxChunkHeight
            if chunkX * maxChunkWidth + i < width
            if chunkZ * maxChunkWidth + j < height
        } yield {
            // todo fix this

        }
        ???
    }

    def writeToDir(outputDir: String): Unit = {
        // todo
    }
}

object MapData {
    def fromChunks(chunks: Seq[MapChunk]): MapData = {
        ???
    }

    def fromFile(file: File, tileResourceDirectory: TileResourceDirectory): MapData = {
        assert(file.getName.endsWith(".tmx"), s"File $file is not a .tmx file")
        val xml = XML.loadFile(file)
        val width = xml.attribute("width").get.text.toInt
        val height = xml.attribute("height").get.text.toInt
        val localResourceMapping = (xml \ "tileset")
          .map(tileset => {
              val firstGid = tileset.attribute("firstgid").get.text.toInt
              val tilesetName = new File(tileset.attribute("source").get.text)
                .getName.trim
              (firstGid, tilesetName)
          }).toMap

        val layers = (xml \ "layer")
          .map(layer => {
              val id = layer.attribute("id").get.text.toInt
              val name = layer.attribute("name").get.text
              val data = (layer \ "data").text.split("\n").map { s =>
                  s.split(",").filter(a => !a.trim.equals("")).map { csvEntry =>
                      tileResourceDirectory.tileIdFromMapFileMapping(csvEntry.toInt, localResourceMapping)
                  }
              }
              MapLayer(name, id, data)
          })

        val mapName = file.getName

        // todo: coordinates
        new MapData(mapName, width, height, layers, makeCoordinates(0, 0, 0))
    }
}
