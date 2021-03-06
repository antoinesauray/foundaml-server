package com.hyperplan.test.infrastructure

import java.util.UUID

import com.hyperplan.domain.models.events.{
  ClassificationPredictionEvent,
  PredictionEvent
}
import com.hyperplan.domain.models.features.FloatFeature
import com.hyperplan.domain.models.labels.ClassificationLabel
import com.hyperplan.infrastructure.serialization.events.PredictionEventSerializer
import com.hyperplan.test.SerializerTester
import io.circe.{Decoder, Encoder}
import org.scalatest.{FlatSpec, Matchers}

class PredictionEventSerializerSpec
    extends FlatSpec
    with SerializerTester
    with Matchers {

  val encoder: Encoder[PredictionEvent] =
    PredictionEventSerializer.encoder
  val decoder: Decoder[PredictionEvent] = PredictionEventSerializer.decoder

  it should "correctly encode a classification prediction event" in {

    val eventId = UUID.randomUUID().toString
    val predictionId = UUID.randomUUID().toString
    val projectId = "test-project-encode"
    val algorithmId = "test-algorithm-encode"
    val labelId = UUID.randomUUID().toString

    val prediction = ClassificationPredictionEvent(
      eventId,
      predictionId,
      projectId,
      algorithmId,
      List(
        FloatFeature("f1", 0.0f),
        FloatFeature("f2", 0.0f),
        FloatFeature("f3", .5f)
      ),
      Set(
        ClassificationLabel(
          "mylabel",
          0.5f,
          "correct_example_url",
          "incorrect_example_url"
        )
      ),
      "mylabel"
    )

    testEncoder(prediction: PredictionEvent) { json =>
      val expectedJson =
        s"""{"type":"classification","id":"$eventId","predictionId":"$predictionId","projectId":"test-project-encode","algorithmId":"test-algorithm-encode","features":[{"key":"f1","type":"float","dimension":"scalar","value":0.0},{"key":"f2","type":"float","dimension":"scalar","value":0.0},{"key":"f3","type":"float","dimension":"scalar","value":0.5}],"labels":[{"label":"mylabel","probability":0.5}],"example":"mylabel"}"""
      json.noSpaces should be(expectedJson)
    }(encoder)
  }

  it should "correctly decode a prediction" in {
    val eventId = "ce79716b-df0c-4641-9c23-e291d924326a"
    val eventJson =
      """{"type":"classification","id":"ce79716b-df0c-4641-9c23-e291d924326a","predictionId":"adf18431-754e-4375-9094-2ef71a75b62e","projectId":"test-project-encode","algorithmId":"test-algorithm-encode","features":[{"key":"f1","type":"float","dimension":"scalar","value":
0.0},{"key":"f2","type":"float","dimension":"scalar","value":0.0},{"key":"f3","type":"float","dimension":"scalar","value":0.5}],"labels":[{"label":"mylabel","probability":0.5}],"example":"mylabel"}"""
    testDecoder[PredictionEvent](eventJson) {
      case event: ClassificationPredictionEvent =>
        assert(event.id == eventId)
      case _ =>
        fail()
    }(decoder)
  }
}
