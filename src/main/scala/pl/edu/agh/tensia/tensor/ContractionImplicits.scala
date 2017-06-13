package pl.edu.agh.tensia.tensor

/**
  * Created by mathek on 03/04/2017.
  */

object ContractionImplicits {
  implicit def tensorToContractableTensor(tensor: Tensor):ContractableTensor = ContractableTensor(tensor)
}

case class ContractableTensor(tensor:Tensor) {
  def ~(dimensionsCnt:Int) = new {
    def ~(other:Tensor) = tensor contract (other, dimensionsCnt)
  }
  def ~(dimensions:Seq[(Int, Int)]) = new {
    def ~(other:Tensor) = tensor contract (other, dimensions)
  }
}
