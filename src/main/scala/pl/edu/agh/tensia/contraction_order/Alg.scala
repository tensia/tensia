package pl.edu.agh.tensia.contraction_order

import pl.edu.agh.tensia.tensorflow.TensorRef

/**
  * Created by mathek on 04/06/2017.
  */
trait Alg {
  def findContractionOrder(tensors:Seq[TensorRef], contractedDims: Map[(TensorRef, TensorRef), Seq[Int]]):Tree
}
