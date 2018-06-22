package util

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.nio.file.Files
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.xml.{Elem, XML}

object Gzipper {
    def compress(file: File): Array[Byte] = {
        val bytes = Files.readAllBytes(file.toPath)
        compress(bytes)
    }

    def compress(input: Array[Byte]): Array[Byte] = {
        val bos = new ByteArrayOutputStream(input.length)
        val gzip = new GZIPOutputStream(bos)
        gzip.write(input)
        gzip.close()
        val compressed = bos.toByteArray
        bos.close()
        compressed
    }

    def decompressToFile(input: Array[Byte], output: File): Unit = {
        val inputAsStream = new GZIPInputStream(new ByteArrayInputStream(input))
        Files.copy(inputAsStream, output.toPath)
        inputAsStream.close()
    }

    def decompressToXml(input: Array[Byte]): Elem = {
        val inputAsStream = new GZIPInputStream(new ByteArrayInputStream(input))
        val result = XML.load(inputAsStream)
        inputAsStream.close()
        result
    }
}
