package pl.edu.agh.tensia.tensor

import scala.collection.SeqView

/**
  * Created by mathek on 03/06/2017.
  */
case class Tensor(content: Seq[Int], dimensions: Dimensions) {
  if (content.length != dimensions.totalSize) throw InvalidTensorSizeError(content.length, dimensions.totalSize)

  lazy val rank = dimensions.length
  /**
    * if current [[Tensor]] is a view on some other one, evaluates to a new [[Tensor]] with computed content, otherwise
    * evaluates to this
    */
  lazy val force = content match {
    case c:SeqView[Int, Seq[_]] @unchecked => Tensor(c toIndexedSeq, dimensions)
    case _ => this
  }

  /**
    * Contacts this with another [[Tensor]] by dimensionsCnt rightmost dimensions
    * @param other  [[Tensor]] to be contacted with this
    * @param dimensionsCnt  amount of rightmost dimensions that should be contacted
    * @return [[Tensor]] being result of contracting this and other
    */
  def contract(other: Tensor, dimensionsCnt: Int) = {

    val (remainingDims, contractedDims) = dimensions split -dimensionsCnt
    val (otherRemainingDims, otherContractedDims) = other.dimensions split -dimensionsCnt

    if (contractedDims != otherContractedDims) throw InvalidContractionArgumentError(contractedDims, otherContractedDims)

    (remainingDims ++ otherRemainingDims) makeTensor {remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt remainingDims.length
      contractedDims.all map { indices => this(t1indices ++ indices) * other(t2indices ++ indices)} sum
    }
  }

  /**
    * Contracts this with another [[Tensor]] by dimensions passed as dims
    * @param other  [[Tensor]] to be contracted with this
    * @param dims Sequence of pairs of contracted dimension numbers, where first element in pair corresponds to this'
    *             dimension, and the second to the others dimension
    * @return [[Tensor]] being result of contracting this and other
    */
  def contract(other: Tensor, dims: Seq[(Int, Int)]): Tensor = {
    val (thisContractedDims, otherContractedDims) = dims.unzip
    val thisRemainingDims = (0 until rank) filterNot thisContractedDims.toSet.contains
    val otherRemainingDims = (0 until other.rank) filterNot otherContractedDims.toSet.contains
    this reDimView thisRemainingDims ++ thisContractedDims contract (
        other reDimView otherRemainingDims ++ otherContractedDims,
        thisContractedDims.length
      )
  }

  /**
    * Shuffles dimensions according to given ordering
    * @param order seq which index corresponds to current dimension no, and value to its new number
    * @return [[Tensor]] being view of this with shuffled dimensions
    */
  def reDimView(order:Seq[Int]): Tensor = {
    val invOrder = order.zipWithIndex sortBy {case (v, i) => v} map {case (v, i) => i}
    dimensions reorder order makeTensorView { indices => this(invOrder map indices)}
  }

  /**
    * Works as [[Tensor.reDimView]], but computes new [[Tensor]] eagerly
    */
  def reDim(ordering:Seq[Int]) = reDimView(ordering).force

  def apply(indices:Seq[Int]) = content(dimensions indexOf indices)
}

object Tensor {
  def apply(content: Seq[Int], dims: Int*):Tensor = {
    Tensor(content, Dimensions(dims.to[IndexedSeq]))
  }
  def zero(dimensions:Int*) = Dimensions(dimensions toIndexedSeq) makeTensorView (_ => 0)
}
