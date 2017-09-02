package pl.edu.agh.tensia.contraction.order

import org.scalatest._
import pl.edu.agh.tensia.contraction.order.tree.Node
import pl.edu.agh.tensia.tensor._

/**
  * Created by mathek on 03/04/2017.
  */
class BFSOrderFinderSpecs extends FunSpec with Matchers {

  describe("BFSOrderFinder") {
    it("should order contractions properly v1") {
      val d = Seq(Dimension(2), Dimension(3), Dimension(4), Dimension(5))
      val tensors = Seq(ScalaTensor.zero(d(1), d(2)), ScalaTensor.zero(d(2), d(3)), ScalaTensor.zero(d(0), d(1), d(3)))
      val Seq(t0, t1, t2) = tensors
      BFSOrderFinder findContractionOrder tensors shouldEqual Node(t2, Node(t0, t1))
    }

    it("should order contractions properly v2") {
      val d: Map[Symbol, Dimension] = Map('d03_a -> 4, 'd03_b -> 3, 'd04 -> 3, 'd12 -> 3, 'd14_a -> 2, 'd14_b -> 2)
      val tensors = Seq(
        ScalaTensor.zero(d('d04), d('d03_a), d('d03_b)),
        ScalaTensor.zero(d('d12), d('d14_a), d('d14_b)),
        ScalaTensor.zero(4, d('d12)),
        ScalaTensor.zero(d('d03_b), d('d03_a), 5, 2),
        ScalaTensor.zero(d('d04), d('d14_b), d('d14_a), 4, 5))
      val Seq(t0, t1, t2, t3, t4) = tensors
      BFSOrderFinder findContractionOrder tensors shouldEqual Node(
        Node(t3, t0),
        Node(
          t4,
          Node(t1, t2)
        )
      )
    }
  }
}
