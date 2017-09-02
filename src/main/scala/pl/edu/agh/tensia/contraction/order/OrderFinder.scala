package pl.edu.agh.tensia.contraction.order

import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait OrderFinder {
  def findContractionOrder(tensors:Seq[Tensor]):Tree
}
