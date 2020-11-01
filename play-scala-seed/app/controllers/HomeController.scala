package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import repositories.MovieRepository

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(
    cc: ControllerComponents,
    movieRepository: MovieRepository
) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def dbInit() =
    Action.async(
      request =>
        movieRepository.dbInit
          .map(_ => Created("table has been created"))
          .recover { ex =>
            play.Logger.of("dbInit").debug("Error creating the DB", ex)
            InternalServerError("Error creating the DB [internal error]")
        })
}
