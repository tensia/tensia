package pl.edu.agh.tensia.tensor

import org.nd4j.linalg.factory.Nd4j
import org.scalatest._


class NDTensorSpecs extends FunSpec with Matchers {

  describe("NDTensor") {

    describe("new") {
      it("should create new tensor") {
        NDTensor(Nd4j.create(Array[Float](1, 2, 3, 4, 5, 6)), Dimensions.of(2, 3))
      }
    }

    describe("contract") {
        it("should return contracted tensor v1") {
          val d1:Dimension = 2
          val d2:Dimension = 2
          NDTensor(Array(3, 4), d1) ~ NDTensor(Array(1, 2, 5, 7), d2, d1) shouldEqual NDTensor(Array(11, 43), d2)
          NDTensor(Array(3, 4, 5, 6), d1, d2) ~ NDTensor(Array(1, 2, 5, 7), d1, d2) shouldEqual NDTensor(Array(78))
        }

      it("should return contracted tensor v2") {
        val d1:Dimension = 2
        val d2:Dimension = 3
        val d3:Dimension = 2
        NDTensor(Array(3, 4, 5, 6, 7, 8), d1, d2) ~ NDTensor(Array(1, 2, 5, 7, 2, 1), d2, d3) shouldEqual NDTensor(Array(33, 39, 57, 69), d1, d3)
      }
    }
  }
}
