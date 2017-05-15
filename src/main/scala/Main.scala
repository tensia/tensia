/**
  * Created by mathek on 31/03/2017.
  */

import TensorContractionImplicits._

object Main extends App {
  BFSContractionOrder.findContractionOrder(Seq(Dimensions(IndexedSeq(1,2,3))), Seq(Seq()))
}
