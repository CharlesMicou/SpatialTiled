package common

import base.BaseTest
import tiled.map.TileId

class TileLayerTest extends BaseTest {

    private val tileId1 = new TileId(1, 6)
    private val tileId2 = new TileId(2, 5)
    private val tileId3 = new TileId(3, 4)
    private val tileId4 = new TileId(4, 3)
    private val tileId5 = new TileId(5, 2)
    private val tileId6 = new TileId(6, 1)

    private val layerData = Map(
        (0, 0) -> tileId1,
        (0, 1) -> tileId2,
        (0, 2) -> tileId3,
        (1, 0) -> tileId4,
        (1, 1) -> tileId5,
        (1, 2) -> tileId6)

    private val expectedOrderingRaw = Seq(
        tileId1,
        tileId4,
        tileId2,
        tileId5,
        tileId3,
        tileId6)

    private val subSectionedData = Map(
        (0, 0) -> tileId1,
        (0, 1) -> tileId2,
        (0, 2) -> tileId3)

    private val tileLayer = new TileLayer(layerData, 2, 3)

    "Converting to the raw format" should "yield a sequence in the convention right-down" in {
        tileLayer.toRaw should be (expectedOrderingRaw)
    }

    "Extracting a subsection" should "provide exactly the tiles in that subsection" in {
        tileLayer.subSection(0, 0, 1, 3).toRaw should be (new TileLayer(subSectionedData, 1, 3).toRaw)
    }

    "Extracting a subsection" should "offset tiles in the subsection" in {
        val tile = tileLayer.subSection(1, 0, 2, 3).getTile(0, 0)
        tile.get should be (tileId4)
    }
}
