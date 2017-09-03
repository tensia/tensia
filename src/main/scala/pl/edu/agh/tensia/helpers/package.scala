package pl.edu.agh.tensia

package object helpers {
  def loadLib(name:String) = System load s"${System getProperty "user.dir"}/target/native/$name.so"
}
