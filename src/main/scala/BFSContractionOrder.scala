/**
  * Created by mathek on 15/05/2017.
  */
object BFSContractionOrder {
  System.load(System.getProperty("user.dir")+"/native/build/BFS_contraction_order.so")
  @native def x(a:Int):Int

  def findContractionOrder(dimensions: Seq[Dimensions], contractedDims: Seq[Seq[Int]]) = {

  }
}
