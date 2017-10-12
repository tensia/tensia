package pl.edu.agh.tensia.tensor

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import pl.edu.agh.tensia.tensor.NDTensor.BaseType

/**
  * Created by bblaszkow on 04.09.17.
  */
case class NDTensor(content: INDArray, dimensions: Dimensions) extends NDTensor.BaseType {

  override def contract(other: BaseType): BaseType = contract(other.asInstanceOf[NDTensor])

  private def permuteForContraction(otherDims: Dimensions, reversed: Boolean): (Dimensions, Dimensions) = {
    /* The condition here is basically `is dimension contracted?` */
    val partitionedDims = dimensions.zipWithIndex.partition {
      case (dim, _) => otherDims.contains(dim)
    }
    val thisPermutedIndices =
    /* If not reversed, contracted dims are last */
      if (!reversed) (partitionedDims._2 ++ partitionedDims._1).map(_._2)
      else           (partitionedDims._1 ++ partitionedDims._2).map(_._2)

    content.permutei(thisPermutedIndices:_*)

    /* Don't return indices for dims */
    (partitionedDims._1.map(_._1), partitionedDims._2.map(_._1))
  }

  def contract(other: NDTensor): NDTensor = {
    val (contractedDims: Dimensions, thisRemainingDims: Dimensions) = this.permuteForContraction(other.dimensions, reversed = false)
    val (_, otherRemainingDims: Dimensions) = other.permuteForContraction(this.dimensions, reversed = true)

    val a = content.reshape(thisRemainingDims.totalSize, contractedDims.totalSize)
    val b = other.content.reshape(contractedDims.totalSize, otherRemainingDims.totalSize)

    val result = a.mmul(b)

    val resultDims = thisRemainingDims ++ otherRemainingDims
    val resultSizes = resultDims map {_.size}
    if (resultSizes.nonEmpty)
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

  def scalar(content: Float): NDTensor = {
    NDTensor(Nd4j.create(1).addi(content), Dimensions(IndexedSeq()))
  }
}
