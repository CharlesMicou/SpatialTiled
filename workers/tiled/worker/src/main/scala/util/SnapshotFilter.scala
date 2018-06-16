package util

import improbable.worker.{SnapshotInputStream, SnapshotOutputStream}

object SnapshotFilter {
    /**
      * Remove all entities from a snapshot which have any of the filtered components.
      *
      * Returns the highest entity id in the output snapshot.
      */
    def removeEntitiesWithComponents(snapshotInputStream: SnapshotInputStream,
                                     snapshotOutputStream: SnapshotOutputStream,
                                     filteredComponents: Set[Int]): Long = {
        var maxEntityId: Long = 1
        while (snapshotInputStream.hasNext) {
            val entity = snapshotInputStream.readEntity()
            if (!filteredComponents.exists(component =>
                entity.getValue.getComponentIds.contains(component))) {
                snapshotOutputStream.writeEntity(entity.getKey, entity.getValue)
                maxEntityId = maxEntityId.max(entity.getKey.getInternalId)
            }
        }
        maxEntityId
    }
}