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
      it("should contract vector with matrix") {
        val d0:Dimension = 1
        val d1:Dimension = 2
        val d2:Dimension = 2
        NDTensor(Array(3, 4), d0, d1) ~ NDTensor(Array(1, 2, 5, 7), d2, d1) shouldEqual NDTensor(Array(11, 43), d0, d2)
      }
      it("should contract matrices into scalar") {
        val d1:Dimension = 2
        val d2:Dimension = 2
        NDTensor(Array(3, 4, 5, 6), d1, d2) ~ NDTensor(Array(1, 2, 5, 7), d1, d2) shouldEqual NDTensor.scalar(78)
      }

      it("should calculate inner product of matrices using contraction") {
        val d1:Dimension = 2
        val d2:Dimension = 3
        val d3:Dimension = 2
        NDTensor(Array(3, 4, 5, 6, 7, 8), d1, d2) ~ NDTensor(Array(1, 2, 5, 7, 2, 1), d2, d3) shouldEqual NDTensor(Array(33, 39, 57, 69), d1, d3)
      }

      it("should return contracted tensor when index swap is needed") {
        val d1: Dimension = 6
        val d2: Dimension = 7
        val d3: Dimension = 3
        val a = Array(0.73236661, 0.48607007, 0.08317922, 0.14769434, 0.0059812,
          0.24509215, 0.76482585, 0.7581358, 0.1268217, 0.7639415,
          0.03678446, 0.08570438, 0.76580253, 0.89181513, 0.10428226,
          0.9835356, 0.53268204, 0.65324358, 0.69648531, 0.5929003,
          0.06828326, 0.73716728, 0.78205737, 0.49406588, 0.04153827,
          0.93334193, 0.38648038, 0.58928974, 0.8398183, 0.65551052,
          0.45158068, 0.51722258, 0.3242585, 0.89076731, 0.88171071,
          0.79465623, 0.91378795, 0.5004566, 0.15307645, 0.35218985,
          0.79788711, 0.53677787)
        val b = Array(0.36993904, 0.662381, 0.60058366, 0.022162, 0.20605811,
          0.60203049, 0.9321842, 0.99744618, 0.51365581, 0.2945279,
          0.05442222, 0.63245651, 0.0340433, 0.58803923, 0.63547951,
          0.91115648, 0.66138241, 0.88429346, 0.64101933, 0.8162533,
          0.74285917)

        val c = Array(1.50786786, 1.47457965, 1.86595753, 2.13412684, 1.65304335,
          2.47763997, 1.58856376, 1.30248958, 2.87353186, 2.06269759,
          2.23475052, 2.89604525, 2.45255591, 2.08941639, 3.47693866,
          2.25650845, 1.98328241, 3.07973811)

        NDTensor(a, d1, d2) ~ NDTensor(b, d3, d2) shouldEqual NDTensor(c, d1, d3)
      }
    }
  }
}
