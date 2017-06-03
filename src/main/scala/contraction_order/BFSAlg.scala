package contraction_order

import tensor._
import helpers.loadLib

/**
  * Created by mathek on 03/06/2017.
  */

object BFSAlg {
  loadLib("contraction_order_BFSAlg")
  @native def ord(dimensionsSizes:Array[Int], contractedDimsSizes:Array[Array[Int]]):NativeContractionOrderResult =
    throw new Error("jni fail")

  def findContractionOrder(tensors:Seq[Tensor], contractedDims: Seq[ContractedDims]) = {
    val dimensions = tensors map (_.dimensions)
    val indDimensions = dimensions.toIndexedSeq
    val dimsSizes = dimensions map (_.totalSize) toArray
    val contractedDimsSizes = contractedDims.foldLeft(Array.fill(dimensions.length, dimensions.length)(1)) {
      case (acc, ContractedDims((d1, d2), cdims)) =>
        val cdimsSize = cdims.map(indDimensions(d1).sizes).product
        acc(d1)(d2) = cdimsSize
        acc(d2)(d1) = cdimsSize
        acc
    }
    ord(dimsSizes, contractedDimsSizes)
  }
}
