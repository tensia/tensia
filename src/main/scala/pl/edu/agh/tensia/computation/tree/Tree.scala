package pl.edu.agh.tensia.computation.tree

/**
  * Created by mathek on 02/09/2017.
  */
sealed trait Tree[+A]
case class Node[A](op: BinOp[A], left: Tree[A], right: Tree[A]) extends Tree[A]
case class Leaf[A](value: ValProvider[A]) extends Tree[A]
case object Empty extends Tree[Nothing]