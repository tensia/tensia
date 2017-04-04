/**
  * Created by mathek on 31/03/2017.
  */
import scala.collection.immutable.Seq

object Main extends App {
  val r = Tensor(Vector(3, 4, 5, 6, 7, 8), 3, 2) contract(Tensor(Vector(1, 2, 5, 7, 7, 6), 3, 2), 1)
  r.content foreach {e => print(s"${e} ")}
}
