/**
  * Created by mathek on 31/03/2017.
  */

import TensorContractionImplicits._

object Main extends App {
  println(BFSContractionOrder.findContractionOrder(
    Seq(Dimensions.of(3, 4), Dimensions.of(4, 5), Dimensions.of(2, 3, 5)),
    Seq(ContractedDims((0, 1), Seq(1)), ContractedDims((0, 2), Seq(0)), ContractedDims((1, 2), Seq(1)))
  ))
}
