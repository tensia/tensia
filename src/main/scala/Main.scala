/**
  * Created by mathek on 31/03/2017.
  */
object Main extends App {
  val r = Tensor(Array(1,2,3,4), 2, 2) ~ Tensor(Array(3,4,5,6,7,8), 3, 2) contractBy 1
  r.content foreach {e => print(s"${e} ")}
}
