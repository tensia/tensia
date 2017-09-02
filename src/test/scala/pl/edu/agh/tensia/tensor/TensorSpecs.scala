package pl.edu.agh.tensia.tensor

import org.scalatest._

/**
  * Created by mathek on 03/04/2017.
  */
class ScalaTensorSpecs extends FunSpec with Matchers {

  describe("ScalaTensor") {

    describe("new") {
      it("should create new tensor") {
        ScalaTensor(Vector(1, 2, 3, 4, 5, 6), 2, 3)
      }
      describe("if content size does not match product of dimensions") {
        it("should throw error") {
          assertThrows[InvalidTensorSizeError] {
            ScalaTensor(Vector(1, 2, 3, 4), 2, 4)
          }
        }
      }
    }

    describe("apply") {
      it("should return value of tensor at specified indices") {
        ScalaTensor(Vector(5))(Seq()) shouldEqual 5
        ScalaTensor(Vector(5), 1)(Seq(0)) shouldEqual 5
        ScalaTensor(Vector(1, 2, 3, 4, 5, 6, 7, 8), 2, 4)(Seq(1, 2)) shouldEqual 7
      }
    }

    describe("contract") {
        it("should return contracted tensor v1") {
          val d1:Dimension = 2
          val d2:Dimension = 2
          ScalaTensor(Vector(3, 4), d1) ~ ScalaTensor(Vector(1, 2, 5, 7), d2, d1) shouldEqual ScalaTensor(Vector(11, 43), d2)
          ScalaTensor(Vector(3, 4, 5, 6), d1, d2) ~ ScalaTensor(Vector(1, 2, 5, 7), d1, d2) shouldEqual ScalaTensor(Vector(78))
        }

      it("should return contracted tensor v2") {
        val d1:Dimension = 2
        val d2:Dimension = 3
        val d3:Dimension = 2
        ScalaTensor(Vector(3, 4, 5, 6, 7, 8), d1, d2) ~ ScalaTensor(Vector(1, 2, 5, 7, 2, 1), d2, d3) shouldEqual ScalaTensor(Vector(33, 39, 57, 69), d1, d3)
      }
    }

    describe("reDim") {
      it("should create the same tensor if given the same dimensions") {
        val d1 = Dimension(3)
        val d2 = Dimension(2)
        val t = ScalaTensor(Vector(1, 2, 3, 4, 5, 6), d1, d2)
        t reDim Dimensions.of(d1, d2) shouldEqual t
      }

      it("should create ScalaTensor with reordered dimensions v1") {
        val d1 = Dimension(3)
        val d2 = Dimension(2)
        ScalaTensor(Vector(1, 2, 5, 7, 7, 6), d1, d2) reDim Dimensions.of(d2, d1) shouldEqual
          ScalaTensor(Vector(1, 5, 7, 2, 7, 6), d2, d1)
      }

      it("should create ScalaTensor with reordered dimensions v2") {
        val d1 = Dimension(2)
        val d2 = Dimension(2)
        val d3 = Dimension(3)
        ScalaTensor(Vector(1, 2, 5, 7, 7, 6, 4, 2, 1, 9, 8, 0), d1, d2, d3) reDim Dimensions.of(d2, d3, d1) shouldEqual
          ScalaTensor(Vector(1, 4, 2, 2, 5, 1, 7, 9, 7, 8, 6, 0), d2, d3, d1)
      }
      describe("if order is invalid") {
        it("should throw error") {
          assertThrows[InvalidTensorDimensionsError] {
            val d = Dimension(3)
            ScalaTensor(Vector(1, 2, 5, 7, 7, 6), d, 2) reDim Dimensions.of(1, d)
          }
          assertThrows[InvalidTensorDimensionsError] {
            ScalaTensor(Vector(1, 2, 5, 7, 7, 6), 3, 2) reDim Dimensions.of(2, 3)
          }
        }

      }
    }

  }
}
