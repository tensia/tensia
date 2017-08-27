package pl.edu.agh.tensia.tensor

/**
  * Created by mathek on 26/08/2017.
  */
case class Dimension(size: Int) {
  override def toString: String = s"d$size"
}

case object Dimension {
  implicit def sizeToDimension(size: Int):Dimension = Dimension(size)
}
