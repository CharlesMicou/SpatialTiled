package generator

import java.io.File

import improbable.worker.{EntityId, SnapshotOutputStream}

class SnapshotGenerator(resourceDir: String) {
    validateResourceFolder(resourceDir)
    val resourceDirectory: TileResourceDirectory = TileResourceDirectory.parseResourceDirectory(
        resourceDir + s"/${SnapshotGenerator.tilesetFolder}")

    val mapData: MapData = MapData.fromFile(
        new File(resourceDir + "/" + SnapshotGenerator.mapFolder + "/map1.tmx"), resourceDirectory)

    def writeSnapshot(path: String): Unit = {
        val snapshotOutputStream = new SnapshotOutputStream(path)
        mapData.toMapChunks(5, 5, resourceDirectory)
          .zip(Stream.from(1337)).foreach(
            f => snapshotOutputStream.writeEntity(new EntityId(f._2), f._1.toEntity))
        snapshotOutputStream.close()
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
}

object SnapshotGenerator {
    val tilesetFolder = "tilesets"
    val imgFolder = "img"
    val mapFolder = "maps"
}
