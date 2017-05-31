/**
  * Created by mathek on 15/05/2017.
  */

case class ContractedDims(dimIds:(Int, Int), contractedDims:Seq[Int])

object BFSContractionOrder {
  System.load(System.getProperty("user.dir")+"/target/native/BFS_contraction_order.so")

  @native def ord(dimensionsSizes:Array[Int], contractedDimsSizes:Array[Array[Int]]):Long = throw new Error("jni fail")

  def findContractionOrder(dimensions:Seq[Dimensions], contractedDims: Seq[ContractedDims]) = {
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
