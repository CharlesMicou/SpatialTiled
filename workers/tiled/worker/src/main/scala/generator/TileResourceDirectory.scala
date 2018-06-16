package generator

import java.io.File

import tiled.map.TileId
import tiled.resource.{LocalResource, TileResource}

import scala.xml.XML

class TileResourceDirectory(tilesetNameToResources: Map[String, TileResource]) {

    def tileIdFromMapFileMapping(gid: Int, firstGidToTilesetName: Map[Int, String]): TileId = {
        if (gid == 0) { /* Special case of empty tile */
            makeTileId(0, 0)
        } else {
            val (_, gidBelow) = firstGidToTilesetName.toSeq.partition(f => f._1 > gid)
            val resource = gidBelow.maxBy(f => f._1)
            getTileId(resource._2, gid - resource._1)
        }
    }

    def getTileId(tilesetFile: String, innerId: Int): TileId = {
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
    def parseResourceDirectory(path: String): TileResourceDirectory = {
        val dir = new File(path)
        new TileResourceDirectory(dir.listFiles.zip(Stream.from(1))
          .map(f => tsxToTileResource(f._1, f._2))
          .collect {case Some((name, tileResource)) => (name, tileResource)}
          .toMap)
    }

    private def tsxToTileResource(file: File, resourceId: Int): Option[(String, TileResource)] = {
        if (file.isFile && file.getName.endsWith(".tsx")) {
            val tileResource = TileResource.create()
            tileResource.setResourceId(resourceId)
            // Todo: decide between local and remote resources.
            // For now, just assume resources are local.
            val tilesetFileName = file.getName.split("/").last
            val xml = (XML.loadFile(file) \\ "image").map(
                node => node.attribute("source").get.text.split("/").last)
            tileResource.setLocalResource(
                new LocalResource(tilesetFileName, xml.headOption.getOrElse("")))
            Option((file.getName, tileResource))
        } else {
            Option.empty
        }
    }
}