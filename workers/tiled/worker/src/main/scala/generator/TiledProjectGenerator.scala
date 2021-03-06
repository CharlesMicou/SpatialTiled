package generator

import java.io.File

import common.MagicConstants
import generator.TiledProjectGenerator.{ParsedSnapshot, TileResourceWithSize}
import improbable.{Position, PositionData}
import improbable.worker.SnapshotInputStream
import tiled.map.{MapChunk, MapChunkData, MapEditorMetadata, MapEditorMetadataData}
import tiled.resource.{GzippedResource, GzippedResourceData}
import util.Gzipper

import scala.collection.mutable
import scala.xml.Elem

class TiledProjectGenerator(outputDir: String) {
    private val imgDir = outputDir + "/" + MagicConstants.imgFolder
    private val tilesetDir = outputDir + "/" + MagicConstants.tilesetFolder
    private val mapDir = outputDir + "/" + MagicConstants.mapFolder
    initProject()

    def loadFromSnapshot(snapshotPath: String): Unit = {
        val parsedSnapshot = parseSnapshot(snapshotPath)
        val resourcesWithSize = unpackResources(parsedSnapshot.resources)
        writeMaps(parsedSnapshot.editorMetadata, parsedSnapshot.mapChunks, resourcesWithSize)
    }

    /**
      * Dumps the contents of resource entities into files.
      * Returns the resource id and corresponding name + size of each resource.
      */
    private def unpackResources(resources: Seq[GzippedResourceData]): Map[Int, TileResourceWithSize] = {
        resources.map(
            resource => {
                val xml = Gzipper.decompressToXml(resource.getGzippedTilesetFile.getBackingArray)
                val name = xml.attribute("name").get.text + ".tsx"
                val size = xml.attribute("tilecount").get.text.toInt
                val tilesetFile = new File(tilesetDir + "/" + name)
                val imgFile = new File(imgDir + "/" + (xml \\ "image").head.attribute("source").get.text.split("/").last)
                Gzipper.decompressToFile(resource.getGzippedTilesetFile.getBackingArray, tilesetFile)
                println(s"Saved ${tilesetFile.getPath}")
                Gzipper.decompressToFile(resource.getGzippedSourceImagePng.getBackingArray, imgFile)
                println(s"Saved ${imgFile.getPath}")
                (resource.getResourceId, TileResourceWithSize(name, size))
            }
        ).toMap
    }

    private def writeMaps(editorMetadata: Map[String, Elem],
                          mapChunks: Map[String, Set[MapChunkEntity]],
                          resourcesWithSize: Map[Int, TileResourceWithSize]): Unit = {
        editorMetadata.foreach(f => {
            val name = f._1
            mapChunks.get(name) match {
                case Some(chunkEntities) =>
                    val mapData = MapData.fromChunks(f._2, chunkEntities.toSeq)
                    val mapFile = new File(mapDir + "/" + name)
                    mapData.writeToFile(mapFile, resourcesWithSize, "../" + MagicConstants.tilesetFolder + "/")
                    println(s"Saved ${mapFile.getPath}")

                case None =>
                    println(s"Map $name had metadata, but no associated chunk entities. Not generating a map file.")
            }
        })
    }

    private def parseSnapshot(snapshotPath: String): ParsedSnapshot = {
        val snapshotInputStream = new SnapshotInputStream(snapshotPath)
        var resources: Seq[GzippedResourceData] = Seq.empty
        var mapEditorMetadata: Map[String, Elem] = Map.empty
        val mapChunks = new mutable.HashMap[String, mutable.Set[MapChunkEntity]]() with mutable.MultiMap[String, MapChunkEntity]
        while (snapshotInputStream.hasNext) {
            val entity = snapshotInputStream.readEntity()
            if (entity.getValue.getComponentIds.contains(GzippedResource.COMPONENT_ID)) {
                val data: GzippedResourceData = entity.getValue
                  .get[GzippedResource, GzippedResourceData](GzippedResource.COMPONENT).get()
                resources = resources :+ data
            }
            if (entity.getValue.getComponentIds.contains(MapEditorMetadata.COMPONENT_ID)) {
                val metadata: MapEditorMetadataData = entity.getValue
                  .get[MapEditorMetadata, MapEditorMetadataData](MapEditorMetadata.COMPONENT).get()
                val xml = Gzipper.decompressToXml(metadata.getPayload.getBackingArray)
                mapEditorMetadata += (metadata.getMapName -> xml)
            }
            if (entity.getValue.getComponentIds.contains(MapChunk.COMPONENT_ID)) {
                val mapChunkData = entity.getValue
                  .get[MapChunk, MapChunkData](MapChunk.COMPONENT).get()
                // It should be a safe assumption that the entity has a position
                val position = entity.getValue
                  .get[Position, PositionData](Position.COMPONENT).get()
                val mce = MapChunkEntity.fromComponentAndPosition(
                    mapChunkData, position.getCoords)
                mapChunks.addBinding(mce.name, mce)
            }
        }
        ParsedSnapshot(resources, mapEditorMetadata, mapChunks.map(f => (f._1, f._2.toSet)).toMap)
    }

    private def initProject(): Unit = {
        new File(imgDir).mkdirs()
        new File(tilesetDir).mkdirs()
        new File(mapDir).mkdirs()
    }
}

object TiledProjectGenerator {

    case class ParsedSnapshot(resources: Seq[GzippedResourceData],
                              editorMetadata: Map[String, Elem],
                              mapChunks: Map[String, Set[MapChunkEntity]])

    case class TileResourceWithSize(name: String, size: Int)
}
