package it.trenzalore.tools.slack.messages

import play.api.libs.json.Json

case class Attachment(
  title:       Option[String] = None,
  fallback:    Option[String] = None,
  title_link:  Option[String] = None,
  text:        Option[String] = None,
  pretext:     Option[String] = None,
  author_name: Option[String] = None,
  author_link: Option[String] = None,
  author_icon: Option[String] = None,
  color:       Option[String] = None,
  fields:      Seq[Field]     = Vector.empty,
  image_url:   Option[String] = None,
  thumb_url:   Option[String] = None,
  footer:      Option[String] = None,
  footer_icon: Option[String] = None,
  ts:          Option[Long]   = None
)

object Attachment {

  implicit val jsonWriter = Json.writes[Attachment]

}