package contraction_order

/**
  * Created by mathek on 03/06/2017.
  */
case class NativeContractionOrderResult(cost:Long, order:Array[Int]) {
  override def toString: String = s"NativeContractionOrderResult($cost, Array(${order mkString ", "}))"
}
