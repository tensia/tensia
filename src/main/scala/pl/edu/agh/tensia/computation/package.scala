package pl.edu.agh.tensia

import akka.actor.{ActorSystem, Props}
import pl.edu.agh.tensia.computation.tree.Tree

/**
  * Created by mathek on 03/09/2017.
  */
package object computation {
  def run(tree:Tree[_]) = {
    ActorSystem("tensia_computation_system") actorOf Props(ComputationNode(tree))
  }
}
