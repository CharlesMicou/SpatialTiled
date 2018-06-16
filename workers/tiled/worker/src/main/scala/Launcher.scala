import generator.SnapshotGenerator

object Launcher extends App {
    private case class GenerateSnapshotConfig(resourcePath: String, outputFile: String)
    private case class GenerateMapsConfig(inputFile: String, outputResourceDir: String)

    val initialEntityId = 1234

    parseArgs match {
        case toSnapshot: GenerateSnapshotConfig =>
            println("Starting snapshot generation.")
            val snapshotGenerator = new SnapshotGenerator(toSnapshot.resourcePath)
            snapshotGenerator.writeSnapshot(toSnapshot.outputFile, initialEntityId)

        case toMaps: GenerateMapsConfig =>
            println("Conversion to maps from snapshot not yet implemented.")
            ???

        case _ =>
            println("Usage: <toSnapshot/toMaps> <project path>")
            System.exit(1)
    }

    private def parseArgs: Any = {
        args(0) match {
            case "toSnapshot" =>
                val resourcePath = args(1) + "/worlds/test_world"
                val outputFile = args(1) + "/snapshots/test_world.snapshot"
                GenerateSnapshotConfig(resourcePath, outputFile)

            case "toMaps" =>
                GenerateMapsConfig("not", "implemented")

            case _ =>
                println("Failed to parse args: ")
                args.foreach(println)
        }
    }
}
