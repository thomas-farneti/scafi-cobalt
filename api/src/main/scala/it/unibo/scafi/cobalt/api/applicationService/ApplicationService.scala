package it.unibo.scafi.cobalt.api.applicationService

/**
  * Created by tfarneti on 12/01/2017.
  */
object ApplicationServiceComponent {

    case class ExecNewApp(networkId:String)
    case class ExecNewAppResult(appId:String)

    case class StopApp(appId:String)
    case class ResumeApp(appId:String)

    case class DeleteApp(appId:String)

}
