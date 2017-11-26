import java.nio.file.Paths

import pl.edu.agh.tensia.TensorNetwork
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.graphml.{GraphMLReader, GraphMLWriter}
import pl.edu.agh.tensia.tensor.NDTensor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.xml.XML

object Main extends App {
  val pathIn = Paths.get("src/main/resources/example.graphml")
  val pathOut = Paths.get("/tmp/out.graphml")
  val xml = XML.loadFile(pathIn.toFile)
  val (tn, locked) = GraphMLReader.tensorNetworkFromXML(xml)

  implicit val orderFinder = BFSOrderFinder
  val writer = GraphMLWriter(pathOut.toString)

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
  System.exit(0)
}
