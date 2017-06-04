/**
  * Created by mathek on 31/03/2017.
  */

import tensia.tensor._
import tensia.contraction_order.{BFSAlg, ContractedDims}

object Main extends App {
  println(BFSAlg.findContractionOrder(
    Seq(Tensor.zero(3, 4), Tensor.zero(4, 5), Tensor.zero(2, 3, 5)),
    Seq(ContractedDims((0, 1), Seq((1, 0))), ContractedDims((0, 2), Seq((0, 1))), ContractedDims((1, 2), Seq((1, 2))))
  ))
}
