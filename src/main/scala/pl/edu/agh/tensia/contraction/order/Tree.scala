package pl.edu.agh.tensia.contraction.order

import pl.edu.agh.tensia.computation.comptree
import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait Tree[T] {
  def toCompTree:comptree.Tree[Tensor[T]]
}

case class TreeNode[T](left:Tree[T], right:Tree[T]) extends Tree[T] {
  override def toString: String = s"Node($left, $right)"

  override def toCompTree = comptree.Node[Tensor[T]](_ ~ _, left toCompTree, right toCompTree)
}
case class TreeLeaf[T](tensor:Tensor[T]) extends Tree[T] {
  override def toString: String = tensor.toString

  override def toCompTree = comptree.Leaf(comptree.ValProvider of tensor)
}
