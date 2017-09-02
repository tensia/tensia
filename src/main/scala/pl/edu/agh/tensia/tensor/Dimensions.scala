package pl.edu.agh.tensia.tensor

import scala.collection.SeqView

/**
  * Created by mathek on 03/06/2017.
  */
case class Dimensions(dimensions:IndexedSeq[Dimension]) {

  if(dimensions != dimensions.distinct) throw DuplicateDimensionsError(this)

  lazy val length:Int = dimensions.length

  lazy val sizes:IndexedSeq[Int] = dimensions map (_.size)

  lazy val totalSize:Int = sizes.product


  /**
    * @param indices  sequence of indices in this [[Dimensions]]
    * @return index in [[Tensor]] contents array
    */
  def tensorIndexOf(indices:Seq[Int]):Int =
    (indices zip sizes).foldRight (1, 0) {
      case ((idx, size), (prod, acc)) => (prod*size, acc+prod*idx)
    } match {
      case (prod, res) => res
    }

  /**
    * @param tensorIndex  index in [[Tensor]] contents array
    * @return sequence of indices in this [[Dimensions]]
    */
  def indicesOf(tensorIndex:Int):Seq[Int] =
    sizes.foldRight (tensorIndex, List[Int]()) {
      case (size, (idx, acc)) => (idx/size, idx % size :: acc)
    } match {
      case (prod, res) => res
    }

  /**
    * @return view of sequence of all possible indices' values
    */
  def all: SeqView[Seq[Int], Seq[_]] =
  length match {
    case 0 => Seq(Seq()) view
    case _ => (0 until totalSize).view map indicesOf
  }

  override def toString: String = s"Dimensions(${dimensions mkString ", "})"

}

object Dimensions {
  def of(sizes:Dimension*) = Dimensions(sizes.toIndexedSeq)

  implicit def to_dimension_seq(d:Dimensions):IndexedSeq[Dimension] = d.dimensions

  implicit def from_dimension_seq(d:IndexedSeq[Dimension]):Dimensions = Dimensions(d)
}
