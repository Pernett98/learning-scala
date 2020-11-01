package controllers

import javax.inject._
import models.Movie
import play.api.mvc._
import play.api.libs.json.Json
import repositories.MovieRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class MovieController @Inject()(
    cc: ControllerComponents,
    movieRepository: MovieRepository
) extends AbstractController(cc) {

  implicit val serializer = Json.format[Movie]
  val logger = play.Logger.of("MovieController")

  def getMovies =
    Action.async(
      movieRepository.getAll
        .map(
          movies =>
            Ok(
              Json.obj(
                "message" -> "success",
                "data" -> movies
              )))
        .recover { ex =>
          logger.error("failure on getMovies", ex)
          InternalServerError(s"Error:${ex.getLocalizedMessage}")
        })

  def getMovie =
    (id: String) =>
      Action.async(
        movieRepository
          .getOne(id)
          .map(
            movie =>
              Ok(
                Json.obj(
                  "message" -> "success",
                  "data" -> movie
                )))
          .recover { ex =>
            logger.error("failure on getMovie", ex)
            InternalServerError(s"Error:${ex.getLocalizedMessage}")
          })

  def createMovie =
    Action.async(parse.json)(request =>
      request.body.validate[Movie].asEither match {
        case Left(error) => Future.successful(BadRequest(error.toString()))
        case Right(movie) =>
          movieRepository
            .create(movie)
            .map(
              movie =>
                Ok(
                  Json.obj(
                    "message" -> "success",
                    "data" -> movie
                  )))
            .recover { ex =>
              logger.error("failure on createMovie", ex)
              InternalServerError(s"Error:${ex.getLocalizedMessage}")
            }
    })

  def updateMovie =
    (id: String) =>
      Action.async(parse.json)(
        request =>
          request.body.validate[Movie].asEither match {
            case Left(error) => Future.successful(BadRequest(error.toString()))
            case Right(movie) =>
              movieRepository
                .update(id, movie)
                .map(
                  movie =>
                    Ok(
                      Json.obj(
                        "message" -> "success",
                        "data" -> movie
                      )))
                .recover { ex =>
                  logger.error("failure on updateMovie", ex)
                  InternalServerError(s"Error:${ex.getLocalizedMessage}")
                }
        }
    )

  def deleteMovie =
    (id: String) => Action.async(movieRepository
      .delete(id)
      .map(movie => Ok(Json.obj(
        "message" -> "success",
        "data" -> movie
      )))
      .recover { ex =>
        logger.error("failure on deleteMovie", ex)
        InternalServerError(s"Error:${ex.getLocalizedMessage}")
      }
    )

}