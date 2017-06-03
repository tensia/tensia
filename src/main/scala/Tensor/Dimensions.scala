package tensor

import scala.collection.SeqView

/**
  * Created by mathek on 03/06/2017.
  */
case class Dimensions(sizes:IndexedSeq[Int]) {

  lazy val length = sizes.length

  lazy val totalSize = sizes.product

  def contract(dims:Seq[(Int, Int)], other:Dimensions) = remove(dims.map(_._1).toSet) ++ (other remove dims.map(_._2).toSet)

  def remove(dims:Set[Int]) = Dimensions(sizes.zipWithIndex.view.filterNot{case (_, i) => dims contains i}.map(_._1).toIndexedSeq)

  /**
    * @param dims   contracted dimensions of this
    * @param other  other [[Dimensions]]
    * @return       cost of contranction of this-dimensioned and other-dimensioned [[Tensor]]s
    */
  def contractionCost(dims:Seq[Int], other:Dimensions) = totalSize / (dims map sizes product) * other.totalSize

  /**
    * Reorders dimensions according to order
    * @param order seq which index corresponds to current dimension no, and value to its new number
    * @return reordered [[Dimensions]]
    */
  def reorder(order: Seq[Int]) = {
    if (order.distinct != order || !order.forall(0 until length contains _)) {
      throw InvalidTensorReDimOrderError(order)
    }
    Dimensions(order map sizes toIndexedSeq)
  }

  def indexOf(indices:Seq[Int]):Int =
    (indices zip sizes).foldRight (1, 0) {
      case ((idx, size), (prod, acc)) => (prod*size, acc+prod*idx)
    } match {
      case (prod, res) => res
    }

  def indicesOf(index:Int):Seq[Int] =
    sizes.foldRight (index, List[Int]()) {
      case (size, (idx, acc)) => (idx/size, idx % size :: acc)
    } match {
      case (prod, res) => res
    }

  /**
    * splits dimensions into two parts, according to given length
    * @param len  if positive, length of the left split part, otherwise length of the right split part
    * @return 2-element tuple of [[Dimensions]]
    */
  def split(len:Int):(Dimensions, Dimensions) = sizes splitAt (if (len < 0) this.length + len else len) match {
    case (leftSizes, rightSizes) => (Dimensions(leftSizes), Dimensions(rightSizes))
  }

  def ++(other:Dimensions) = Dimensions(sizes ++ other.sizes)

  /**
    * @return lazy sequence of all possible indices' values
    */
  def all: SeqView[Seq[Int], Seq[_]] =
  length match {
    case 0 => Seq(Seq()) view
    case _ => (0 until totalSize).view map indicesOf
  }

  /**
    * Creates [[Tensor]] of content being result of applying mapper to each value of indices
    * @param maker  function mapping indices to value of tensor at these indices
    * @return [[Tensor]] of content produced as written above, and [[Dimensions]] of this
    */
  def makeTensorView(maker:Seq[Int] => Int):Tensor = Tensor(all map maker, this)
  def makeTensor(maker:Seq[Int] => Int):Tensor = Tensor(all map maker toIndexedSeq, this)

}

object Dimensions {
  def of(sizes:Int*) = Dimensions(sizes.toIndexedSeq)
}

