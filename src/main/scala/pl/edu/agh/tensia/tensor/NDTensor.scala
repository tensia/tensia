package pl.edu.agh.tensia.tensor

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import pl.edu.agh.tensia.tensor.NDTensor.BaseType

/**
  * Created by bblaszkow on 04.09.17.
  */
case class NDTensor(content: INDArray, dimensions: Dimensions) extends NDTensor.BaseType {

  override def contract(other: BaseType): BaseType = contract(other.asInstanceOf[NDTensor])

  def contract(other: NDTensor): NDTensor = {

    /* permute `this` to place contracted dimensions at the end */
    val thisDims = dimensions.zipWithIndex.partition {
      case (dim, _) => other.dimensions.contains(dim)
    }
    val thisPermutedIndices = (thisDims._2 ++ thisDims._1).map(_._2)
    System.err.println(thisPermutedIndices)
    content.permutei(thisPermutedIndices:_*)

    /* permute `other` to place contracted dimensions in front */
    val otherDims = other.dimensions.zipWithIndex.partition {
      case (dim, _) => dimensions.contains(dim)
    }
    val otherPermutedIndices = (otherDims._1 ++ otherDims._2).map(_._2)
    other.content.permutei(otherPermutedIndices:_*)

    lazy val contractedDims:Dimensions = thisDims._1.map(_._1)
    lazy val thisRemainingDims:Dimensions = thisDims._2.map(_._1)
    lazy val otherRemainingDims:Dimensions = otherDims._2.map(_._1)

    content.reshape(thisRemainingDims.totalSize, contractedDims.totalSize)
    other.content.reshape(otherRemainingDims.totalSize, contractedDims.totalSize)

    val result = content.mmul(other.content)

    val resultDims = thisRemainingDims ++ otherRemainingDims
    val resultSizes = resultDims map {_.size}
    result.reshape(resultSizes:_*)

    NDTensor(result, resultDims)
  }

}

object NDTensor {
  type BaseType = Tensor[NDTensor]

  def apply(content: INDArray, dims: Dimension*): NDTensor = {
    NDTensor(content, Dimensions(dims.to[IndexedSeq]))
  }
  def apply(content: Array[Float], dims: Dimension*): NDTensor = {
    val shape: Array[Int] = dims.toArray.map(_.size)
    NDTensor(Nd4j.create(content, shape), Dimensions(dims.to[IndexedSeq]))
  }
  def apply(content: Array[Double], dims: Dimension*): NDTensor = {
    val shape: Array[Int] = dims.toArray.map(_.size)
    NDTensor(Nd4j.create(content, shape), Dimensions(dims.to[IndexedSeq]))
  }
  def apply(content: Array[Int], dims: Dimension*): NDTensor = {
    val shape: Array[Int] = dims.toArray.map(_.size)
    val castedContent = content.map(_.toFloat)
    NDTensor(Nd4j.create(castedContent, shape), Dimensions(dims.to[IndexedSeq]))
  }
}
