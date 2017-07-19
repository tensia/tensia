package pl.edu.agh.tensia.tensorflow

import java.nio.DoubleBuffer

import org.scalatest._
import org.tensorflow.Tensor

/**
  * Created by bblaszkow on 18.07.17.
  */
class TensorRefTest extends FunSpec with Matchers {

  describe("TensorRef") {
    describe("rand") {
      it("should create random tensor") {
        val shape = Seq(5, 5)
        val longShape = shape map(_.toLong)
        val t = TensorRef.rand(longShape:_*)
      }
    }

    describe("contraction") {
      it("should multiply matrices") {
        val aBuf = Array(0.80540826,  0.55945642,  0.75818172,  0.27311218,  0.04110658,
          0.78536127,  0.75555582,  0.88895088,  0.62667   ,  0.72688545,
          0.90668985,  0.5106784)
        val bBuf = Array(0.50816526,  0.04567725,  0.05901749,  0.33120091,  0.96799273,
          0.86011825,  0.32606629,  0.86290062)
        val a = TensorRef(Tensor.create(Array(3L, 4L), DoubleBuffer.wrap(aBuf)))
        val b = TensorRef(Tensor.create(Array(4L, 2L), DoubleBuffer.wrap(bBuf)))
        val c = TensorRef.contract(a, b, Seq((1, 0)))
      }
    }
  }

}
