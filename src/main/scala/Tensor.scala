import scala.collection.{SeqView, mutable}

/**
  * Created by mathek on 31/03/2017.
  */

trait TensorError extends Error
case class InvalidTensorSizeError(size:Int, dimensionsTotalSize:Int) extends TensorError
case class InvalidContractionArgumentError(dims1: Dimensions, dims2:Dimensions) extends TensorError
case class InvalidTensorReDimOrderError(order: Seq[Int]) extends TensorError

case class Tensor(content: Seq[Int], dimensions: Dimensions) {
  if (content.length != dimensions.totalSize) throw InvalidTensorSizeError(content.length, dimensions.totalSize)

  lazy val rank = dimensions.length
  /**
    * if current [[Tensor]] is a view on some other one, evaluates to a new [[Tensor]] with computed content, otherwise
    * evaluates to this
    */
  lazy val force = content match {
    case c:SeqView[Int, Seq[_]] @unchecked => Tensor(c toIndexedSeq, dimensions)
    case _ => this
  }

  /**
    * Contacts this with another [[Tensor]] by dimensionsCnt rightmost dimensions
    * @param other  [[Tensor]] to be contacted with this
    * @param dimensionsCnt  amount of rightmost dimensions that should be contacted
    * @return [[Tensor]] being result of contracting this and other
    */
  def contract (other: Tensor, dimensionsCnt: Int) = {

    val (remainingDims, contractedDims) = dimensions split -dimensionsCnt
    val (otherRemainingDims, otherContractedDims) = other.dimensions split -dimensionsCnt

    if (contractedDims != otherContractedDims) throw InvalidContractionArgumentError(contractedDims, otherContractedDims)

    (remainingDims ++ otherRemainingDims) makeTensor {remainingIndices =>
      val (t1indices, t2indices) = remainingIndices splitAt remainingDims.length
      contractedDims.all map { indices => this(t1indices ++ indices) * other(t2indices ++ indices)} sum
    }
  }

  /**
    * Shuffles dimensions according to given ordering
    * @param order seq which index corresponds to current dimension no, and value to its new number
    * @return [[Tensor]] being view of this with shuffled dimensions
    */
  def reDimView(order:Seq[Int]): Tensor = {
    val invOrder = order.zipWithIndex sortBy {case (v, i) => v} map {case (v, i) => i}
    dimensions reorder order makeTensorView { indices => this(invOrder map indices)}
  }

  /**
    * Works as [[Tensor.reDimView]], but computes new [[Tensor]] eagerly
    */
  def reDim(ordering:Seq[Int]) = reDimView(ordering).force

  def apply(indices:Seq[Int]) = content(dimensions indexOf indices)
}

object Tensor {
  def apply(content: Seq[Int], dims: Int*):Tensor = {
    Tensor(content, Dimensions(dims.to[IndexedSeq]))
  }
}

case class Dimensions(sizes:IndexedSeq[Int]) {

  lazy val length = sizes.length
  lazy val shiftedSizes:Seq[Int] = sizes.drop(1) ++ Seq(1)

  lazy val totalSize = sizes.product

  /**
    * Reorders dimensions according to order
    * @param order seq which index corresponds to current dimension no, and value to its new number
    * @return reordered [[Dimensions]]
    */
  def reorder(order: Seq[Int]) = {
    if (order.distinct != order || !order.forall(0 until length contains _)) {
      throw InvalidTensorReDimOrderError(order)
    }
    Dimensions(order map sizes toIndexedSeq)
  }

  def indexOf(indices:Seq[Int]):Int =
    indices zip shiftedSizes map { case (dim, dimSize) => dim*dimSize } sum

  def indicesOf(index:Int):Seq[Int] = sizes map (index % _)

  /**
    * splits dimensions into two parts, according to given length
    * @param len  if positive, length of the left split part, otherwise length of the right split part
    * @return 2-element tuple of [[Dimensions]]
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
      case 0 => Seq(Seq()) view
      case _ => (0 until totalSize).view map indicesOf
    }

  /**
    * Creates [[Tensor]] of content being result of applying mapper to each value of indices
    * @param maker  function mapping indices to value of tensor at these indices
    * @return [[Tensor]] of content produced as written above, and [[Dimensions]] of this
    */
  def makeTensorView(maker:Seq[Int] => Int):Tensor = Tensor(all map maker, this)
  def makeTensor(maker:Seq[Int] => Int):Tensor = Tensor(all map maker toIndexedSeq, this)

}
