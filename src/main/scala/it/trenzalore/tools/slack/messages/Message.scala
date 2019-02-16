package it.trenzalore.tools.slack.messages

import play.api.libs.json.Json

case class Message(
  text:        Option[String]  = None,
  attachments: Seq[Attachment] = Vector.empty
)

object Message {

  implicit val jsonWrite = Json.writes[Message]

  def italic(txt: String): String = s"_${txt}_"

  def strong(txt: String): String = s"*$txt*"

  def inlineCode(txt: String): String = s"`$txt`"

  def blockCode(txt: String): String = s"```$txt```"

  val break: String = "\n"

}