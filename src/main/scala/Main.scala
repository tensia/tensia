/**
  * Created by mathek on 31/03/2017.
  */

import pl.edu.agh.tensia.tensor._
import pl.edu.agh.tensia.helpers._
import pl.edu.agh.tensia.contraction_order.{BFSAlg, ContractedDims}

object Main extends App {
  val t = Seq(Tensor.zero(3, 4), Tensor.zero(4, 5), Tensor.zero(2, 3, 5))
  println(BFSAlg.findContractionOrder(
    t,
    mkContractedDims((t(0), t(1)) -> Seq((1, 0)), (t(0), t(2)) -> Seq((0, 1)), (t(1), t(2)) -> Seq((1, 2)))
  ))
}
