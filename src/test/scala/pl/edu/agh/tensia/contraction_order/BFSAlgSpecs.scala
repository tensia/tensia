package pl.edu.agh.tensia.contraction_order

import org.scalatest._
import pl.edu.agh.tensia.helpers._
import pl.edu.agh.tensia.tensorflow.TensorRef

/**
  * Created by mathek on 03/04/2017.
  */
class BFSAlgSpecs extends FunSpec with Matchers {

  describe("BFSAlg") {
    it("should order contractions properly") {
      val tensors = Seq(TensorRef.rand(3, 4), TensorRef.rand(4, 5), TensorRef.rand(2, 3, 5))
      val Seq(t0, t1, t2) = tensors
      val contractedDims =
        mkContractedDims((t0, t1) -> Seq((1, 0)), (t0, t2) -> Seq((0, 1)), (t1, t2) -> Seq((1, 2)))
      //BFSAlg.findContractionOrder(tensors, contractedDims) shouldEqual TreeNode(t2, TreeNode(t0, t1))
    }
  }
}
