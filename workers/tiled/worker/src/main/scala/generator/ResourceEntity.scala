package generator

import common.MagicConstants.{READ_ATTRIBUTES, RESOURCE_COORDINATES, WRITE_ATTRIBUTE}
import improbable._
import improbable.worker.{Bytes, Entity}
import tiled.resource.{GzippedResource, GzippedResourceData}

import scala.collection.JavaConversions._

object ResourceEntity {
    def makeResourceEntity(): Entity = {
        // todo: actually pass in the resources here
        val entity = new Entity
        entity.add(Metadata.COMPONENT, new MetadataData("TileResource"))
        entity.add(EntityAcl.COMPONENT, makeEntityAcl())
        entity.add(Persistence.COMPONENT, new PersistenceData())
        entity.add(Position.COMPONENT, new PositionData(RESOURCE_COORDINATES))
        entity.add(GzippedResource.COMPONENT, makeGzippedResource())
        entity
    }

    private def makeGzippedResource(): GzippedResourceData = {
        val resource = GzippedResourceData.create()
        val dummyData: Bytes = Bytes.fromBackingArray(Array(123.toByte))
        resource.setGzippedSourceImagePng(dummyData)
        resource.setGzippedTilesetFile(dummyData)
        resource
    }

    private def makeEntityAcl(): EntityAclData = {
        val readAcl = new WorkerRequirementSet(
            READ_ATTRIBUTES.map(attribute => new WorkerAttributeSet(Seq(attribute))))
        val writeAcl: Map[Integer, WorkerRequirementSet] = WRITE_ATTRIBUTE match {
            case Some(attribute) =>
                Map((GzippedResource.COMPONENT_ID: Integer) ->
                  new WorkerRequirementSet(Seq(new WorkerAttributeSet(Seq(attribute)))))
            case None =>
                Map.empty
        }
        new EntityAclData(readAcl, writeAcl)
    }
}
