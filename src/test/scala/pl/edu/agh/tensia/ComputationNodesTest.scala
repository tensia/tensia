package pl.edu.agh.tensia

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import pl.edu.agh.tensia.comptree.{Leaf, Node, Tree, ValProvider}

class ComputationNodesTest extends org.scalatest.FlatSpec {
  implicit val system = ActorSystem()
  val probe = TestProbe()

  "Computation tree of 2 leaves" should "sum up to 2" in {
    def op(a: Int, b: Int) = a + b
    val provider = ValProvider.of(1)
    val tree = Node(op, Leaf(provider), Leaf(provider))

    probe.childActorOf(ComputationNode.props(tree))

    probe.expectMsg(Result(2))
    succeed
  }

  "Computation tree of 1024 leaves and 10 levels" should "sum up to 1024" in {
    val N = 1024
    def op(a: Int, b: Int) = a + b
    val provider = ValProvider.of(1)
    val leaves = (1 to N).map(_ => Leaf(provider)).toList

    def group(elems: List[Tree[Int]]): List[Tree[Int]] = elems match {
      case a :: b :: tail => Node(op, a, b) :: group(tail)
      case arg => arg
    }

    lazy val leavesStream: Stream[List[Tree[Int]]] = leaves #:: leavesStream.map(group)
    val tree = leavesStream.dropWhile(_.length > 1).head.head
    probe.childActorOf(ComputationNode.props(tree))

    probe.expectMsg(Result(N))
    succeed
  }
}
