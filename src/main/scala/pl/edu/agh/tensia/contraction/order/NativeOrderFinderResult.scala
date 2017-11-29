package pl.edu.agh.tensia.contraction.order

import pl.edu.agh.tensia.contraction.order.tree._
import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 03/06/2017.
  */
case class NativeOrderFinderResult(cost:Long, order:Array[Array[Int]]) {

  override def toString: String = s"NativeOrderFinderResult($cost, Array(${order.deep mkString ", "}))"

  def toContractionTrees[T](tensors:IndexedSeq[Tensor[T]]):Seq[Tree[T]] = {
    def mkContractionTree(order: Array[Int], ind:Int):Tree[T] = {
      if (order(ind) < tensors.size) Leaf(tensors(order(ind)))
      else Node(mkContractionTree(order, ind + 1), mkContractionTree(order, order(ind) - tensors.size))
    }
    order map (mkContractionTree(_, 0))
  }
}
