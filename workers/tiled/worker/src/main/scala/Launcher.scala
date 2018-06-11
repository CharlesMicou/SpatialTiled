import java.util

import generator.{SnapshotGenerator, TileResourceDirectory}
import improbable._
import improbable.worker.{Entity, EntityId, SnapshotOutputStream}
import tiled.map._
import tiled.resource.TileResource

import scala.collection.JavaConversions._

object Launcher extends App {
    /*val path = System.getProperty("user.dir") + "/test.snapshot"
    System.out.println(s"Outputting snapshot to $path")
    val snapshotOutputStream = new SnapshotOutputStream(path)
    snapshotOutputStream.writeEntity(new EntityId(1337), makeTestEntity)
    snapshotOutputStream.close()*/

    val path = getClass.getClassLoader.getResource("test").getFile
    System.out.println(s"Loading from $path")
    val s = new SnapshotGenerator(path)
    System.exit(0)

    def makeTestEntity: Entity = {
        val entity = new Entity()
        entity.add(Metadata.COMPONENT, new MetadataData("ScalaGeneratedEntity"))
        entity.add(EntityAcl.COMPONENT, makeEntityAcl)
        entity.add(Persistence.COMPONENT, new PersistenceData())
        entity.add(Position.COMPONENT, new PositionData(new Coordinates(1, 0, -1)))
        entity.add(MapChunk.COMPONENT, makeMapChunkData)
        entity
    }

    def makeEntityAcl: EntityAclData = {
        val readAcl = new WorkerRequirementSet(new java.util.ArrayList())
        val writeAcl = new util.HashMap[Integer, WorkerRequirementSet]()
        new EntityAclData(readAcl, writeAcl)
    }

    def makeMapChunkData: MapChunkData = {
        val properties = MapChunkProperties.create()
        properties.setName("Test map name")
        properties.setHeight(2)
        properties.setWidth(3)
        properties.setTileXDimension(1)
        properties.setTileZDimension(1)
        val tileLayer = TileLayer.create()
        tileLayer.setName("tile layer 1")
        tileLayer.setTiles(Seq(new TileId(1, 1), new TileId(1, 2)))
        val tileResource = TileResource.create()
        tileResource.setResourceId(1)
        new MapChunkData(
            properties,
            Map((1: Integer) -> tileResource),
            Seq(tileLayer))
    }
}
