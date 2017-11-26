package pl.edu.agh.tensia.graphml

import pl.edu.agh.tensia.tensor.{NDTensor, Tensor}

import scala.xml.{Elem, NodeSeq}

object GraphMLWriter {
  def tensorToXml(tensor: NDTensor, id: String): Elem = {
    val shape: String = tensor.dimensions.sizes.mkString(",")
    <node id={id}>
      <data key="shape">{shape}</data>
      <data key="dataPath"></data>
    </node>
  }
  def wrapWithGraph(nodes: NodeSeq): Elem =
    <graph id="0" edgedefault="undirected">{nodes}</graph>

  def wrapWithGraphML(nodeSeq: NodeSeq) =
    <graphml xmlns="http://graphml.graphdrawing.org/xmlns"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
      {nodeSeq}
    </graphml>
}
