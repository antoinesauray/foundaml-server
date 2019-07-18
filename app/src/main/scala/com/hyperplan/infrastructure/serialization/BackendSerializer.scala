package com.hyperplan.infrastructure.serialization

import io.circe._
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

import com.hyperplan.domain.models.labels.Labels
import com.hyperplan.domain.models.backends._

import com.hyperplan.domain.models.features.transformers._
import com.hyperplan.domain.models.labels.transformers._

import com.hyperplan.infrastructure.serialization.features.FeaturesTransformerSerializer
import com.hyperplan.infrastructure.serialization.labels.LabelsTransformerSerializer

object BackendSerializer {

  object Implicits {

    val tensorFlowClassificationBackendEncoder
        : Encoder[TensorFlowClassificationBackend] =
      (backend: TensorFlowClassificationBackend) =>
        Json.obj(
          (
            "class",
            Json.fromString(TensorFlowClassificationBackend.backendClass)
          ),
          ("host", Json.fromString(backend.host)),
          ("port", Json.fromInt(backend.port)),
          (
            "featuresTransformer",
            tfTransformerEncoder.apply(backend.featuresTransformer)
          ),
          (
            "labelsTransformer",
            tfLabelsTransformerEncoder.apply(backend.labelsTransformer)
          )
        )

    val tensorFlowClassificationBackendDecoder
        : Decoder[TensorFlowClassificationBackend] =
      (c: HCursor) =>
        for {
          host <- c.downField("host").as[String]
          port <- c.downField("port").as[Int]
          featuresTransformer <- c
            .downField("featuresTransformer")
            .as[TensorFlowFeaturesTransformer]
          labelsTransformer <- c
            .downField("labelsTransformer")
            .as[TensorFlowLabelsTransformer]
        } yield
          TensorFlowClassificationBackend(
            host,
            port,
            featuresTransformer,
            labelsTransformer
          )

    val tensorFlowRegressionBackendEncoder
        : Encoder[TensorFlowRegressionBackend] =
      (backend: TensorFlowRegressionBackend) =>
        Json.obj(
          ("class", Json.fromString(TensorFlowRegressionBackend.backendClass)),
          ("host", Json.fromString(backend.host)),
          ("port", Json.fromInt(backend.port)),
          (
            "featuresTransformer",
            tfTransformerEncoder.apply(backend.featuresTransformer)
          )
        )

    val tensorFlowRegressionBackendDecoder
        : Decoder[TensorFlowRegressionBackend] =
      (c: HCursor) =>
        for {
          host <- c.downField("host").as[String]
          port <- c.downField("port").as[Int]
          featuresTransformer <- c
            .downField("featuresTransformer")
            .as[TensorFlowFeaturesTransformer]
        } yield TensorFlowRegressionBackend(host, port, featuresTransformer)

    implicit val tfTransformerEncoder: Encoder[TensorFlowFeaturesTransformer] =
      FeaturesTransformerSerializer.tfTransformerEncoder
    implicit val ftTransformerDecoder: Decoder[TensorFlowFeaturesTransformer] =
      FeaturesTransformerSerializer.tfTransformerDecoder

    implicit val tfLabelsTransformerEncoder
        : Encoder[TensorFlowLabelsTransformer] =
      LabelsTransformerSerializer.tfLabelsTransformerEncoder
    implicit val tfLabelsTransformerDecoder
        : Decoder[TensorFlowLabelsTransformer] =
      LabelsTransformerSerializer.tfLabelsTransformerDecoder

    implicit val labelsEncoder: Encoder[Labels] = LabelsSerializer.encoder
    implicit val labelsDecoder: Decoder[Labels] = LabelsSerializer.decoder

    implicit val rasaNluFeaturesTransformerEncoder
        : Encoder[RasaNluFeaturesTransformer] =
      FeaturesTransformerSerializer.rasaNluTransformerEncoder
    implicit val rasaNluFeaturesTransformerDecoder
        : Decoder[RasaNluFeaturesTransformer] =
      FeaturesTransformerSerializer.rasaNluTransformerDecoder

    val rasaNluClassificationBackendEncoder
        : Encoder[RasaNluClassificationBackend] =
      (backend: RasaNluClassificationBackend) =>
        Json.obj(
          (
            "class",
            Json.fromString(RasaNluClassifcationBackend.backendClass)
          ),
          ("host", Json.fromString(backend.host)),
          ("port", Json.fromInt(backend.port)),
          (
            "featuresTransformer",
            rasaNluFeaturesTransformerEncoder.apply(backend.featuresTransformer)
          )
        )

    val rasaNluClassificationBackendDecoder
        : Decoder[RasaNluClassificationBackend] =
      (c: HCursor) =>
        for {
          host <- c.downField("host").as[String]
          port <- c.downField("port").as[Int]
          featuresTransformer <- c
            .downField("featuresTransformer")
            .as[RasaNluFeaturesTransformer]
        } yield
          RasaNluClassificationBackend(
            host,
            port,
            featuresTransformer,
            RasaNluLabelsTransformer()
          )
  }

  import Implicits._

  implicit val decoder: Decoder[Backend] =
    (c: HCursor) =>
      c.downField("class").as[String].flatMap {
        case TensorFlowClassificationBackend.backendClass =>
          tensorFlowClassificationBackendDecoder(c)
        case TensorFlowRegressionBackend.backendClass =>
          tensorFlowRegressionBackendDecoder(c)
        case LocalClassification.backendClass => ???
        case RasaNluClassifcationBackend.backendClass =>
          rasaNluClassificationBackendDecoder(c)

      }

  implicit val encoder: Encoder[Backend] = {
    case backend: TensorFlowClassificationBackend =>
      tensorFlowClassificationBackendEncoder(backend)
    case backend: TensorFlowRegressionBackend =>
      tensorFlowRegressionBackendEncoder(backend)
    case backend: LocalClassification => ???
    case backend: RasaNluClassificationBackend =>
      rasaNluClassificationBackendEncoder(backend)
  }

  def encodeJsonNoSpaces(backend: Backend): String = {
    backend.asJson.noSpaces
  }

  def encodeJson(backend: Backend): Json = {
    backend.asJson
  }

  def decodeJson(n: String): Either[io.circe.Error, Backend] = {
    decode[Backend](n)
  }
}