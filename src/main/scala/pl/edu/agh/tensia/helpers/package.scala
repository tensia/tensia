package pl.edu.agh.tensia

import pl.edu.agh.tensia.tensor._

package object helpers {
  def loadLib(name:String) = System load s"${System getProperty "user.dir"}/target/native/$name.so"

  def mkContractedDims(contractedDims: Map[(Tensor, Tensor), Seq[(Int, Int)]]) = contractedDims flatMap {
    case ((t1, t2), di) => Map((t1, t2) -> di.map{case (a, b) => a}, (t2, t1) -> di.map{case (a, b) => b})
  }
  def mkContractedDims(contractedDims: ((Tensor, Tensor), Seq[(Int, Int)])*):Map[(Tensor, Tensor), Seq[Int]] =
    mkContractedDims(contractedDims.toMap)
}
