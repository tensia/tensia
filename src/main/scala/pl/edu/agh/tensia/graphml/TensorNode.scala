package pl.edu.agh.tensia.graphml

import java.io.File

import org.nd4j.linalg.factory.Nd4j
import pl.edu.agh.tensia.tensor.{Dimension, NDTensor}


case class TensorNode(dataPath: String, dims: Array[Dimension]) {

  def toTensor: NDTensor = {
    if (dims.contains(null)) throw new IllegalStateException("Cannot create tensor with null")
    val array = Nd4j.readBinary(new File(dataPath))
    NDTensor(array, dims:_*)
  }
}
