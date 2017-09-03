package pl.edu.agh.tensia.computation


package object tree {
  type BinOp[T] = (T, T) => T

  class ValProvider[T](producer: () => T) {
    lazy val get: T = producer()
  }

  object ValProvider {
    def apply[T](producer: () => T): ValProvider[T] = new ValProvider(producer)

    def of[T](v: T): ValProvider[T] = new ValProvider[T](() => v)
  }
}
