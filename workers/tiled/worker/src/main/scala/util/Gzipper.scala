package util

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.Files
import java.util.zip.GZIPOutputStream

object Gzipper {

    // todo: actually test this thoroughly, you lazy maniac
    def compress(file: File): Array[Byte] = {
        val bytes = Files.readAllBytes(file.toPath)
        val bos = new ByteArrayOutputStream(bytes.length)
        val gzip = new GZIPOutputStream(bos)
        gzip.write(bytes)
        gzip.close()
        val compressed = bos.toByteArray
        bos.close()
        compressed
    }

    def decompress(input: Array[Byte]) = {
        // todo: figure out if we want to decompress straight to file
        // or an intermediate representation
        ???
    }
}
