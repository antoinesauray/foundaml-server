package com.foundaml.server.domain.models.features.transformers

import com.foundaml.server.domain.models.features._

case class TensorFlowFeaturesTransformer(
    signatureName: String,
    fields: Set[String]
) {

  def transform(
      features: Features
  ): Either[Throwable, TensorFlowClassificationFeatures] = {
    if (features.data.size == fields.size) {
      val examples = features match {
        case FloatFeatures(floatFeatures) =>
          floatFeatures.zip(fields).map {
            case (value, field) =>
              TensorFlowFloatFeature(field, value)
          }
        case IntFeatures(intFeatures) =>
          intFeatures.zip(fields).map {
            case (value, field) =>
              TensorFlowIntFeature(field, value)
          }
        case StringFeatures(stringFeatures) =>
          stringFeatures.zip(fields).map {
            case (value, field) =>
              TensorFlowStringFeature(field, value)
          }
        case CustomFeatures(customFeatures) =>
          customFeatures.zip(fields).map {
            case (FloatFeature(value), field) =>
              TensorFlowFloatFeature(field, value)
            case (IntFeature(value), field) =>
              TensorFlowIntFeature(field, value)
            case (StringFeature(value), field) =>
              TensorFlowStringFeature(field, value)
          }
      }
      Right(TensorFlowClassificationFeatures(signatureName, examples))
    } else {
      Left(new IllegalArgumentException("Feature transformer failed"))
    }
  }

}
