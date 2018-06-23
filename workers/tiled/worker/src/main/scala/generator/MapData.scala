package generator

import java.io.File

import common.CoordinatesHelper._
import common.GridMap
import common.MagicConstants.{RESOURCE_COORDINATES, TILE_X_DIMENSION, TILE_Z_DIMENSION}
import improbable._
import improbable.worker.{Bytes, Entity}
import tiled.map.{MapEditorMetadata, MapEditorMetadataData}
import util.{Gzipper, XMLHelper}

import scala.xml.{Elem, XML}

class MapData(name: String,
              width: Int,
              height: Int,
              layers: Seq[MapLayer],
              origin: Coordinates,
              metadata: Elem) {

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
                chunkedMapLayers)
        }
    }

    def metadataEntity(): Entity = {
        val entity = new Entity
        val data = Gzipper.compress(metadata.toString().getBytes())
        entity.add(Metadata.COMPONENT, new MetadataData("MapMetadata"))
        entity.add(Persistence.COMPONENT, new PersistenceData())
        entity.add(EntityAcl.COMPONENT, EntityAclData.create())
        entity.add(MapEditorMetadata.COMPONENT, new MapEditorMetadataData(
            name, Bytes.fromBackingArray(data)))
        entity.add(Position.COMPONENT, new PositionData(RESOURCE_COORDINATES))
        entity
    }

    def writeToFile(file: File): Unit = {
        // figure out the necessary dependencies and make a tileid -> gid function

        // for each layer, write layer data as csv

        XML.save(file.getAbsolutePath,
            metadata,
            "UTF-8",
            xmlDecl = true,
            doctype = null)
    }
}

object MapData {
    def fromChunks(metadata: Elem, chunks: Seq[MapChunkEntity]): MapData = {
        // The origin is the top-left-most chunk's origin.
        val origin = chunks
          .sortWith((c1, c2) => c1.coordinates.isTopLeftOf(c2.coordinates))
          .head.coordinates

        // All chunks share the same map name.
        val name = chunks.head.name

        // Iterate through layers, making the assumption that layers present
        // in one chunk are present in all chunks.
        val mapLayers: Seq[MapLayer] = chunks.head.mapLayers.map(
            mapLayer =>
              MapLayer.merge(chunks.flatMap(chunk =>
                  chunk.mapLayers
                    .filter(layer => layer.id == mapLayer.id)
                    .map(data => (chunk.coordinates, data))).toMap)
        )

        val width = mapLayers.head.tileData.width
        val height = mapLayers.head.tileData.height

        new MapData(name, width, height, mapLayers, origin, metadata)
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
              MapLayer(name, id, GridMap.fromRowsAndCols(data))
          })

        val metadata = XMLHelper.stripLabels(xml, Set("layer", "tileset", "objectgroup"))

        new MapData(mapName, width, height, layers, mapOffset, metadata)
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
