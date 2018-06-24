package generator

import generator.LocalTileResourceMapping.TileResourceAndInitialId
import generator.TiledProjectGenerator.TileResourceWithSize
import tiled.map.TileId

/**
  * A mapping from a snapshot's tile resource id to a single map's csv identifier.
  */
class LocalTileResourceMapping(tileResourcesToInitialIds: Map[Int, TileResourceAndInitialId]) {

    def getDependenciesAndInitialIds: Seq[(Int, String)] = {
        tileResourcesToInitialIds.values.toIndexedSeq
          .sortBy(_.initialId)
          .map(f => (f.initialId, f.name))
    }

    def getLocalId(tileId: TileId): Int = {
        if (tileId.getResourceId == 0) {
            /* Special case of empty tile */
            0
        } else {
            tileResourcesToInitialIds(tileId.getResourceId).initialId + tileId.getInnerId
        }
    }
}

object LocalTileResourceMapping {
    def makeForResources(tiles: Set[TileId],
                         resourceIdToTileset: Map[Int, TileResourceWithSize]): LocalTileResourceMapping = {
        var nextInitialId = 1
        val resourcesWithInitialIds = resourceIdToTileset
          .filter(f => tiles.exists(tile => tile.getResourceId == f._1))
          .map(f => {
            val resourceWithInitialId = TileResourceAndInitialId(f._2.name, nextInitialId)
            nextInitialId = f._2.size + nextInitialId
            (f._1, resourceWithInitialId)
        })

        new LocalTileResourceMapping(resourcesWithInitialIds)
    }

    case class TileResourceAndInitialId(name: String, initialId: Int)
}
