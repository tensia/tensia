package pl.edu.agh.tensia.contraction.order.native

import pl.edu.agh.tensia.contraction.order.tree._
import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 03/06/2017.
  */
case class OrderFinderResult(cost:Long, order:Array[Int]) {

  override def toString: String = s"OrderFinderResult($cost, Array(${order mkString ", "}))"

  def toContractionTree[T](tensors:IndexedSeq[Tensor[T]]) = {
    def mkContractionTree(ind:Int):Tree[T] =
      if (order(ind) < tensors.size) Leaf(tensors(order(ind)))
      else Node(mkContractionTree(ind + 1), mkContractionTree(order(ind) - tensors.size))
    mkContractionTree(0)
  }
}
