package tensia.contraction_order

import tensia.tensor.Tensor

/**
  * Created by mathek on 03/06/2017.
  */
case class NativeContractionOrderResult(cost:Long, order:Array[Int]) {

  override def toString: String = s"NativeContractionOrderResult($cost, Array(${order mkString ", "}))"

  def toContractionTree(tensors:IndexedSeq[Tensor]) = {
    def mkContractionTree(ind:Int):Tree =
      if (order(ind) < tensors.size) TreeLeaf(tensors(order(ind)))
      else TreeNode(mkContractionTree(ind + 1), mkContractionTree(order(ind) - tensors.size))
    mkContractionTree(0)
  }
}
