package util

import java.io.File
import java.util.UUID

import org.scalatest._

import scala.xml.XML

class GzipperTest extends FlatSpec with Matchers {
    private lazy val tempDir = {
        val dir = File.createTempFile("scalatestdir", "")
        dir.delete()
        dir.mkdir()
        dir
    }

    "Compressing to an XML file and then decompressing the file" should "yield the data of the original file" in {
        val originalXml = <test attribute="some attribute data">More data in here</test>
        val f = makeFileName()
        XML.save(f.toString, originalXml)
        val compressed = Gzipper.compress(new File(f))
        val o = makeFileName()
        Gzipper.decompressToFile(compressed, new File(o))

        XML.load(o.toString) should be (originalXml)
    }

    "Decompressing a compressed file to xml data" should "yield the original data" in {
        val originalXml = <test attribute="some attribute data">More data in here</test>
        val f = makeFileName()
        XML.save(f.toString, originalXml)
        val compressed = Gzipper.compress(new File(f))

        Gzipper.decompressToXml(compressed) should be (originalXml)
    }

    private def makeFileName(): String = {
        tempDir.getPath + "/" + UUID.randomUUID().toString
    }
}
