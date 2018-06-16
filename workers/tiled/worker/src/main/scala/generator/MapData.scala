package generator

import java.io.File

import improbable.Coordinates
import common.MagicConstants.{TILE_X_DIMENSION, TILE_Z_DIMENSION}
import common.CoordinatesHelper._
import common.TileLayer

import scala.xml.{Elem, XML}

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
        val mapName = file.getName
        val xml = XML.loadFile(file)
        val width = xml.attribute("width").get.text.toInt
        val height = xml.attribute("height").get.text.toInt
        val mapOffset = mapOffsetCoordinates(xml)

        // Sanity check orientation
        assert(xml.attribute("renderorder").get.text.equals("right-down"),
            "Tiled Map render order must be set to right-down.")

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


        new MapData(mapName, width, height, layers, mapOffset)
    }

    private def mapOffsetCoordinates(xml: Elem): Coordinates = {
        val coordsField = (xml \ "properties" \ "property")
          .filter(property => property.attribute("name").get.text.equals("coordinate_offset"))
          .map(property => property.attribute("value").get.text)
        assert(coordsField.length == 1, "Maps require a coordinate_offset property in the format 1.0, 2.0, 3.0")
        val coordsVals = coordsField.head.split(",").map(s => s.trim.toDouble)
        makeCoordinates(coordsVals(0), coordsVals(1), coordsVals(2))
    }
}
