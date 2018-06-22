package generator

import java.io.File

import common.MagicConstants

class TiledProjectGenerator(outputDir: String) {
    setupProject(outputDir)


    private def setupProject(dir: String): Unit = {
        val projectDir = new File(dir)
        if (projectDir.exists()) {
            assert(projectDir.isDirectory, s"$projectDir exists but is not a folder.")
        }

        val imgDir = projectDir + "/" + MagicConstants.imgFolder
        new File(imgDir).mkdirs()
        val tilesetDir = projectDir + "/" + MagicConstants.tilesetFolder
        new File(tilesetDir).mkdirs()
        val mapDir = projectDir + "/" + MagicConstants.mapFolder
        new File(mapDir).mkdirs()
    }
}
