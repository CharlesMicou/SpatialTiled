package generator

import java.io.File

import common.MagicConstants
import generator.TiledProjectGenerator.ParsedSnapshot
import improbable.worker.SnapshotInputStream
import tiled.resource.{GzippedResource, GzippedResourceData}
import util.Gzipper

class TiledProjectGenerator(outputDir: String) {
    private val imgDir = outputDir + "/" + MagicConstants.imgFolder
    private val tilesetDir = outputDir + "/" + MagicConstants.tilesetFolder
    private val mapDir = outputDir + "/" + MagicConstants.mapFolder
    initProject()

    def loadFromSnapshot(snapshotPath: String): Unit = {
        val parsedSnapshot = parseSnapshot(snapshotPath)
        writeResources(parsedSnapshot.resources)
    }

    private def writeResources(resources: Seq[GzippedResourceData]) = {
        resources.foreach(
            resource => {
                val xml = Gzipper.decompressToXml(resource.getGzippedTilesetFile.getBackingArray)
                // todo: validate presence of these attributes instead of exploding on arbitrary data
                val tilesetName = xml.attribute("name").get.text
                val imgName = (xml \\ "image").head.attribute("source").get.text.split("/").last

                Gzipper.decompressToFile(resource.getGzippedSourceImagePng.getBackingArray,
                    new File(imgDir + "/" + imgName))
                Gzipper.decompressToFile(resource.getGzippedTilesetFile.getBackingArray,
                    new File(tilesetDir + "/" + tilesetName + ".tsx"))
            }
        )
    }

    private def parseSnapshot(snapshotPath: String): ParsedSnapshot = {
        val snapshotInputStream = new SnapshotInputStream(snapshotPath)
        var resources: Seq[GzippedResourceData] = Seq.empty
        while (snapshotInputStream.hasNext) {
            val entity = snapshotInputStream.readEntity()
            if (entity.getValue.getComponentIds.contains(GzippedResource.COMPONENT_ID)) {
                // Type inference is wonky here
                val data: GzippedResourceData = entity.getValue
                  .get[GzippedResource, GzippedResourceData](GzippedResource.COMPONENT).get()
                resources = resources :+ data
            }
            // todo: map metadata entities
            // todo: map chunking by map id
        }
        // todo populate the rest of returned data
        ParsedSnapshot(resources)
    }

    private def initProject(): Unit = {
        new File(imgDir).mkdirs()
        new File(tilesetDir).mkdirs()
        new File(mapDir).mkdirs()
    }
}

object TiledProjectGenerator {

    case class ParsedSnapshot(resources: Seq[GzippedResourceData])

}