package pl.edu.agh.tensia.graphml

import pl.edu.agh.tensia.tensor.{Dimension, NDTensor}

object TensorNode {
  def apply(dataPath: String,dims: Array[Dimension]): TensorNode =
    new TensorNode(dataPath, dims)
}

class TensorNode(val dataPath: String, val dims: Array[Dimension]) {

  def toTensor: NDTensor = {
    if (dims.contains(null)) throw new IllegalStateException("Cannot create tensor with null")
    NDTensor.zero(dims:_*)
  }
}
