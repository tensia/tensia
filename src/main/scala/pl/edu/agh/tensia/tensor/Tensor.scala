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
    val contractedDims:Dimensions = dimensions intersect other.dimensions
    val thisRemainingDims:Dimensions = dimensions diff other.dimensions
    val otherRemainingDims:Dimensions = other.dimensions diff dimensions
    Tensor do_contract(this, other, contractedDims, thisRemainingDims, otherRemainingDims)
  }

  /**
    * Alias for [[Tensor.contract(other)]]
    */
  def ~(other: Tensor):Tensor = contract(other)

  /**
    * Changes dimensions order according to the new dimensions
    * @param newDimensions  dimensions to be set on [[Tensor]], must consist of the same [[Dimension]]s in any order
    * @return [[Tensor]] being view of this with shuffled dimensions
    */
  def reDimView(newDimensions: Dimensions): Tensor = {
    if (newDimensions.toSet != dimensions.toSet) throw InvalidTensorDimensionsError(newDimensions)
    val order = dimensions map newDimensions.indexOf
    newDimensions makeTensorView {indices => this(order map indices)}
  }

  /**
    * Works as [[Tensor.reDimView]], but computes new [[Tensor]] eagerly
    */
  def reDim(newDimensions: Dimensions) = reDimView(newDimensions).force

  def apply(indices:Seq[Int]) = content(dimensions tensorIndexOf indices)
}

object Tensor {
  def apply(content: Seq[Int], dims: Dimension*):Tensor = {
    Tensor(content, Dimensions(dims.to[IndexedSeq]))
  }
  def zero(dimensions:Dimension*) = Dimensions(dimensions toIndexedSeq) makeTensorView (_ => 0)
  def rand(dimensions:Dimension*) = Dimensions(dimensions toIndexedSeq) makeTensor (_ => Random nextInt 10)

  private def do_contract(t1: Tensor, t2:Tensor, contractedDims: Dimensions, t1remainingDims:Dimensions, t2remainingDims:Dimensions) = {
    val t1redimmed = t1.reDimView(t1remainingDims ++ contractedDims)
    val t2redimmed = t2.reDimView(t2remainingDims ++ contractedDims)


    val remainingDims:Dimensions = t1remainingDims ++ t2remainingDims
    remainingDims makeTensor { remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt t1remainingDims.length
      contractedDims.all map { indices => t1redimmed(t1indices ++ indices) * t2redimmed(t2indices ++ indices) } sum
    }
  }
}
