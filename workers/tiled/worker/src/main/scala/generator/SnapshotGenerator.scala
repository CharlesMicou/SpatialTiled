package generator

import java.io.File

import common.MagicConstants
import common.MagicConstants.{MAX_X_CHUNK, MAX_Z_CHUNK}
import improbable.worker.{Entity, EntityId, SnapshotInputStream, SnapshotOutputStream}
import tiled.map.MapChunk
import tiled.resource.GzippedResource
import util.SnapshotFilter

class SnapshotGenerator(resourcePath: String) {
    validateResourceFolder(resourcePath)

    def generateSnapshot(path: String, fromSnapshot: Option[String]): Unit = {
        val snapshotOutputStream = new SnapshotOutputStream(path)

        var lastEntityId: Long = fromSnapshot match {
            case Some(existingSnapshot) =>
                println(s"Filtering existing snapshot $existingSnapshot")
                val snapshotInputStream = new SnapshotInputStream(existingSnapshot)
                val offset = SnapshotFilter.removeEntitiesWithComponents(
                    snapshotInputStream, snapshotOutputStream, SnapshotGenerator.filteredComponents)
                snapshotInputStream.close()
                offset

            case None => 0
        }

        val tileResource: TileResourceDirectory = TileResourceDirectory.loadResourceDirectory(
            resourcePath + "/" + MagicConstants.tilesetFolder, lastEntityId)

        // Add the resource entities to the snapshot, and save the highest entity ID in use.
        lastEntityId = lastEntityId.max(writeResourceEntities(
            snapshotOutputStream, tileResource.resourceEntities))

        val maps: Seq[MapData] = loadMapDirectory(
            resourcePath + "/" + MagicConstants.mapFolder, tileResource)

        println("Writing to snapshot.")
        writeMapEntities(snapshotOutputStream, lastEntityId, maps, tileResource)
        snapshotOutputStream.close()
    }

    /** Returns the largest entity id used by resource entities */
    private def writeResourceEntities(snapshotOutputStream: SnapshotOutputStream,
                                      entities: Seq[(Long, Entity)]): Long = {
        var largestId: Long = 0
        entities.foreach(f => {
            largestId = largestId.max(f._1)
            snapshotOutputStream.writeEntity(new EntityId(f._1), f._2)
        })
        largestId
    }

    private def writeMapEntities(snapshotOutputStream: SnapshotOutputStream,
                                 entityIdOffset: Long,
                                 maps: Seq[MapData],
                                 tileResource: TileResourceDirectory): Unit = {
        // Write chunk data and count how many entity IDs were used in the process
        val usedEntityIds = maps
          .flatMap(mapData => mapData.toMapChunks(MAX_X_CHUNK, MAX_Z_CHUNK, tileResource))
          .zip(Stream.from(1))
          .count(f => {
              snapshotOutputStream.writeEntity(
                  new EntityId(entityIdOffset + f._2), f._1.toEntity(tileResource))
              true
          })

        // Write map metadata entities at the end
        maps.zip(Stream.from(1))
          .foreach(f => snapshotOutputStream.writeEntity(
              new EntityId(entityIdOffset + usedEntityIds + f._2), f._1.metadataEntity()))
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
              file.getName.equals(MagicConstants.tilesetFolder) ||
                file.getName.equals(MagicConstants.imgFolder) ||
                file.getName.equals(MagicConstants.mapFolder)
          }) == 3,
            s"Resource directory $path did not contain the required folders: " +
              s"${MagicConstants.tilesetFolder}, " +
              s"${MagicConstants.imgFolder}, " +
              s"${MagicConstants.mapFolder}")
    }

    private def loadMapDirectory(mapDirectoryPath: String,
                                 tileResource: TileResourceDirectory): Seq[MapData] = {
        val dir = new File(mapDirectoryPath)
        dir.listFiles
          .filter(f => f.isFile && f.getName.endsWith(".tmx"))
          .map(mapFile => MapData.fromFile(mapFile, tileResource))
    }
}

object SnapshotGenerator {
    val filteredComponents: Set[Int] = Set(MapChunk.COMPONENT_ID, GzippedResource.COMPONENT_ID)
}
