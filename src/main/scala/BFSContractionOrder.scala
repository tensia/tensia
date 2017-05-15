/**
  * Created by mathek on 15/05/2017.
  */
object BFSContractionOrder {

  case class ComputedDims(dimensions: Dimensions, cost:Int)

  def findContractionOrder(dimensions: Seq[Dimensions], contractedDims: Seq[Seq[Int]]) = {
    (2 to dimensions.size).foldLeft(Vector(
      dimensions.zipWithIndex map {case (d, i) => Set(i) -> ComputedDims(d, 0)} toMap
    )) { (acc, i) =>
      1 to i/2 map { j =>
        val s1 = acc(j)
        val s2 = acc(i-j)
        for {
          (o1, ComputedDims(d1, c1)) <- s1
          (o2, ComputedDims(d2, c2)) <-s2 if o1 intersect o2 isEmpty
        } {
          yield (o1 union o2, ComputedDims(d2, c2))
        }
      }
      acc
    }
  }
}
