package generator

import java.io.File

import improbable.Coordinates
import common.MagicConstants.{TILE_X_DIMENSION, TILE_Z_DIMENSION}
import common.CoordinatesHelper._
import common.TileLayer

import scala.xml.XML

case class MapLayer(name: String, id: Int, tileData: TileLayer)

class MapData(name: String, width: Int, height: Int, layers: Seq[MapLayer], origin: Coordinates) {

    def toMapChunks(maxChunkWidth: Int,
                    maxChunkHeight: Int,
                    tileResourceDirectory: TileResourceDirectory): Seq[MapChunkEntity] = {
        // Chunk out the map layers
        val chunksXBound = Math.ceil(width.toFloat / maxChunkWidth).toInt
        val chunksZBound = Math.ceil(height.toFloat / maxChunkHeight).toInt

        for {
            i <- 0 until chunksXBound
            j <- 0 until chunksZBound
        } yield {
            val offset = makeCoordinates(
                i * maxChunkWidth * TILE_X_DIMENSION,
                0.0,
                -j * maxChunkHeight * TILE_Z_DIMENSION)
            val xLower = i * maxChunkWidth
            val zLower = j * maxChunkHeight
            val xUpper = Math.min((i + 1) * maxChunkWidth, width)
            val zUpper = Math.min((j + 1) * maxChunkHeight, height)
            val chunkedMapLayers: Seq[MapLayer] = layers.map {
                layer =>
                    MapLayer(layer.name, layer.id, layer.tileData.subSection(xLower, zLower, xUpper, zUpper))
            }
            new MapChunkEntity(
                name,
                origin + offset,
                xUpper - xLower,
                zUpper - zLower,
                chunkedMapLayers,
                tileResourceDirectory)
        }
    }

    def writeToDir(outputDir: String): Unit = {
        // todo
    }
}

object MapData {
    def fromChunks(chunks: Seq[MapChunkEntity]): MapData = {
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
              val data = (layer \ "data").text.split("\n")
                .filter(s => !s.trim.equals(""))
                .map {
                    line =>
                        line.split(",")
                          .filter(s => !s.trim.equals(""))
                          .map {
                              csvEntry =>
                                  tileResourceDirectory.tileIdFromMapFileMapping(csvEntry.toInt, localResourceMapping)
                          }
                }
              MapLayer(name, id, TileLayer.fromRowsAndCols(data))
          })

        val mapName = file.getName

        // todo: coordinates
        new MapData(mapName, width, height, layers, makeCoordinates(0, 0, 0))
    }
}
