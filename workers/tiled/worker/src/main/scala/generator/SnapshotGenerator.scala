package generator

import java.io.File

import common.MagicConstants.{MAX_X_CHUNK, MAX_Z_CHUNK}
import improbable.worker.{EntityId, SnapshotInputStream, SnapshotOutputStream}
import tiled.map.MapChunk
import tiled.resource.GzippedResource
import util.SnapshotFilter

class SnapshotGenerator(resourcePath: String) {
    validateResourceFolder(resourcePath)
    private val tileResource: TileResourceDirectory = TileResourceDirectory.parseResourceDirectory(
        resourcePath + "/" + SnapshotGenerator.tilesetFolder)

    private val maps: Seq[MapData] = loadMapDirectory(
        resourcePath + "/" + SnapshotGenerator.mapFolder)

    def generateSnapshot(path: String, fromSnapshot: Option[String]): Unit = {
        val snapshotOutputStream = new SnapshotOutputStream(path)

        val entityIdOffset: Long = fromSnapshot match {
            case Some(existingSnapshot) =>
                val snapshotInputStream = new SnapshotInputStream(existingSnapshot)
                val offset = SnapshotFilter.removeEntitiesWithComponents(
                    snapshotInputStream, snapshotOutputStream, SnapshotGenerator.filteredComponents)
                snapshotInputStream.close()
                offset

            case None => 0
        }

        writeEntities(snapshotOutputStream, entityIdOffset)
        snapshotOutputStream.close()
    }

    private def writeEntities(snapshotOutputStream: SnapshotOutputStream,
                              entityIdOffset: Long): Unit = {
        maps.foreach {
            mapData =>
                mapData.toMapChunks(MAX_X_CHUNK, MAX_Z_CHUNK, tileResource)
                  .zip(Stream.from(1))
                  .foreach(f => snapshotOutputStream.writeEntity(
                      new EntityId(entityIdOffset + f._2), f._1.toEntity))
        }
    }

    private def validateResourceFolder(path: String): Unit = {
        println(s"Loading resources from $path")
        val dir = new File(path)
        if (!dir.exists() || !dir.isDirectory) {
            throw new RuntimeException(s"$path is not a folder or does not exist.")
        }
        assert(dir.listFiles()
          .filter(file => file.isDirectory)
          .count(file => {
              file.getName.equals(SnapshotGenerator.tilesetFolder) ||
                file.getName.equals(SnapshotGenerator.imgFolder) ||
                file.getName.equals(SnapshotGenerator.mapFolder)
          }) == 3,
            s"Resource directory $path did not contain the required folders: " +
              s"${SnapshotGenerator.tilesetFolder}, " +
              s"${SnapshotGenerator.imgFolder}, " +
              s"${SnapshotGenerator.mapFolder}")
    }

    private def loadMapDirectory(mapDirectoryPath: String): Seq[MapData] = {
        val dir = new File(mapDirectoryPath)
        dir.listFiles
          .filter(f => f.isFile && f.getName.endsWith(".tmx"))
          .map(mapFile => MapData.fromFile(mapFile, tileResource))
    }
}

object SnapshotGenerator {
    val tilesetFolder = "tilesets"
    val imgFolder = "img"
    val mapFolder = "maps"
    val filteredComponents: Set[Int] = Set(MapChunk.COMPONENT_ID, GzippedResource.COMPONENT_ID)
}
