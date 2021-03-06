package common

import improbable.Coordinates

object CoordinatesHelper {
    implicit class ExtendedCoordinates(c1: Coordinates) {
        def +(c2: Coordinates): Coordinates = {
            val result = Coordinates.create()
            result.setX(c1.getX + c2.getX)
            result.setY(c1.getY + c2.getY)
            result.setZ(c1.getZ + c2.getZ)
            result
        }

        def -(c2: Coordinates): Coordinates = {
            val result = Coordinates.create()
            result.setX(c1.getX - c2.getX)
            result.setY(c1.getY - c2.getY)
            result.setZ(c1.getZ - c2.getZ)
            result
        }

        def apply(x: Double, y: Double, z: Double): Coordinates = {
            val result = Coordinates.create()
            result.setX(x)
            result.setY(y)
            result.setZ(z)
            result
        }

        def isTopLeftOf(c2: Coordinates): Boolean = {
            c1.getX <= c2.getX && c1.getZ >= c2.getZ
        }

        def isBottomRightOf(c2: Coordinates): Boolean = {
            c1.getX >= c2.getX && c1.getZ <= c2.getZ
        }
    }

    def makeCoordinates(x: Double, y: Double, z: Double): Coordinates = {
        val result = Coordinates.create()
        result.setX(x)
        result.setY(y)
        result.setZ(z)
        result
    }
}
