package pl.edu.agh.tensia.contraction_order

import pl.edu.agh.tensia.tensor._
import pl.edu.agh.tensia.helpers.loadLib

/**
  * Created by mathek on 03/06/2017.
  */

object BFSAlg extends Alg{
  loadLib("contraction_order_BFSAlg")
  @native def ord(dimensionsSizes:Array[Int], contractedDimsSizes:Array[Array[Int]]):NativeContractionOrderResult =
    throw new Error("jni fail")

  override def findContractionOrder(tensors:Seq[Tensor]) = {
    val dimsSizes = tensors map (_.dimensions.totalSize) toArray
    val contractedDimsSizes: Array[Array[Int]] = Array.fill(tensors.length, tensors.length)(1)
    for ((t1, i) <- tensors.zipWithIndex; (t2, j) <- tensors.zipWithIndex; if t1 != t2) {
      val contractedDims: Dimensions = t1.dimensions intersect t2.dimensions
      contractedDimsSizes(i)(j) = contractedDims.totalSize
    }
    ord(dimsSizes, contractedDimsSizes) toContractionTree tensors.toIndexedSeq
  }
}
