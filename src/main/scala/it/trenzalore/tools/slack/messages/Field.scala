package it.trenzalore.tools.slack.messages

import play.api.libs.json.Json

case class Field(
  title: Option[String] = None,
  value: Option[String] = None,
  short: Boolean        = true
)

object Field {

  implicit val jsonWriter = Json.writes[Field]

}