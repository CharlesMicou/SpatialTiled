import generator.{SnapshotGenerator, TiledProjectGenerator}

object Launcher extends App {
    private case class GenerateSnapshotConfig(resourcePath: String, outputFile: String, existingSnapshot: Option[String])
    private case class GenerateMapsConfig(inputFile: String, outputResourceDir: String)

    private val usageMsg: String = {
        "==== Usage ====\n" +
        "-- Snapshot generation --\n" +
        "To generate a snapshot: \"toSnapshot /path/to/world /path/to/output.snapshot\"\n" +
        "To modify an existing snapshot: \"toSnapshot /path/to/world /path/to/output.snapshot /path/to/input.snapshot\"\n" +
        "-- Map generation --\n" +
        "To generate maps from a snapshot: \"toMaps /path/to/snapshot.snapshot /path/to/output\""
    }

    parseArgs match {
        case toSnapshot: GenerateSnapshotConfig =>
            println(s"Generating snapshot: ${toSnapshot.outputFile}.")
            val snapshotGenerator = new SnapshotGenerator(toSnapshot.resourcePath)
            snapshotGenerator.generateSnapshot(toSnapshot.outputFile, toSnapshot.existingSnapshot)

        case toMaps: GenerateMapsConfig =>
            println(s"Generating tiled project: ${toMaps.outputResourceDir}")
            val tiledProjectGenerator = new TiledProjectGenerator(toMaps.outputResourceDir)
            tiledProjectGenerator.loadFromSnapshot(toMaps.inputFile)

        case _ =>
            println(usageMsg)
            System.exit(1)
    }

    // todo: make all of this less hacky
    private def parseArgs: Any = {
        args(0) match {
            case "toSnapshot" =>
                val resourcePath = args(1)
                val outputFile = args(2)
                if (args.length == 4) {
                    // this could be better...
                    val existingSnapshot = args(3)
                    GenerateSnapshotConfig(resourcePath, outputFile, Some(existingSnapshot))
                } else {
                    GenerateSnapshotConfig(resourcePath, outputFile, Option.empty)
                }

            case "toMaps" =>
                GenerateMapsConfig(args(1), args(2))

            case _ =>
                println(usageMsg)
                println("Failed to parse args: ")
                args.foreach(println)
        }
    }
}
