import generator.SnapshotGenerator

object Launcher extends App {
    private case class GenerateSnapshotConfig(resourcePath: String, outputFile: String, existingSnapshot: Option[String])
    private case class GenerateMapsConfig(inputFile: String, outputResourceDir: String)

    private val usageMsg: String = {
        "==== Usage ====\n" +
        "-- Snapshot generation --\n" +
        "To generate a snapshot: \"toSnapshot /path/to/project\"\n" +
        "To modify an existing snapshot: \"toSnapshot /path/to/project /path/to/snapshot.snapshot\"\n" +
        "-- Map generation --\n" +
        "To generate maps from a snapshot: \"toMaps and then other things I need to implement\""
    }

    parseArgs match {
        case toSnapshot: GenerateSnapshotConfig =>
            val snapshotGenerator = new SnapshotGenerator(toSnapshot.resourcePath)
            println("Starting snapshot generation.")
            snapshotGenerator.generateSnapshot(toSnapshot.outputFile, toSnapshot.existingSnapshot)

        case toMaps: GenerateMapsConfig =>
            println("Conversion to maps from snapshot not yet implemented.")
            ???

        case _ =>
            println(usageMsg)
            System.exit(1)
    }

    // todo: make all of this less hacky
    private def parseArgs: Any = {
        args(0) match {
            case "toSnapshot" =>
                val resourcePath = args(1) + "/worlds/test_world"
                val outputFile = args(1) + "/snapshots/test_world.snapshot"
                if (args.length == 3) {
                    // this could be better...
                    val existingSnapshot = args(2)
                    GenerateSnapshotConfig(resourcePath, outputFile, Some(existingSnapshot))
                } else {
                    GenerateSnapshotConfig(resourcePath, outputFile, Option.empty)
                }

            case "toMaps" =>
                GenerateMapsConfig("not", "implemented")

            case _ =>
                println(usageMsg)
                println("Failed to parse args: ")
                args.foreach(println)
        }
    }
}
