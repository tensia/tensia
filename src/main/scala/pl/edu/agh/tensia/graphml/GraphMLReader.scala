package pl.edu.agh.tensia.graphml

import pl.edu.agh.tensia.TensorNetwork
import pl.edu.agh.tensia.tensor.{Dimension, NDTensor}

import scala.collection.mutable
import scala.xml._

object GraphMLReader {
  def tensorNetworkFromXML(xml: Elem): (TensorNetwork[NDTensor], Seq[NDTensor]) = {
    tensorNetworkFromGraph(xml \ "graph")
  }

  def tensorNetworkFromGraph(xml: NodeSeq): (TensorNetwork[NDTensor], Seq[NDTensor]) = {
    val nodesMap = parseNodes(xml \ "node")
    val edges = parseEdges(xml \ "edge")
    for (edge <- edges) {
      val dimension: Dimension = nodesMap(edge.srcId).dims(edge.srcDimIndex)
      nodesMap(edge.destId).dims(edge.destDimIndex) = dimension
    }

    val (lockedNodes, unlockedNodes) = nodesMap.values
      .partition(_.locked)

    val (lockedTensors, unlockedTensors) = (
      lockedNodes.map(_.toTensor).toSeq,
      unlockedNodes.map(_.toTensor).toSeq,
    )

    (TensorNetwork(unlockedTensors ++ lockedTensors), lockedTensors)
  }

  def parseNodes(nodes: NodeSeq): mutable.Map[String, TensorNode] = {
    mutable.Map(nodes.map(node => (node \@ "id") -> parseNode(node)):_*)
  }

  def parseNode(node: Node): TensorNode = {
    val dims: Array[Dimension] = getDataByKey(node, "shape")
      .split(',')
      .map(Integer.parseInt)
      .map(Dimension(_))

    val dataPath = getDataByKey(node, "dataPath")

    val locked = tryGetDataNodeByKey(node, "locked").isDefined

    TensorNode(dataPath, dims, locked)
  }

  def parseEdges(nodeSeq: NodeSeq): Seq[EdgeNode] = {
    nodeSeq.map { node =>
      val srcId   = node \@ "source"
      val destId  = node \@ "target"
      val srcDim  = Integer.parseInt(getDataByKey(node, "srcDim"))
      val destDim = Integer.parseInt(getDataByKey(node, "destDim"))
      EdgeNode(srcId, srcDim, destId, destDim)
    }
  }

  private def getDataByKey(node: Node, id: String): String =
    (node \ "data").find(_ \@ "key" == id).get.text

  private def tryGetDataNodeByKey(node: Node, id: String): Option[Node] =
    (node \ "data").find(_ \@ "key" == id)
}
