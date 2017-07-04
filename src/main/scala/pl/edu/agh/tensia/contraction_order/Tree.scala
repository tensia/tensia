package pl.edu.agh.tensia.contraction_order

import pl.edu.agh.tensia.computation.comptree
import pl.edu.agh.tensia.tensor.Tensor
import pl.edu.agh.tensia.tensor.ContractionImplicits._

/**
  * Created by mathek on 04/06/2017.
  */
trait Tree {
  def toCompTree(contractedDims: Map[(Tensor, Tensor), Seq[Int]]):comptree.Tree[TensorComputationNode]
}

case class TensorComputationNode(tensor: Tensor, madeOf: Seq[(Tensor, Seq[Int])])

case class TreeNode(left:Tree, right:Tree) extends Tree {
  override def toString: String = s"Node($left, $right)"

  override def toCompTree(contractedDims: Map[(Tensor, Tensor), Seq[Int]]) = comptree.Node[TensorComputationNode]({
    case (TensorComputationNode(t1, t1MadeOf), TensorComputationNode(t2, t2MadeOf)) =>
      val t1ContrDims = t1MadeOf
        .map { case (mt1, _) => (mt1, t2MadeOf.flatMap {case (mt2, _) => contractedDims(mt1, mt2)} .toSet)}
        .toMap
      val t2ContrDims = t2MadeOf
        .map { case (mt2, _) => (mt2, t1MadeOf.flatMap {case (mt1, _) => contractedDims(mt2, mt1)} .toSet)}
        .toMap
      val madeOf =
        t1MadeOf.map{case (t, d) => (t, d.filterNot(t1ContrDims(t).contains))} ++
        t2MadeOf.map{case (t, d) => (t, d.filterNot(t2ContrDims(t).contains))}

      val contrDims = (for{(mt1, _) <- t1MadeOf; (mt2, _) <- t2MadeOf}
        yield contractedDims.getOrElse((mt1, mt2), Seq()) zip contractedDims.getOrElse((mt2, mt1), Seq())
      ).flatten

      TensorComputationNode(t1 ~ contrDims ~ t2, madeOf)
    }, left toCompTree contractedDims, right toCompTree contractedDims)
}
case class TreeLeaf(tensor:Tensor) extends Tree {
  override def toString: String = tensor.toString

  override def toCompTree(contractedDims: Map[(Tensor, Tensor), Seq[Int]]) = {
    val madeOf = Seq((tensor, 0 until tensor.rank))
    comptree.Leaf(comptree.ValProvider of TensorComputationNode(tensor, madeOf))
  }
}
