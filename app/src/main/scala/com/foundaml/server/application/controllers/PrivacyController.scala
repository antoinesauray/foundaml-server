package com.foundaml.server.application.controllers
import cats.Functor
import org.http4s.{HttpRoutes, HttpService}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import cats.implicits._
import cats.MonadError

import com.foundaml.server.domain.services.PrivacyService
import com.foundaml.server.controllers.requests.ForgetPredictionRequest
import com.foundaml.server.application.controllers.responses.ForgetPredictionsResponse
import com.foundaml.server.infrastructure.logging.IOLogging
import com.foundaml.server.infrastructure.serialization._

class PrivacyController(
    privacyService: PrivacyService
) extends Http4sDsl[IO]
    with IOLogging {

  val service: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "gdpr" =>
        (for {
          request <- req.as[ForgetPredictionRequest](
            MonadError[IO, Throwable],
            ForgetPredictionRequestSerializer.entityDecoder
          )
          noForgotten <- privacyService.forgetPredictionsLinkedToEntity(
            request.entityName,
            request.entityId
          )
        } yield
          ForgetPredictionsResponse(
            request.entityName,
            request.entityId,
            noForgotten
          ))
          .flatMap { response =>
            Ok(ForgetPredictionsResponseSerializer.encodeJson(response))
          }
          .handleErrorWith {
            case err =>
              logger.error(s"Unhandled error: ${err.getMessage}") *> IO
                .raiseError(err)
          }
    }
  }
}