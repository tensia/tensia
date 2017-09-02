package pl.edu.agh.tensia.contraction

import org.scalatest._
import pl.edu.agh.tensia.contraction.order.{BFSOrderFinder, TreeNode}
import pl.edu.agh.tensia.tensor._
import pl.edu.agh.tensia.helpers._

/**
  * Created by mathek on 03/04/2017.
  */
class BFSAlgSpecs extends FunSpec with Matchers {

  describe("BFSOrderFinder") {
    it("should order contractions properly v1") {
      val d = Seq(Dimension(2), Dimension(3), Dimension(4), Dimension(5))
      val tensors = Seq(Tensor.zero(d(1), d(2)), Tensor.zero(d(2), d(3)), Tensor.zero(d(0), d(1), d(3)))
      val Seq(t0, t1, t2) = tensors
      BFSOrderFinder findContractionOrder tensors shouldEqual TreeNode(t2, TreeNode(t0, t1))
    }

    it("should order contractions properly v2") {
      val d: Map[Symbol, Dimension] = Map('d03_a -> 4, 'd03_b -> 3, 'd04 -> 3, 'd12 -> 3, 'd14_a -> 2, 'd14_b -> 2)
      val tensors = Seq(
        Tensor.zero(d('d04), d('d03_a), d('d03_b)),
        Tensor.zero(d('d12), d('d14_a), d('d14_b)),
        Tensor.zero(4, d('d12)),
        Tensor.zero(d('d03_b), d('d03_a), 5, 2),
        Tensor.zero(d('d04), d('d14_b), d('d14_a), 4, 5))
      val Seq(t0, t1, t2, t3, t4) = tensors
      BFSOrderFinder findContractionOrder tensors shouldEqual TreeNode(
        TreeNode(t3, t0),
        TreeNode(
          t4,
          TreeNode(t1, t2)
        )
      )
    }
  }
}
