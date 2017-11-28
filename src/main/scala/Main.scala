import java.nio.file.Paths

import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.graphml.{GraphMLReader, GraphMLWriter}
import pl.edu.agh.tensia.tensor.NDTensor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.xml.XML

object Main extends App {
  if (args.length != 2) {
    println("Invalid arguments")
    sys.exit(1)
  }
  val fileIn = Paths.get(args(0))
  val fileOut = Paths.get(args(1))
  val xml = XML.loadFile(fileIn.toFile)
  val (tn, locked) = GraphMLReader.tensorNetworkFromXML(xml)

  implicit val orderFinder = BFSOrderFinder
  val writer = GraphMLWriter(fileOut.toString)

  import scala.concurrent.ExecutionContext.Implicits.global

  val res: Future[Unit] = if (locked.isEmpty) {
    tn.contract
      .mapTo[NDTensor]
      .map(writer.write)
  } else {
    tn.contract(locked)
      .mapTo[Seq[NDTensor]]
      .map(writer.write)
  }

  Await.ready(res, Duration.Inf)
  sys.exit(0)
}
