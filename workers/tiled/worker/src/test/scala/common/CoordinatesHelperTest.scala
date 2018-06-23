package common

import base.BaseTest
import CoordinatesHelper._

class CoordinatesHelperTest extends BaseTest {

    private val coords1 = makeCoordinates(0, 0, 0)
    private val coords2 = makeCoordinates(0, 0, 1)
    private val coords3 = makeCoordinates(1, 0, 0)
    private val coords4 = makeCoordinates(1, 0, 1)

    "isTopLeftOf" should "correctly determine whether coordinates are top left of" in {
        coords2.isTopLeftOf(coords3) should be (true)
        coords2.isTopLeftOf(coords4) should be (true)
        coords2.isTopLeftOf(coords1) should be (true)

        // along a top-right diagonal, neither is top left
        coords1.isTopLeftOf(coords4) should be (false)
        coords4.isTopLeftOf(coords1) should be (false)
    }
}
