package tensia.contraction_order

import tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait Alg {
  def findContractionOrder(tensors:Seq[Tensor], contractedDims: Seq[ContractedDims]):Tree
}
