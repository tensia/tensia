/**
  * Created by mathek on 15/05/2017.
  */

case class ContractableDimensions(dimensions: Dimensions, contractedDims:Seq[(Int, Seq[Int])])

object BFSContractionOrder {
  System.load(System.getProperty("user.dir")+"/native/build/BFS_contraction_order.so")

  @native def ord(dimensionsSizes:Array[Int], contractedDimsSizes:Array[Array[Int]]):Long = throw new Error("jni fail")

  def findContractionOrder(contractableDimensions: Seq[ContractableDimensions]) = {
    val (dimsSizes, contractedDimsSizes) = contractableDimensions map { case ContractableDimensions(dims, cdims) =>
      val cdimsSizes = cdims.foldLeft(Vector.fill(contractableDimensions.length)(1)) {
        case (acc, (i, indices)) => acc.updated(i, dims.sizes map indices product)
      }
      (dims.totalSize, cdimsSizes)
    } unzip

    ord(dimsSizes.toArray, contractedDimsSizes map (_.toArray) toArray)
  }
}
