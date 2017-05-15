package pl.edu.agh.tensia


package object comptree {
  type BinOp[T] = (T, T) => T

  class ValProvider[T](producer: () => T) {
    lazy val get: T = producer()
  }

  object ValProvider {
    def apply[T](producer: () => T): ValProvider[T] = new ValProvider(producer)

    def of[T](v: T): ValProvider[T] = new ValProvider[T](() => v)
  }

  sealed trait Tree[+A]
  case class Node[A](op: BinOp[A], left: Tree[A], right: Tree[A]) extends Tree[A]
  case class Leaf[A](value: ValProvider[A]) extends Tree[A]
  case object Empty extends Tree[Nothing]

}
