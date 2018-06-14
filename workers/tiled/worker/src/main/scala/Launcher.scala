import generator.SnapshotGenerator


object Launcher extends App {
    val resourcePath = getClass.getClassLoader.getResource("test").getFile
    System.out.println(s"Loading from $resourcePath")
    val snapshotGenerator = new SnapshotGenerator(resourcePath)
    val destinationFile = System.getProperty("user.dir") + "/test.snapshot"
    snapshotGenerator.writeSnapshot(destinationFile)
    System.exit(0)
}
