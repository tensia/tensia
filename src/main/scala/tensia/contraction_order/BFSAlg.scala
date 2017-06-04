package tensia.contraction_order

import tensia.tensor._
import tensia.helpers.loadLib

/**
  * Created by mathek on 03/06/2017.
  */

object BFSAlg extends Alg{
  loadLib("contraction_order_BFSAlg")
  @native def ord(dimensionsSizes:Array[Int], contractedDimsSizes:Array[Array[Int]]):NativeContractionOrderResult =
    throw new Error("jni fail")

  override def findContractionOrder(tensors:Seq[Tensor], contractedDims: Seq[ContractedDims]) = {
    val dimensions = tensors map (_.dimensions) toIndexedSeq
    val dimsSizes = dimensions map (_.totalSize) toArray
    val contractedDimsSizes = contractedDims.foldLeft(Array.fill(dimensions.length, dimensions.length)(1)) {
      case (acc, ContractedDims((d1, d2), cdims)) =>
        val cdimsSize = (cdims map {case (i1, i2) => i1} map dimensions(d1).sizes).product
        acc(d1)(d2) = cdimsSize
        acc(d2)(d1) = cdimsSize
        acc
    }
    ord(dimsSizes, contractedDimsSizes) toContractionTree tensors.toIndexedSeq
  }
}
