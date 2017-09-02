package pl.edu.agh.tensia.contraction.order.tree

import pl.edu.agh.tensia.computation.{tree => comptree}
import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
trait Tree[T] {
  def toCompTree:comptree.Tree[Tensor[T]]
}

case class Node[T](left:Tree[T], right:Tree[T]) extends Tree[T] {
  override def toString: String = s"Node($left, $right)"

  override def toCompTree = comptree.Node[Tensor[T]](_ ~ _, left toCompTree, right toCompTree)
}
case class Leaf[T](tensor:Tensor[T]) extends Tree[T] {
  override def toString: String = tensor.toString

  override def toCompTree = comptree.Leaf(comptree.ValProvider of tensor)
}
