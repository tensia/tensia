import org.scalatest._
import tensor._
import tensor.ContractionImplicits._

/**
  * Created by mathek on 03/04/2017.
  */
class TensorSpecs extends FunSpec with Matchers {

  describe("Tensor") {

    describe("new") {
      it("should create new tensor") {
        Tensor(Vector(1, 2, 3, 4, 5, 6), 2, 3)
      }
      describe("if content size does not match product of dimensions") {
        it("should throw error") {
          assertThrows[InvalidTensorSizeError] {
            Tensor(Vector(1, 2, 3, 4), 2, 4)
          }
        }
      }
    }

    describe("apply") {
      it("should return value of tensor at specified indices") {
        Tensor(Vector(5))(Seq()) shouldEqual 5
        Tensor(Vector(5), 1)(Seq(0)) shouldEqual 5
        Tensor(Vector(1, 2, 3, 4, 5, 6, 7, 8), 2, 4)(Seq(1, 2)) shouldEqual 7
      }
    }

    describe("contract") {
      describe("by count of dimensions") {
        it("should return contracted tensor") {
          Tensor(Vector(3, 4), 2) ~ 1 ~ Tensor(Vector(1, 2, 5, 7), 2, 2) shouldEqual Tensor(Vector(11, 43), 2)
          Tensor(Vector(3, 4, 5, 6), 2, 2) ~ 2 ~ Tensor(Vector(1, 2, 5, 7), 2, 2) shouldEqual Tensor(Vector(78))
        }

        describe("if contracted dimensions' sizes differ") {
          it("should throw error") {
            assertThrows[InvalidContractionArgumentError] {
              Tensor(Vector(3, 4, 5, 6, 7, 8), 2, 3) ~ 1 ~ Tensor(Vector(1, 2, 5, 7), 2, 2)
            }
          }
        }
      }
      describe("by dimensions' pairs") {
        it("should return contracted tensor") {
          Tensor(Vector(3, 4, 5, 6, 7, 8), 2, 3) ~ Seq((1, 0)) ~ Tensor(Vector(1, 2, 5, 7, 2, 1), 3, 2) shouldEqual Tensor(Vector(33, 39, 57, 69), 2, 2)
        }
      }
    }

    describe("reDim") {
      it("should create Tensor with reordered dimensions") {
        Tensor(Vector(1, 2, 5, 7, 7, 6), 3, 2) reDim Seq(1, 0) shouldEqual Tensor(Vector(1, 5, 7, 2, 7, 6), 2, 3)
      }
      describe("if order is invalid") {
        it("should throw error") {
          assertThrows[InvalidTensorReDimOrderError] {
            Tensor(Vector(1, 2, 5, 7, 7, 6), 3, 2) reDim Seq(1, 1)
          }
          assertThrows[InvalidTensorReDimOrderError] {
            Tensor(Vector(1, 2, 5, 7, 7, 6), 3, 2) reDim Seq(1, 3)
          }
        }

      }
    }

  }
}
