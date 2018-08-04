package generator

import common.GridMap
import common.MagicConstants.{READ_ATTRIBUTES, TILE_X_DIMENSION, TILE_Z_DIMENSION, WRITE_ATTRIBUTE}
import improbable._
import improbable.worker.Entity
import tiled.map.{MapChunk, MapChunkData, MapChunkProperties, TileLayer}
import tiled.resource.TileResource

import scala.collection.JavaConversions._

class MapChunkEntity(val name: String,
                     val coordinates: Coordinates,
                     val width: Int,
                     val height: Int,
                     val mapLayers: Seq[MapLayer]) {

    def toEntity(tileResourceDirectory: TileResourceDirectory): Entity = {
        val entity = new Entity()
        entity.add(Metadata.COMPONENT, new MetadataData("MapChunk"))
        entity.add(EntityAcl.COMPONENT, makeEntityAcl)
        entity.add(Persistence.COMPONENT, new PersistenceData())
        entity.add(Position.COMPONENT, new PositionData(coordinates))
        val mapChunkData = new MapChunkData(
            makeMapProperties, makeRequiredResources(tileResourceDirectory), makeTileLayers)
        entity.add(MapChunk.COMPONENT, mapChunkData)
        entity
    }

    private def makeEntityAcl: EntityAclData = {
        val readAcl = new WorkerRequirementSet(
            READ_ATTRIBUTES.map(attribute => new WorkerAttributeSet(Seq(attribute))))
        val writeAcl: Map[Integer, WorkerRequirementSet] = WRITE_ATTRIBUTE match {
            case Some(attribute) =>
                Map((MapChunk.COMPONENT_ID: Integer) ->
                  new WorkerRequirementSet(Seq(new WorkerAttributeSet(Seq(attribute)))))
            case None =>
                Map.empty
        }
        new EntityAclData(readAcl, writeAcl)
    }

    private def makeMapProperties: MapChunkProperties = {
        val mapChunkProperties = MapChunkProperties.create()
        mapChunkProperties.setHeight(height)
        mapChunkProperties.setWidth(width)
        mapChunkProperties.setName(name)
        mapChunkProperties.setTileXDimension(TILE_X_DIMENSION)
        mapChunkProperties.setTileZDimension(TILE_Z_DIMENSION)
        mapChunkProperties
    }

    private def makeTileLayers: Seq[TileLayer] = {
        mapLayers.map { layer =>
            val tileLayer = TileLayer.create()
            tileLayer.setName(layer.name)
            tileLayer.setTiles(layer.tileData.toRaw)
            tileLayer.setId(layer.id)
            tileLayer.setRenderingProperties(layer.properties.renderingLayerProperties.toSchema)
            tileLayer.setGameplayProperties(layer.properties.gameplayLayerProperties.toSchema)
            tileLayer
        }
    }

    private def makeRequiredResources(tileResourceDirectory: TileResourceDirectory): Map[Integer, TileResource] = {
        tileResourceDirectory.resources
          .filter(resource => {
              mapLayers.exists(mapLayer => {
                  mapLayer.tileData.toRaw.exists(tileId =>
                      tileId.getResourceId == resource.getResourceId)
              })
          })
          .map(resource => (resource.getResourceId: Integer, resource))
          .toMap
    }
}

object MapChunkEntity {
    def fromComponentAndPosition(data: MapChunkData,
                                 position: Coordinates): MapChunkEntity = {
        // Note: the properties for tile x and z dimensions are currently ignored
        new MapChunkEntity(
            data.getProperties.getName,
            position,
            data.getProperties.getWidth,
            data.getProperties.getHeight,
            data.getTileLayers.map(tileLayer =>
                MapLayer(tileLayer.getName,
                    tileLayer.getId,
                    GridMap.fromRaw(tileLayer.getTiles,
                        data.getProperties.getWidth,
                        data.getProperties.getHeight),
                    LayerProperties.fromSchema(tileLayer.getRenderingProperties,
                        tileLayer.getGameplayProperties))))
    }
}
