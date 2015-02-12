package com.innoq.numbergame.permutation

import java.util.Random
import scala.collection._

/**
 * Permutes the numbers 0 .. base-1
 */
class Permutation(base: Int) {
  val forward: Array[Int] = {
    val result: Array[Int]  = new Array[Int](base)
    val unmapped: mutable.ArrayBuffer[Int]  = new mutable.ArrayBuffer[Int](base)
    result.indices.foreach(unmapped += _)
    result.indices.foreach((x: Int) => {
      val r: Random = new Random()
      val j: Int = if(0 < unmapped.size) r.nextInt(unmapped.size) else 0
      result(x) = (unmapped remove j)
    })
    result
  }
  val reverse:Array[Int] = {
    val result: Array[Int] = new Array[Int](base) 
    result.indices.foreach((i: Int) => result(forward(i)) = i)
    result
  }
  
  def apply(x:Int) = forward(x)
  def unapply(y:Int) = reverse(y)
}
