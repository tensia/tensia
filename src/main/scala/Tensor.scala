import scala.collection.SeqView
import scala.collection.immutable._

/**
  * Created by mathek on 31/03/2017.
  */

trait TensorException extends Exception
case class InvalidTensorSizeException(size:Int, dimensionsTotalSize:Int) extends TensorException
case class InvalidContractionArgumentException(dims1: Dimensions, dims2:Dimensions) extends TensorException

case class Tensor(content: IndexedSeq[Int], dimensions: Dimensions) {
  if (content.length != dimensions.totalSize) throw InvalidTensorSizeException(content.length, dimensions.totalSize)

  lazy val rank = dimensions.length

  /**
    * Contacts two tensors by dimensionsCnt rightmost dimensions
    * @param other Tensor to be contacted with this
    * @param dimensionsCnt amount of rightmost dimensions that should be contacted
    * @return Tensor being result of contraction this and other
    */
  def contract (other: Tensor, dimensionsCnt: Int) = {

    val (remainingDims, contractedDims) = dimensions split -dimensionsCnt
    val (otherRemainingDims, otherContractedDims) = other.dimensions split -dimensionsCnt

    if (contractedDims != otherContractedDims) throw InvalidContractionArgumentException(contractedDims, otherContractedDims)

    (remainingDims ++ otherRemainingDims) makeTensor {remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt remainingDims.length
      contractedDims.all map { indices => this(t1indices ++ indices) * other(t2indices ++ indices)} sum
    }
  }

  def apply(indices:Seq[Int]) = content(dimensions indexOf indices)
}

object Tensor {
  def apply(content: IndexedSeq[Int], dims: Int*):Tensor = {
    Tensor(content, Dimensions(dims.to[IndexedSeq]))
  }
}

case class Dimensions(sizes:IndexedSeq[Int]) {

  lazy val length = sizes.length
  lazy val shiftedSizes:Seq[Int] = sizes.drop(1) ++ Seq(1)

  lazy val totalSize = sizes.product

  def indexOf(indices:Seq[Int]):Int =
    indices zip shiftedSizes map { case (dim, dimSize) => dim*dimSize } sum

  def indicesOf(index:Int):Seq[Int] = sizes map (index % _)

  /**
    * splits dimensions into two parts, according to given length
    * @param len - if positive, length of the left split part, otherwise length of the right split part
    * @return 2-element tuple of Dimensions
    */
  def split(len:Int):(Dimensions, Dimensions) = sizes splitAt (if (len < 0) this.length + len else len) match {
    case (leftSizes, rightSizes) => (Dimensions(leftSizes), Dimensions(rightSizes))
  }

  def ++(other:Dimensions) = Dimensions(sizes ++ other.sizes)

  /**
    * @return lazy sequence of all possible indices' values
    */
  def all: SeqView[Seq[Int], Seq[_]] =
    length match {
      case 0 => Seq() view
      case _ => (0 until totalSize) map indicesOf view
    }

  /**
    * Creates tensor of content being result of applying mapper to each value of indices
    * @param maker function mapping indices to value of tensor at these indices
    * @return Tensor of content produced as written above, and dimensions of this
    */
  def makeTensor(maker:Seq[Int] => Int):Tensor = Tensor(all map maker toIndexedSeq, this)
}
