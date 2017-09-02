package pl.edu.agh.tensia

import org.scalatest.{FunSpec, Matchers}
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.tensor.{Dimension, ScalaTensor}

/**
  * Created by mathek on 03/09/2017.
  */
class TensorNetworkSpecs extends FunSpec with Matchers {
  describe("TensorNetwork") {
    describe("contract") {
      it("should perform proper contraction v1") {
        val d: Seq[Dimension] = Seq(2, 3, 4, 5)
        val tensors = Seq(ScalaTensor.zero(d(1), d(2)), ScalaTensor.zero(d(2), d(3)), ScalaTensor.zero(d(0), d(1), d(3)))
        implicit val orderFinder = BFSOrderFinder
//        TODO: check result
        TensorNetwork(tensors).contract
      }
    }
  }
}
