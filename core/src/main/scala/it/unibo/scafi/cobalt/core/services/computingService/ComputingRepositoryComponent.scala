package it.unibo.scafi.cobalt.core.services.computingService

import java.util.UUID

import it.unibo.scafi.core.Core
import it.unibo.scafi.incarnations.{BasicAbstractIncarnation, Incarnation}

import scala.concurrent.Future

/**
  * Created by tfarneti.
  */
trait ComputingRepositoryComponent { self:Incarnation =>
  def repository: Repository

  type STATE <: State

  trait Repository{
    def get(id: ID):Future[Option[STATE]]
    def set(id: ID, state: STATE):Future[STATE]

    def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]]
  }

  trait State{
    val id: ID
    val export: EXPORT
  }
}

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

trait BasicAbstractComputingRepositoryComponent extends ComputingRepositoryComponent{ self: BasicCobaltIncarnation =>
  override type STATE = StateImpl

  case class StateImpl(id: ID,export: EXPORT) extends State
}

trait ComputingRepositoryMockComponent extends BasicAbstractComputingRepositoryComponent { self:BasicCobaltIncarnation =>
  override def repository = new MockRepo()

  class MockRepo extends Repository{
    private val db = collection.mutable.Map("1" -> new STATE("1", new EXPORT))

    override def get(id: ID): Future[Option[STATE]] = {
      Future.successful(db.get(id))
    }

    override def set(id: ID, state: STATE): Future[STATE] = {
      Future.successful(db.put(id,state).get)
    }

    override def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]] = {
      Future.successful(Map(id.head->Some(new STATE("1", new EXPORT))))
    }
  }
}

trait RedisComputingRepositoryComponent extends BasicAbstractComputingRepositoryComponent{ self:BasicCobaltIncarnation =>

  override def repository = new RedisRepository()

  class RedisRepository extends Repository{
    override def get(id: ID): Future[Option[STATE]] = ???

    override def set(id: ID, state: STATE): Future[STATE] = ???

    override def getAll(id: Set[ID]): Future[Map[ID,Option[STATE]]] = ???
  }
}