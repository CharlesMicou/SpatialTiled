package generator

import tiled.map.{MapChunkData, MapChunkProperties, TileLayer}

class TileMapChunk(mapName: String,
                   width: Int,
                   height: Int) {

    def toComponent: MapChunkData = {
        val properties = MapChunkProperties.create()
        properties.setName(mapName)
        properties.setWidth(width)
        properties.setHeight(height)
        properties.setTileXDimension(1)
        properties.setTileZDimension(1)

        val tileLayer = TileLayer.create()
        /* todo how do we do these? */
        ???
    }

}
