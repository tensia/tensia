/**
  * Created by mathek on 03/04/2017.
  */

object TensorContractionImplicits {
  implicit def tensorToContractableTensor(tensor: Tensor):ContractableTensor = ContractableTensor(tensor)
}

case class TensorContraction(tensor: Tensor, dimensionsCnt:Int) {
  def ~(other:Tensor) = tensor.contract(other, dimensionsCnt)
}

case class ContractableTensor(tensor:Tensor) {
  def ~(dimensionsCnt:Int) = TensorContraction(tensor, dimensionsCnt)
}
