package pl.edu.agh.tensia.contraction_order

import pl.edu.agh.tensia.computation.comptree
import pl.edu.agh.tensia.tensor.Tensor
import pl.edu.agh.tensia.tensor.ContractionImplicits._

/**
  * Created by mathek on 04/06/2017.
  */
trait Tree {
  def toCompTree:comptree.Tree[Tensor]
}

case class TreeNode(left:Tree, right:Tree) extends Tree {
  override def toString: String = s"Node($left, $right)"

  override def toCompTree: comptree.Tree[Tensor] = comptree.Node[Tensor](
    (t1, t2) => {
      val Seq(d1, d2) = Seq(t1, t2) map (_.dimensions.sizes)
      val contractedDims: Seq[(Int, Int)] = d1.zipWithIndex.foldLeft(Map[Int, Int]()){
        case (acc, (d, i)) =>
          d2 indexOf d match {
            case j if j >= 0 && !acc.contains(j) => acc.updated(j, i)
            case _ => acc
          }
      } .toSeq
      t2 ~ contractedDims ~ t1
    }, left.toCompTree, right.toCompTree)
}
case class TreeLeaf(content:Tensor) extends Tree {
  override def toString: String = content.toString

  override def toCompTree: comptree.Tree[Tensor] = comptree.Leaf(comptree.ValProvider of content)
}
