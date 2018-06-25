package generator

import java.io.File

import common.MagicConstants
import improbable.worker.Entity
import tiled.map.TileId
import tiled.resource.{LocalResource, RemoteResource, TileResource}

import scala.xml.XML

class TileResourceDirectory(tilesetNameToResources: Map[String, TileResource],
                            val resourceEntities: Seq[(Long, Entity)]) {

    def tileIdFromMapFileMapping(gid: Int, firstGidToTilesetName: Map[Int, String]): TileId = {
        if (gid == 0) {
            /* Special case of empty tile */
            makeTileId(0, 0)
        } else {
            val (_, gidBelow) = firstGidToTilesetName.toSeq.partition(f => f._1 > gid)
            val resource = gidBelow.maxBy(f => f._1)
            getTileId(resource._2, gid - resource._1)
        }
    }

    private def getTileId(tilesetFile: String, innerId: Int): TileId = {
        tilesetNameToResources.get(tilesetFile) match {
            case Some(resource) =>
                makeTileId(resource.getResourceId, innerId)
            case None =>
                throw new RuntimeException(s"$tilesetFile is not a known tileset in the resource directory")
        }
    }

    def resources: Seq[TileResource] = {
        tilesetNameToResources.values.toSeq
    }

    private def makeTileId(resourceId: Int, innerId: Int): TileId = {
        val tileId = TileId.create()
        tileId.setResourceId(resourceId)
        tileId.setInnerId(innerId)
        tileId
    }
}

object TileResourceDirectory {
    def loadResourceDirectory(path: String, initialEntityId: Long): TileResourceDirectory = {
        val dir = new File(path)
        val parsedData = dir.listFiles
          .filter(file => file.isFile && file.getName.endsWith(".tsx"))
          .zip(Stream.from(1))
          .map(f => tsxToResource(f._1, f._2, initialEntityId + f._2))
        new TileResourceDirectory(
            parsedData.map(f => (f._1, f._2)).toMap,
            parsedData.map(f => (f._2.getRemoteResource.getResourceEntityId, f._3)))
    }

    private def tsxToResource(file: File, resourceId: Int, entityId: Long): (String, TileResource, Entity) = {
        val tileResource = TileResource.create()
        tileResource.setResourceId(resourceId)
        // For now, all resources are remote until I figure out a nice way to declare
        // dependencies on local resources for snapshot regeneration
        val tilesetFileName = file.getName.split("/").last
        println(s"Loaded $tilesetFileName into tileset resources.")
        val sourceImage = (XML.loadFile(file) \\ "image")
          .map(node => node.attribute("source").get.text.split("/").last)
          .headOption.getOrElse("")
        // tileResource.setLocalResource(new LocalResource(tilesetFileName, sourceImage))
        tileResource.setRemoteResource(new RemoteResource(entityId))

        // todo use the relative path instead of hacks
        val imgFile = new File(new File(file.getParent).getParent + "/" + MagicConstants.imgFolder + "/" + sourceImage)

        (file.getName, tileResource, ResourceEntity.makeResourceEntity(file, imgFile, resourceId))
    }
}