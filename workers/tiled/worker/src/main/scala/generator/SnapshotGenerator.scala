package generator

import java.io.File

class SnapshotGenerator(resourceDir: String) {
    validatePath(resourceDir)
    val resourceDirectory = TileResourceDirectory.parseResourceDirectory(
        resourceDir + s"/${SnapshotGenerator.tilesetFolder}")

    MapData.fromFile(new File(resourceDir + "/" + SnapshotGenerator.mapFolder + "/map1.tmx"), resourceDirectory)


    private def validatePath(path: String): Unit = {
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