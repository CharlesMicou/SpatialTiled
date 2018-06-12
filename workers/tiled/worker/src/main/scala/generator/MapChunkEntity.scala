package generator

import java.util

import improbable._
import improbable.worker.Entity
import tiled.map.{MapChunk, MapChunkData, MapChunkProperties, TileLayer}
import tiled.resource.TileResource

import scala.collection.JavaConversions._

class MapChunkEntity(name: String,
                     origin: Coordinates,
                     width: Int,
                     height: Int,
                     mapLayers: Seq[MapLayer],
                     tileResourceDirectory: TileResourceDirectory) {

    def toEntity: Entity = {
        val entity = new Entity()
        entity.add(Metadata.COMPONENT, new MetadataData("MapChunk"))
        entity.add(EntityAcl.COMPONENT, makeEntityAcl)
        entity.add(Persistence.COMPONENT, new PersistenceData())
        entity.add(Position.COMPONENT, new PositionData(origin))
        val mapChunkData = new MapChunkData(
            makeMapProperties, makeRequiredResources, makeTileLayers)
        entity.add(MapChunk.COMPONENT, mapChunkData)
        entity
    }

    def makeEntityAcl: EntityAclData = {
        val readAcl = new WorkerRequirementSet(new java.util.ArrayList())
        val writeAcl = new util.HashMap[Integer, WorkerRequirementSet]()
        new EntityAclData(readAcl, writeAcl)
    }

    def makeMapProperties: MapChunkProperties = {
        val mapChunkProperties = MapChunkProperties.create()
        mapChunkProperties.setHeight(height)
        mapChunkProperties.setWidth(width)
        mapChunkProperties.setName(name)
        mapChunkProperties.setTileXDimension(1)
        mapChunkProperties.setTileZDimension(1)
        mapChunkProperties
    }

    def makeTileLayers: Seq[TileLayer] = {
        mapLayers.map { layer =>
            val tileLayer = TileLayer.create()
            tileLayer.setName(layer.name)
            tileLayer.setTiles(layer.data.flatten.toSeq)
            tileLayer.setId(layer.id)
            tileLayer
        }
    }

    def makeRequiredResources: Map[Integer, TileResource] = {
        tileResourceDirectory.resources
          .filter(resource => {
              mapLayers.exists(mapLayer => {
                  mapLayer.data.flatten.exists(tileId =>
                      tileId.getResourceId == resource.getResourceId)
              })
          })
          .map(resource => (resource.getResourceId: Integer, resource))
          .toMap
    }
}