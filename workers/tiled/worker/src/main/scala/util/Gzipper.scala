package util

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.nio.file.{Files, Path}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.xml.{Elem, XML}

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

    def decompressToFile(input: Array[Byte], output: File): Unit = {
        val inputAsStream = new GZIPInputStream(new ByteArrayInputStream(input))
        Files.copy(inputAsStream, output.toPath)
        inputAsStream.close()
        // todo: figure out if we want to decompress straight to file
        // or an intermediate representation
    }

    def decompressToXml(input: Array[Byte]): Elem = {
        val inputAsStream = new GZIPInputStream(new ByteArrayInputStream(input))
        val result = XML.load(inputAsStream)
        inputAsStream.close()
        result
    }
}
