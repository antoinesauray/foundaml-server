package com.foundaml.server.application.controllers

import cats.Functor
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.Task
import scalaz.zio.interop.catz._
import com.foundaml.server.application.controllers.requests._
import com.foundaml.server.domain.models.errors._
import com.foundaml.server.domain.services.PredictionsService
import com.foundaml.server.infrastructure.serialization.{PredictionRequestEntitySerializer, PredictionSerializer}

class PredictionsController(
    predictionsService: PredictionsService
) extends Http4sDsl[Task] {

  val service: HttpService[Task] = {
    HttpService[Task] {
      case req @ POST -> Root =>
        (for {
          predictionRequest <- req.as[PredictionRequest](Functor[Task], PredictionRequestEntitySerializer.requestDecoder)
          prediction <- predictionsService.predict(
            predictionRequest.projectId,
            predictionRequest.features,
            predictionRequest.algorithmId
          )
          _ <- Task(
            println(
              s"Prediction computed for project ${prediction.projectId} using algorithm ${prediction.algorithmId}"
            )
          )
        } yield prediction).flatMap { prediction =>
          Created(
            PredictionSerializer.encodeJson(prediction)
          )
        }.catchAll {
          case AlgorithmDoesNotExist(algorithmId) =>
            NotFound(s"the algorithm $algorithmId does not exist")
          case BackendError(message) =>
            InternalServerError(message)
          case FeaturesValidationFailed(message) =>
            FailedDependency(message)
          case LabelsValidationFailed(message) =>
            FailedDependency(message)
          case NoAlgorithmAvailable(message) =>
            FailedDependency(message)
          case FeaturesTransformerError(message) =>
            FailedDependency(message)
          case LabelsTransformerError(message) =>
            FailedDependency(message)
        }
    }
  }

}
