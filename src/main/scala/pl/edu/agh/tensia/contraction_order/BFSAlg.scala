package pl.edu.agh.tensia.contraction_order

import pl.edu.agh.tensia.helpers.loadLib
import pl.edu.agh.tensia.tensorflow.TensorRef

/**
  * Created by mathek on 03/06/2017.
  */

object BFSAlg extends Alg{
  loadLib("contraction_order_BFSAlg")
  @native def ord(dimensionsSizes:Array[Int], contractedDimsSizes:Array[Array[Int]]):NativeContractionOrderResult =
    throw new Error("jni fail")

  override def findContractionOrder(tensors:Seq[TensorRef], contractedDims: Map[(TensorRef, TensorRef), Seq[Int]]) = {
    val dimsSizes = tensors map (_.tensor.numElements) toArray
    val contractedDimsSizes: Array[Array[Int]] = Array.fill(tensors.length, tensors.length)(1)
    for ((t1, i) <- tensors.zipWithIndex; (t2, j) <- tensors.zipWithIndex)
      contractedDimsSizes(i)(j) = contractedDims getOrElse ((t1, t2), Seq()) map t1.tensor.shape().map(_.toInt) product

    //ord(dimsSizes, contractedDimsSizes) toContractionTree tensors.toIndexedSeq
    TreeLeaf(null)
  }
}
