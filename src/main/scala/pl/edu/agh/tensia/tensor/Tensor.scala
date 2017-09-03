package pl.edu.agh.tensia.tensor

/**
  * Created by mathek on 02/09/2017.
  */
trait Tensor[T] {
  def contract(other:Tensor[T]):Tensor[T]
  final def ~(other: Tensor[T]):Tensor[T] = contract(other)
  def dimensions:Dimensions
}
