package pl.edu.agh.tensia.tensor

import pl.edu.agh.tensia.contraction_order.ContractedDims

import scala.collection.SeqView
import scala.util.Random

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
    * Contracts this with another [[Tensor]] by dimensions passed as dims
    * @param other  [[Tensor]] to be contracted with this
    * @return [[Tensor]] being result of contracting this and other
    */
  def contract(other: Tensor): Tensor = {
    val contractedDimsSet = dimensions.toSet intersect other.dimensions.toSet
    val thisDimsNewIndices = contracted_than_remaining_order(contractedDimsSet, this)
    val otherDimsNewIndices = contracted_than_remaining_order(contractedDimsSet, other)
    this reDimView thisDimsNewIndices do_contract (
        other reDimView otherDimsNewIndices,
        contractedDimsSet.size
      )
  }

  private def contracted_than_remaining_order(contractedDimsSet: Set[Dimension], tensor: Tensor) = {
    val (contractedDimsIndices, remainingDimsIndices) =
      (0 until tensor.rank) partition { i => contractedDimsSet contains tensor.dimensions(i) }
    contractedDimsIndices ++ remainingDimsIndices

  }

  private def do_contract(other: Tensor, dimensionsCnt: Int) = {

    val (remainingDims, contractedDims) = dimensions split -dimensionsCnt
    val (otherRemainingDims, otherContractedDims) = other.dimensions split -dimensionsCnt

    if (contractedDims != otherContractedDims) throw InvalidContractionArgumentError(contractedDims, otherContractedDims)

    (remainingDims ++ otherRemainingDims) makeTensor {remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt remainingDims.length
      contractedDims.all map { indices => this(t1indices ++ indices) * other(t2indices ++ indices)} sum
    }
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
  def apply(content: Seq[Int], dims: Dimension*):Tensor = {
    Tensor(content, Dimensions(dims.to[IndexedSeq]))
  }
  def zero(dimensions:Dimension*) = Dimensions(dimensions toIndexedSeq) makeTensorView (_ => 0)
  def rand(dimensions:Dimension*) = Dimensions(dimensions toIndexedSeq) makeTensor (_ => Random nextInt 10)
}
