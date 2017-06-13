package tensia

import tensia.tensor.Tensor

/**
  * Created by mathek on 04/06/2017.
  */
package object contraction_order {
  implicit def tensorToTreeLeaf(tensor: Tensor):TreeLeaf = TreeLeaf(tensor)
}
