package pl.edu.agh.tensia


package object comptree {
  type Operation[T] = (T*) => T

  class ValProvider[T](producer: () => T) {
    lazy val get: T = producer()
  }

  object ValProvider {
    def apply[T](producer: () => T): ValProvider[T] = new ValProvider(producer)

    def of[T](v: T): ValProvider[T] = new ValProvider[T](() => v)
  }

  sealed trait Tree[+A]
  case class Node[A](op: Operation[A], children: Tree[A]*) extends Tree[A]
  case class Leaf[A](value: ValProvider[A]) extends Tree[A]
  case object Empty extends Tree[Nothing]

}
