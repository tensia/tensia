package pl.edu.agh.tensia.graphml

import pl.edu.agh.tensia.TensorNetwork
import pl.edu.agh.tensia.tensor.{Dimension, NDTensor}

import scala.collection.mutable
import scala.xml._

object GraphMLParser {
  def tensorNetworkFromXML(xml: Elem): TensorNetwork[NDTensor] = {
    val nodes = parseNodes(xml \ "node")
    val edges = parseEdges(xml \ "edge")
    for (edge <- edges) {
      val dimension: Dimension = nodes(edge.srcId).dims(edge.srcDimIndex)
      nodes(edge.destId).dims(edge.destDimIndex) = dimension
    }

    TensorNetwork(nodes.values.map(_.toTensor).toSeq)
  }

  def parseNodes(nodes: NodeSeq): mutable.Map[String, TensorNode] = {
    mutable.Map(nodes.map(node => (node \@ "id") -> parseNode(node)):_*)
  }

  def parseNode(node: Node): TensorNode = {
    val dims: Array[Dimension] = getDataById(node, "shape")
      .split(',')
      .map(Integer.parseInt)
      .map(Dimension(_))

    val dataPath = getDataById(node, "dataPath")

    TensorNode(dataPath, dims)
  }

  def parseEdges(nodeSeq: NodeSeq): Seq[EdgeNode] = {
    nodeSeq.map { node =>
      val srcId   = node \@ "source"
      val destId  = node \@ "target"
      val srcDim  = Integer.parseInt(getDataById(node, "srcDim"))
      val destDim = Integer.parseInt(getDataById(node, "destDim"))
      EdgeNode(srcId, srcDim, destId, destDim)
    }
  }

  private def getDataById(node: Node, id: String): String =
    (node \ "data").find(_ \@ "id" == id).get.text
}
