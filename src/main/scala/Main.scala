/**
  * Created by mathek on 31/03/2017.
  */

import TensorContractionImplicits._

object Main extends App {
  val r = Tensor(Vector(1, 2, 5, 7, 7, 6), 3, 2).reDim(Seq(1, 0))
  r.content foreach {e => print(s"$e ")}
}
