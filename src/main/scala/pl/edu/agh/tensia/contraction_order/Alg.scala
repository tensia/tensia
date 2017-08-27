package pl.edu.agh.tensia.contraction_order

import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait Alg {
  def findContractionOrder(tensors:Seq[Tensor]):Tree
}
