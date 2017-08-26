package pl.edu.agh.tensia.tensor

import scala.collection.SeqView

/**
  * Created by mathek on 03/06/2017.
  */
case class Dimensions(dimensions:IndexedSeq[Dimension]) {

  lazy val length:Int = dimensions.length

  lazy val sizes:IndexedSeq[Int] = dimensions map (_.size)

  lazy val totalSize:Int = sizes.product

  /**
    * Reorders dimensions according to order
    * @param order seq which index corresponds to current dimension no, and value to its new number
    * @return reordered [[Dimensions]]
    */
  def reorder(order: Seq[Int]):Dimensions = {
    if (order.distinct != order || !order.forall(0 until length contains _)) {
      throw InvalidTensorReDimOrderError(order)
    }
    Dimensions(order map dimensions toIndexedSeq)
  }

  /**
    * @param indices  sequence of indices in this [[Dimensions]]
    * @return index in [[Tensor]] contents array
    */
  def indexOf(indices:Seq[Int]):Int =
    (indices zip sizes).foldRight (1, 0) {
      case ((idx, size), (prod, acc)) => (prod*size, acc+prod*idx)
    } match {
      case (prod, res) => res
    }

  /**
    * @param index  index in [[Tensor]] contents array
    * @return sequence of indices in this [[Dimensions]]
    */
  def indicesOf(index:Int):Seq[Int] =
    sizes.foldRight (index, List[Int]()) {
      case (size, (idx, acc)) => (idx/size, idx % size :: acc)
    } match {
      case (prod, res) => res
    }

  /**
    * Splits dimensions into two parts, according to given length
    * @param len  if positive, length of the left split part, otherwise length of the right split part
    * @return 2-element tuple of [[Dimensions]]
    */
  def split(len:Int):(Dimensions, Dimensions) = dimensions splitAt (if (len < 0) this.length + len else len) match {
    case (leftSizes, rightSizes) => (Dimensions(leftSizes), Dimensions(rightSizes))
  }

  /**
    * Concats dimensions and `other` dimensions
    * @param other  dimensions to concat with
    * @return [[Dimensions]] being result of concatenation
    */
  def ++(other:Dimensions):Dimensions = Dimensions(dimensions ++ other.dimensions)

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
    * @param maker  function mapping indices to value of pl.edu.agh.tensia.tensor at these indices
    * @return [[Tensor]] of content produced as written above, and [[Dimensions]] of this
    */
  def makeTensorView(maker:Seq[Int] => Int):Tensor = Tensor(all map maker, this)
  def makeTensor(maker:Seq[Int] => Int):Tensor = Tensor(all map maker toIndexedSeq, this)

}

object Dimensions {
  def of(sizes:Dimension*) = Dimensions(sizes.toIndexedSeq)

  implicit def to_dimension_list(d:Dimensions):List[Dimension] = d.dimensions
}
