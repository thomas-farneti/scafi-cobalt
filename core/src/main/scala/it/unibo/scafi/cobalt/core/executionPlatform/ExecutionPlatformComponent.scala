package it.unibo.scafi.cobalt.core.executionPlatform


import it.unibo.scafi.cobalt.core.application.AggregateApplicationComponent
import it.unibo.scafi.cobalt.core.devices.DevicesComponent
import it.unibo.scafi.cobalt.core.network.NetworkComponent

/**
  * Created by tfarneti on 18/01/2017.
  */
trait ExecutionPlatformComponent { self: NetworkComponent with AggregateApplicationComponent  with DevicesComponent=>

  class AggregateApplicationExecutor(networkReadModel: NetworkReadModel, execPlatfomAggregateApp: ExecPlatfomAggregateApp, devicesReadModel: DevicesReadModel){

    def execute = {
      val devices = networkReadModel.query(GetAllDevices(execPlatfomAggregateApp.networkId)).deviceIds

      devices.foreach(id=> devicesReadModel.query(GetDevice(id)))
    }

    def start(): Unit ={

    }

    def stop(): Unit ={

    }
  }

  class ExecPlatfomAggregateApp(idp:String, networkIdp: String){
    val id: String = idp
    val networkId: String = networkIdp

    def this(query: GetAggregateApplicationResult) = {
      this(query.id,query.networkId)
    }
  }


  class ExecutionPlatformService(aggregateApplicationReadModel: AggregateApplicationReadModel, networkReadModel: NetworkReadModel){
    case class StartApplication(appId:String)

    val executionPlatform = new ExecutionPlatform()

    def handle(cmd:StartApplication): Unit ={
      val app = aggregateApplicationReadModel.query(GetAggregateApplication(cmd.appId))

      executionPlatform.startApplication(new ExecPlatfomAggregateApp(app))
    }
  }


  class ExecutionPlatform{
    private val activeApplications: collection.mutable.Map[String,AggregateApplicationExecutor] = collection.mutable.Map.empty[String,AggregateApplicationExecutor]

    def startApplication(app:ExecPlatfomAggregateApp): Unit ={
      activeApplications.find(p => p._1 == app.id) match {
        case Some(_) => return
        case None =>
          //val exec = new AggregateApplicationExecutor(new NetworkReadModel(),app)
          //activeApplications.put(app.id,exec)
      }
    }

    def stopApplication(appId:String): Unit ={

    }

  }

}
