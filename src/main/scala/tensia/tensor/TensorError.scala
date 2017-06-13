package tensia.tensor

/**
  * Created by mathek on 31/03/2017.
  */

trait TensorError extends Error
case class InvalidContractionArgumentError(dims1: Dimensions, dims2:Dimensions) extends TensorError
case class InvalidTensorReDimOrderError(order: Seq[Int]) extends TensorError
case class InvalidTensorSizeError(size:Int, dimensionsTotalSize:Int) extends TensorError
