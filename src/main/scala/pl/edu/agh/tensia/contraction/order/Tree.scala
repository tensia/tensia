package pl.edu.agh.tensia.contraction.order

import pl.edu.agh.tensia.computation.comptree
import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait Tree {
  def toCompTree:comptree.Tree[Tensor]
}

case class TreeNode(left:Tree, right:Tree) extends Tree {
  override def toString: String = s"Node($left, $right)"

  override def toCompTree = comptree.Node[Tensor](_ ~ _, left toCompTree, right toCompTree)
}
case class TreeLeaf(tensor:Tensor) extends Tree {
  override def toString: String = tensor.toString

  override def toCompTree = comptree.Leaf(comptree.ValProvider of tensor)
}
