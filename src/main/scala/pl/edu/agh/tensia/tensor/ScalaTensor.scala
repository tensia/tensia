package pl.edu.agh.tensia.tensor


import scala.collection.SeqView
import scala.util.Random

/**
  * Created by mathek on 03/06/2017.
  */
case class ScalaTensor(content: Seq[Int], dimensions: Dimensions) extends ScalaTensor.BaseType {
  import ScalaTensor._
  if (content.length != dimensions.totalSize) throw InvalidTensorSizeError(content.length, dimensions.totalSize)

  lazy val rank = dimensions.length
  /**
    * if current [[ScalaTensor]] is a view on some other one, evaluates to a new [[ScalaTensor]] with computed content, otherwise
    * evaluates to this
    */
  lazy val force = content match {
    case c:SeqView[Int, Seq[_]] @unchecked => ScalaTensor(c toIndexedSeq, dimensions)
    case _ => this
  }

  /**
    * Contracts this with another [[ScalaTensor]] by dimensions passed as dims
    * @param other  [[ScalaTensor]] to be contracted with this
    * @return [[ScalaTensor]] being result of contracting this and other
    */
  def contract(other: BaseType): BaseType = contract(other.asInstanceOf[ScalaTensor])
  def contract(other: ScalaTensor): ScalaTensor = {
    val contractedDims:Dimensions = dimensions intersect other.dimensions
    val thisRemainingDims:Dimensions = dimensions diff other.dimensions
    val otherRemainingDims:Dimensions = other.dimensions diff dimensions
    ScalaTensor do_contract(this, other, contractedDims, thisRemainingDims, otherRemainingDims)
  }


  /**
    * Changes dimensions order according to the new dimensions
    * @param newDimensions  dimensions to be set on [[ScalaTensor]], must consist of the same [[Dimension]]s in any order
    * @return [[ScalaTensor]] being view of this with shuffled dimensions
    */
  def reDimView(newDimensions: Dimensions): ScalaTensor = {
    if (newDimensions.toSet != dimensions.toSet) throw InvalidTensorDimensionsError(newDimensions)
    val order = dimensions map newDimensions.indexOf
    ScalaTensor makeView({indices => this(order map indices)}, newDimensions)
  }

  /**
    * Works as [[ScalaTensor.reDimView]], but computes new [[ScalaTensor]] eagerly
    */
  def reDim(newDimensions: Dimensions) = reDimView(newDimensions).force

  def apply(indices:Seq[Int]) = content(dimensions tensorIndexOf indices)
}

object ScalaTensor {
  type BaseType = Tensor[ScalaTensor]

  def apply(content: Seq[Int], dims: Dimension*):ScalaTensor = {
    ScalaTensor(content, Dimensions(dims.to[IndexedSeq]))
  }

  def zero(dimensions:Dimension*) = makeView(_ => 0, dimensions toIndexedSeq)

  def rand(dimensions:Dimension*) = makeView(_ => Random nextInt 10, dimensions toIndexedSeq)

  /**
    * Creates [[ScalaTensor]] of content being result of applying mapper to each value of indices of passed dimensions
    * @param maker  function mapping each indices sequence to value of [[ScalaTensor]] at these indices
    * @param dimensions dimensions of created [[ScalaTensor]]
    * @return [[ScalaTensor]] of content produced as written above, and passed [[Dimensions]]
    */
  def makeView(maker:Seq[Int] => Int, dimensions:Dimensions):ScalaTensor =
    ScalaTensor(dimensions.all map maker, dimensions)

  /**
    * Eager version of [[ScalaTensor.makeView(maker)]]
    */
  def make(maker:Seq[Int] => Int, dimensions:Dimensions):ScalaTensor =
    makeView(maker, dimensions).force

  private def do_contract(t1: ScalaTensor, t2:ScalaTensor, contractedDims: Dimensions, t1remainingDims:Dimensions, t2remainingDims:Dimensions) = {
    val t1redimmed = t1.reDimView(t1remainingDims ++ contractedDims)
    val t2redimmed = t2.reDimView(t2remainingDims ++ contractedDims)


    val remainingDims:Dimensions = t1remainingDims ++ t2remainingDims
    make({ remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt t1remainingDims.length
      contractedDims.all map { indices => t1redimmed(t1indices ++ indices) * t2redimmed(t2indices ++ indices) } sum
    }, remainingDims)
  }
}
