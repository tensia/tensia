/**
  * Created by mathek on 03/06/2017.
  */

package tensia

package object helpers {
  def loadLib(name:String) = System load s"${System getProperty "user.dir"}/target/native/$name.so"
}
