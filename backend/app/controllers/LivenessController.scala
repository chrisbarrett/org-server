package controllers

import javax.inject.Singleton
import play.api.mvc.{ Action, Controller }

@Singleton
class LivenessController extends Controller {
  def getLiveness() = Action(Ok)
}
