package repositories

import javax.inject.Inject
import models.{Movie, MovieTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class MovieRepository @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider,
    cc: ControllerComponents
)(
    implicit ec: ExecutionContext
) extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  private lazy val movieQuery = TableQuery[MovieTable]

  def dbInit: Future[Unit] = db.run(movieQuery.schema.createIfNotExists)

  def getAll = db.run(movieQuery.sortBy(_.id).result)

  def getOne =
    (id: String) =>
      db.run(
          movieQuery
            .filter(_.id === id)
            .result
            .headOption
      )

  def create =
    (movie: Movie) =>
      db.run(movieQuery += movie)
        .flatMap(_ => getOne(movie.id.getOrElse("")))

  def update =
    (id: String, movie: Movie) =>
      db.run(
          movieQuery
            .filter(_.id === movie.id && movie.id.contains(id))
            .update(movie)
        )
        .flatMap(_ => getOne(movie.id.getOrElse("")))

  def delete =
    (id: String) =>
      for {
        movie <- getOne(id)
        _ <- db.run(movieQuery.filter(_.id === id).delete)
      } yield movie

}
