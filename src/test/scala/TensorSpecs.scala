import org.scalatest._
import TensorContractionImplicits._

/**
  * Created by mathek on 03/04/2017.
  */
class TensorSpecs extends FunSpec with Matchers {

  describe("Tensor") {

    describe("apply") {
      describe("if args are valid") {
        it("should create new tensor") {
          Tensor(Vector(1, 2, 3, 4, 5, 6), 2, 3)
        }
      }
      describe("if content size does not match product of dimensions") {
        it("should throw InvalidTensorSizeException") {
          assertThrows[InvalidTensorSizeException] {
            Tensor(Vector(1, 2, 3, 4), 2, 4)
          }
        }
      }
    }

    describe("contract") {
      describe("if contraction parameters are valid") {
        it("should return contracted tensor") {
          Tensor(Vector(3, 4), 2) ~ 1 ~ Tensor(Vector(1, 2, 5, 7), 2, 2) shouldEqual Tensor(Vector(11, 43), 2)
        }
      }

      describe("if contracted dimensions' sizes differ") {
        it("should throw InvalidContractionArgumentException") {
          assertThrows[InvalidContractionArgumentException] {
            Tensor(Vector(3, 4, 5, 6, 7, 8), 2, 3) ~ 1 ~ Tensor(Vector(1, 2, 5, 7), 2, 2)
          }
        }
      }
    }

  }
}
