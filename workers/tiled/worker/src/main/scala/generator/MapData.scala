package generator

import java.io.File

import improbable.Coordinates
import tiled.map.{MapChunk, TileId}

import scala.xml.XML

case class MapLayer(name: String, id: Int, data: Seq[TileId])

class MapData(name: String, width: Int, height: Int, layers: Seq[MapLayer], origin: Coordinates) {

    def toMapChunks: Seq[MapChunk] = ???

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
              val data = (layer \ "data").text.split("\n").flatMap(s => s.split(","))
                .filter(s => !s.equals(""))
                .map(csvEntry => tileResourceDirectory.tileIdFromMapFileMapping(
                    csvEntry.toInt, localResourceMapping))
              MapLayer(name, id, data)
          })

        val mapName = file.getName

        // todo: coordinates
        new MapData(mapName, width, height, layers, Coordinates.create())
    }
}
