package pl.edu.agh.tensia.contraction_order

import org.scalatest._
import pl.edu.agh.tensia.tensor._
import pl.edu.agh.tensia.helpers._

/**
  * Created by mathek on 03/04/2017.
  */
class BFSAlgSpecs extends FunSpec with Matchers {

  describe("BFSAlg") {
    it("should order contractions properly v1") {
      val tensors = Seq(Tensor.zero(3, 4), Tensor.zero(4, 5), Tensor.zero(2, 3, 5))
      val Seq(t0, t1, t2) = tensors
      val contractedDims =
        mkContractedDims((t0, t1) -> Seq((1, 0)), (t0, t2) -> Seq((0, 1)), (t1, t2) -> Seq((1, 2)))
      BFSAlg.findContractionOrder(tensors, contractedDims) shouldEqual TreeNode(t2, TreeNode(t0, t1))
    }

    it("should order contractions properly v2") {
      val tensors = Seq(Tensor.zero(3, 4, 3), Tensor.zero(3, 2, 2), Tensor.zero(4, 3), Tensor.zero(3, 4, 5, 2), Tensor.zero(3, 2, 2, 4, 5))
      val Seq(t0, t1, t2, t3, t4) = tensors
      val contractedDims =
        mkContractedDims((t0, t3) -> Seq((1, 1), (2, 0)), (t0, t4) -> Seq((0, 0)), (t1, t2) -> Seq((0, 1)), (t1, t4) -> Seq((1, 2), (2, 1)),
          (t2, t4) -> Seq((0, 3)), (t3, t4) -> Seq((2, 4)))
      BFSAlg.findContractionOrder(tensors, contractedDims) shouldEqual TreeNode(
        TreeNode(t3, t0),
        TreeNode(
          TreeNode(t1, t2),
          t4
        )
      )
    }
  }
}
