/*
 *
 * This file is part of the Hyperplan project.
 * Copyright (C) 2019  Hyperplan
 * Authors: Antoine Sauray
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gn/u.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the Hyperplan software without
 * disclosing the source code of your own applications.
 *
 *
 */

package com.hyperplan.infrastructure.serialization.rasa

import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

import io.circe.parser._
import io.circe.syntax._

import cats.effect.IO
import cats.implicits._

import com.hyperplan.domain.models.features.RasaNluFeatures

object RasaNluFeaturesSerializer {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encoder: Encoder[RasaNluFeatures] =
    (features: RasaNluFeatures) =>
      Json.obj(
        ("q", Json.fromString(features.q)),
        ("project", Json.fromString(features.project)),
        ("model", Json.fromString(features.model))
      )
  implicit val decoder: Decoder[RasaNluFeatures] =
    (c: HCursor) =>
      for {
        q <- c.downField("q").as[String]
        project <- c.downField("project").as[String]
        model <- c.downField("model").as[String]
      } yield RasaNluFeatures(q, project, model)

  implicit val entityDecoder: EntityDecoder[IO, RasaNluFeatures] =
    jsonOf[IO, RasaNluFeatures]

  implicit val entityEncoder: EntityEncoder[IO, RasaNluFeatures] =
    jsonEncoderOf[IO, RasaNluFeatures]

  def encodeJson(features: RasaNluFeatures): Json = {
    features.asJson
  }

  def decodeJson(n: String): RasaNluFeatures = {
    decode[RasaNluFeatures](n).right.get
  }

}
