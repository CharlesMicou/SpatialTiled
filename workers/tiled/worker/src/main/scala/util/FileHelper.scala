package util

import java.io.File

import org.apache.commons.io.FileUtils


object FileHelper {
    def deleteDirectoryIfExists(abspath: String): Unit = {
        val file = new File(abspath)
        if (file.exists() && file.isDirectory) {
            println(s"WARN: deleting existing directory $abspath")
            FileUtils.deleteDirectory(file)
        }
    }
}
