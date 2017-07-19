package pl.edu.agh.tensia.tensorflow

import java.nio.DoubleBuffer

import org.tensorflow.{Graph, Output, Session, Tensor}
import pl.edu.agh.tensia.helpers.withResource
/**
  * Created by bblaszkow on 17.07.17.
  */
case class TensorRef(tensor: Tensor) {

  def constant(name: String)(implicit graph: Graph): Output = {
    graph.opBuilder("Const", name)
      .setAttr("dtype", tensor.dataType())
      .setAttr("value", tensor)
      .build()
      .output(0)
  }
}

object TensorRef {
  private def contractOp(a: Output, b: Output, equasion: String)(implicit graph: Graph): Output = {
    graph.opBuilder("Einsum", "contraction")
      .addInput(a)
      .addInput(b)
      .setAttr("equation", equasion)
      .build()
      .output(0)
  }

  val alphabet = Stream.range('a', 'z') #::: Stream.range('A', 'Z')
  def contract(a: TensorRef, b:TensorRef, indicesPairs: Seq[(Int, Int)]): TensorRef = {
    val aToB = indicesPairs.toMap
    val letters = alphabet.iterator

    val aDims = a.tensor.numDimensions()
    val aIndices = Array.fill(aDims){'#'}

    val bDims = b.tensor.numDimensions()
    val bIndices = Array.fill(bDims){'#'}

    val resIndices = new StringBuilder()
    var i = 0
    while (i < aDims) {
      val nextLetter = letters.next()
      aIndices(i) = nextLetter
      aToB get i match {
        case Some(otherIndex) => bIndices(otherIndex) = nextLetter
        case None => resIndices.append(nextLetter)
      }
      i += 1
    }

    i = 0
    while (i < bDims) {
      if (bIndices(i) == '#') {
        val nextLetter = letters.next()
        bIndices(i) = nextLetter
        resIndices.append(nextLetter)
      }
      i += 1
    }

    val equation = s"${aIndices.mkString},${bIndices.mkString}->${resIndices.mkString}"
    withResource(new Graph()) { implicit graph =>
      val output = contractOp (
        a.constant("a"),
        b.constant("b"),
        equation
      )

      withResource(new Session(graph)) { session =>
        TensorRef(
          session.runner()
            .fetch(output)
            .run()
            .get(0)
        )
      }
    }
  }

  def rand(shape: Long*): TensorRef = {
    val elems = shape.product.toInt
    val doubleBuffer = DoubleBuffer.wrap(Array.fill(elems){math.random() * 50})
    TensorRef(
      Tensor.create(
        shape.toArray,
        doubleBuffer
      )
    )
  }
  implicit def asTensor(tensorRef: TensorRef): Tensor = tensorRef.tensor
}