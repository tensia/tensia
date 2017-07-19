package pl.edu.agh.tensia

import pl.edu.agh.tensia.tensorflow._

package object helpers {
  def loadLib(name:String) = System load s"${System getProperty "user.dir"}/target/native/$name.so"

  def mkContractedDims(contractedDims: Map[(TensorRef, TensorRef), Seq[(Int, Int)]]) = contractedDims flatMap {
    case ((t1, t2), di) =>
      val (d1, d2) = di.unzip
      Map((t1, t2) -> d1, (t2, t1) -> d2)
  }
  def mkContractedDims(contractedDims: ((TensorRef, TensorRef), Seq[(Int, Int)])*):Map[(TensorRef, TensorRef), Seq[Int]] =
    mkContractedDims(contractedDims.toMap)

  def withResource[A <: AutoCloseable,B](closeable: A)(fun: (A) ⇒ B): B = {
    try {
      fun(closeable)
    } finally {
      closeable.close()
    }
  }
}
