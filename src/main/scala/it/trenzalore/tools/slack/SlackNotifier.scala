package it.trenzalore.tools.slack

import it.trenzalore.tools.slack.messages.Message
import it.trenzalore.utils.Logging
import okhttp3.{ MediaType, OkHttpClient, Request, RequestBody }
import play.api.libs.json.Json

class SlackNotifier(webHookUrl: String, shouldNotify: Boolean = true) extends Logging {

  private val jsonMediaType = MediaType.get("application/json; charset=utf-8")
  private val httpClient = new OkHttpClient()
  private val requestBuilder = new Request.Builder().url(webHookUrl)

  def notify(msg: Message): Any = if (shouldNotify) {
    logger.info("Notifying Slack")
    postJson(Json.toJson(msg).toString())
  }

  private def postJson(json: String) = {
    val body = RequestBody.create(jsonMediaType, json)
    val request = requestBuilder.post(body).build

    httpClient.newCall(request).execute
  }

}
