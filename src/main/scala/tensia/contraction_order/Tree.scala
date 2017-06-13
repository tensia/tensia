package tensia.contraction_order

import tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait Tree

case class TreeNode(left:Tree, right:Tree) extends Tree {
  override def toString: String = s"Node($left, $right)"
}
case class TreeLeaf(content:Tensor) extends Tree {
  override def toString: String = content.toString
}
