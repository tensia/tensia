package pl.edu.agh.tensia

import org.scalatest.{FunSpec, Matchers}
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.tensor.{Dimension, NDTensor}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by mathek on 03/09/2017.
  */
class TensorNetworkSpecs extends FunSpec with Matchers {
  describe("TensorNetwork") {
    describe("contract") {
      it("should perform proper contraction v1") {
        val d: Seq[Dimension] = Seq(2, 3, 4, 5)
        val tensors = Seq(NDTensor.zero(d(1), d(2)), NDTensor.zero(d(2), d(3)), NDTensor.zero(d(0), d(1), d(3)))
        implicit val orderFinder = BFSOrderFinder
//        TODO: check result
        Await.result(TensorNetwork(tensors).contract, Duration.Inf)
      }
    }
  }
}
