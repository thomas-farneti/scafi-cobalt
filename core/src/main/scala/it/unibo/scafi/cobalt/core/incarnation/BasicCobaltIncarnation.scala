package it.unibo.scafi.cobalt.core.incarnation

import it.unibo.scafi.incarnations.Incarnation

/**
  * Created by tfarneti.
  */
trait BasicCobaltIncarnation extends Incarnation{
  override type LSNS = String
  override type NSNS = String
  override type ID = String
  override type EXECUTION = AggregateProgram

  implicit val NBR_RANGE_NAME: NSNS = "nbrRange"

  trait AggregateProgramSpec extends AggregateProgramSpecification with Builtins

  trait AggregateProgram extends ExecutionTemplate with Builtins with Serializable {
    override type MainResult = Any
  }

  @transient implicit override val linearID: Linearizable[ID] = new Linearizable[ID] {
    override def toNum(v: ID): Int = v.toInt
    override def fromNum(n: Int): ID = ""+n
  }
  @transient implicit override val interopID: Interop[ID] = new Interop[ID] {
    def toString(id: ID): String = id
    def fromString(str: String) = str
  }
  @transient implicit override val interopLSNS: Interop[LSNS] = new Interop[LSNS] {
    def toString(lsns: LSNS): String = lsns.toString
    def fromString(str: String): LSNS = str
  }
  @transient implicit override val interopNSNS: Interop[NSNS] = new Interop[NSNS] {
    def toString(nsns: NSNS): String = nsns.toString
    def fromString(str: String): NSNS = str
  }
}
