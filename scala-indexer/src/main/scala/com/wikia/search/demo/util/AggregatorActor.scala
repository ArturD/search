package com.wikia.search.demo.util

import actors.Actor
import actors.Actor._
import collection.mutable.ListBuffer

/**
 * Aggregates messages into fixed size chunks and sends chunks to consumer.
 * @param aggregateSize
 * @param consumer
 * @tparam T
 */
class AggregatorActor[T]( val aggregateSize: Int, val consumer: Actor ) extends Actor {
  var currentList:ListBuffer[T] = ListBuffer[T]()

  def act() {
    loop {
      react {
        case None => finish()
        case element:T => addAndCheck(element)
        case _ => println("unknown element in aggregator")
      }
    }
  }

  def closeAndProduce() = {
    val list:ListBuffer[T] = currentList
    currentList = ListBuffer[T]()
    println("sending:" + list.length)
    consumer ! list.result()
  }

  def addAndCheck( element:T ) = {
    currentList.append(element)
    if ( currentList.length >= aggregateSize ) {
      closeAndProduce()
    }
  }

  def finish() = {
    closeAndProduce()
    consumer ! None
    exit()
  }
}
