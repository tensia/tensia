package pl.edu.agh.tensia.graphml

import java.io._
import java.nio.file.{Files, Paths}

import org.nd4j.linalg.factory.Nd4j
import pl.edu.agh.tensia.tensor.NDTensor

import scala.xml.{Elem, NodeSeq, XML}

case class GraphMLWriter(gmlPath: String) {
  import GraphMLWriter._
  def write(tensor: NDTensor): Unit = {
    val dir = Paths.get(gmlPath).getParent

    val file = dir.resolve("out.bin")
    Nd4j.write(Files.newOutputStream(file), tensor.content)

    val xml = wrapWithGraphML(
      wrapWithGraph(
          tensorToXml(tensor, "0", file.toString
        )
      )
    )
    XML.save(gmlPath, xml)
  }

  def write(tensors: Seq[NDTensor]): Unit = {
    val dir = Paths.get(gmlPath).getParent
    val pw = new PrintWriter(gmlPath)
    val xmlTensors: NodeSeq = tensors.zipWithIndex.map { case (tensor, i) =>
      val fileName = s"out$i.bin"
      val file = dir.resolve(fileName)
      Nd4j.write(Files.newOutputStream(file), tensor.content)
      tensorToXml(tensor, i.toString, fileName)
    }
    val xml = wrapWithGraphML(wrapWithGraph(xmlTensors))
    XML.save(gmlPath, xml)
  }

}

object GraphMLWriter {
  def tensorToXml(tensor: NDTensor.BaseType, id: String, dataPath: String): Elem = {
    val shape: String = tensor.dimensions.sizes.mkString(",")
    <node id={id}>
      <data key="shape">{shape}</data>
      <data key="dataPath">{dataPath}</data>
    </node>
  }
  def wrapWithGraph(nodes: NodeSeq): Elem =
    <graph id="0" edgedefault="undirected">{nodes}</graph>

  def wrapWithGraphML(nodeSeq: NodeSeq): Elem =
    <graphml xmlns="http://graphml.graphdrawing.org/xmlns"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
      {nodeSeq}
    </graphml>
}
