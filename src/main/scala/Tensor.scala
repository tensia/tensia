import scala.collection.SeqView

/**
  * Created by mathek on 31/03/2017.
  */

trait TensorException extends Exception
case class InvalidTensorSizeException(size:Int, dimensionsTotalSize:Int) extends TensorException
case class InvalidContractionArgumentException(dims1: Dimensions, dims2:Dimensions) extends TensorException

case class Tensor(content: Array[Int], dimensions: Dimensions) {

  if (content.length != dimensions.totalSize) throw InvalidTensorSizeException(content.length, dimensions.totalSize)

  lazy val rank = dimensions.length

  def ~ (tensor: Tensor) = TensorPair(this, tensor)

  def apply(indices:Seq[Int]) = content(dimensions indexOf indices)
}

object Tensor {
  def apply(content: Array[Int], dims: Int*):Tensor = {
    Tensor(content, Dimensions(dims))
  }
}

case class Dimensions(sizes:Seq[Int]) {

  lazy val length = sizes.length
  lazy val shiftedSizes:Seq[Int] = sizes.drop(1) ++ Seq(1)

  lazy val totalSize = sizes.product

  def indexOf(indices:Seq[Int]):Int =
    indices zip shiftedSizes map { case (dim, dimSize) => dim*dimSize } sum

  def indicesOf(index:Int):Seq[Int] = sizes map (index % _)

  def subDimensions(len:Int):Dimensions = Dimensions(sizes takeRight len)

  def cut(len:Int):Dimensions = Dimensions(sizes take (this.length - len))

  def ++(other:Dimensions) = Dimensions(sizes ++ other.sizes)

  def map(f:Seq[Int] => Int): SeqView[Int, Seq[_]] =
    length match {
      case 0 => Seq(f(Seq())).view
      case _ => Range (0, totalSize).view map indicesOf map f
    }

  def makeTensor(maker:Seq[Int] => Int):Tensor = Tensor(this map maker toArray, this)
}

case class TensorPair(t1:Tensor, t2:Tensor) {
  def contractBy(dimensionsCnt: Int) = {

    val Seq(contractedDims, contractedDims2) = Seq(t1, t2) map (_.dimensions subDimensions dimensionsCnt)
    if (contractedDims != contractedDims2) throw InvalidContractionArgumentException(contractedDims, contractedDims2)

    val remainingDims = Seq(t1, t2) map (_.dimensions.cut(dimensionsCnt)) reduce (_++_)

    remainingDims makeTensor {remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt (t1.rank - dimensionsCnt)
      contractedDims map { indices => t1(t1indices ++ indices) * t2(t2indices ++ indices)} sum
    }
  }
}
