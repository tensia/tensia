package pl.edu.agh.tensia.tensor

/**
  * Created by mathek on 31/03/2017.
  */

trait TensorError extends Error
case class InvalidTensorDimensionsError(dimensions: Dimensions) extends TensorError
case class InvalidTensorSizeError(size:Int, dimensionsTotalSize:Int) extends TensorError
case class DuplicateDimensionsError(dimensions: Dimensions) extends TensorError
