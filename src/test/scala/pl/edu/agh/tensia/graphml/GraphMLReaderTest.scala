package pl.edu.agh.tensia.graphml

import java.nio.file.{Path, Paths}

import org.scalatest.{FunSpec, Matchers}
import pl.edu.agh.tensia.TensorNetwork
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.tensor.NDTensor

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.xml._

class GraphMLReaderTest extends FunSpec with Matchers {
  val aPath = Paths.get("src/test/resources/a.bin").toAbsolutePath
  val bPath = Paths.get("src/test/resources/b.bin").toAbsolutePath

  describe("GraphMLReaderTest") {

    it("should parseNodes") {
      val graph =
        <graph>
          <node id="a">
            <data key="shape">2,3</data>
            <data key="dataPath">{aPath.toString}</data>
          </node>
          <node id="b">
            <data key="shape">3,4</data>
            <data key="dataPath">{bPath.toString}</data>
          </node>
          <edge id="e1" source="a" target="b">
            <data key="srcDim">1</data>
            <data key="destDim">0</data>
          </edge>
        </graph>

      val res = GraphMLReader.parseNodes(graph \ "node")

      res.get("a") should not be empty
      res.get("b") should not be empty
      //TODO: more checks
    }

    it("should parseNode") {
      val node =
        <node>
          <data key="shape">2,2</data>
          <data key="dataPath">/path/to/data</data>
        </node>

      val res = GraphMLReader.parseNode(node)
      res.dims should have length 2
      res.dims(0) should have size 2
      res.dims(1) should have size 2
      res.dims(0) should not equal res.dims(1)

      res.locked should be (false)

      res.dataPath should be ("/path/to/data")
    }
    it("should parse locked node") {
      val node =
        <node>
          <data key="shape">2,2</data>
          <data key="dataPath">/path/to/data</data>
          <data key="locked" />
        </node>

      val res = GraphMLReader.parseNode(node)
      res.dims should have length 2
      res.dims(0) should have size 2
      res.dims(1) should have size 2
      res.dims(0) should not equal res.dims(1)

      res.locked should be (true)

      res.dataPath should be ("/path/to/data")
    }

    it("should parseEdges") {
      val graph =
        <graph>
          <edge id="e0" source="n0" target="n1">
            <data key="srcDim">1</data>
            <data key="destDim">2</data>
          </edge>
        </graph>

      val res = GraphMLReader.parseEdges(graph \ "edge")
      res should have length 1
      res.head should equal (EdgeNode("n0", 1, "n1", 2))
    }

    it("should create tensorNetworkFromGraph") {
      val graph =
        <graph>
          <node id="a">
            <data key="shape">2,3</data>
            <data key="dataPath">{aPath.toString}</data>
          </node>
          <node id="b">
            <data key="shape">3,4</data>
            <data key="dataPath">{bPath.toString}</data>
          </node>
          <edge id="e1" source="a" target="b">
            <data key="srcDim">1</data>
            <data key="destDim">0</data>
          </edge>
        </graph>

      val res: TensorNetwork[NDTensor] = GraphMLReader.tensorNetworkFromGraph(graph)._1

      res.tensors should have length 2
      //TODO: Check result
      Await.result(res.contract(BFSOrderFinder), Duration.Inf)
    }

  }
}
