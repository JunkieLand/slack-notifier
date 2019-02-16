package it.trenzalore.tools.slack

import it.trenzalore.tools.slack.messages.Message._
import it.trenzalore.tools.slack.messages.{ Attachment, Field, Message }
import org.joda.time.format.PeriodFormatterBuilder
import org.joda.time.{ DateTime, Duration }

class SlackJobNotifier(
  jobName:      String,
  webHookUrl:   String,
  shouldNotify: Boolean        = true,
  environment:  Option[String] = None
) extends SlackNotifier(webHookUrl, shouldNotify) {
  import SlackJobNotifier._

  private var startDateTime: DateTime = null

  def trackingUrl: Option[String] = None

  def notifyJobStart(text: String = "") = {
    startDateTime = DateTime.now()

    val txt = if (text.isEmpty) None else Some(text)

    notify(Message(
      attachments = Vector(Attachment(
        author_name = Some(jobName),
        title = Some("Job started"),
        title_link = trackingUrl,
        text = txt,
        fields = environment.map(env ⇒ Field(title = Some("Environment"), value = Some(env), short = true)).toVector,
        ts = Some(startDateTime.getMillis / 1000),
        fallback = Some(fallback(jobName, startDateTime, txt))
      ))
    ))
  }

  def notifyJobEndedWithSuccess(txt: String = "") = {
    val endDateTime = DateTime.now()

    val text = if (txt.isEmpty) None else Some(txt)

    val msg = Message(
      attachments = Vector(Attachment(
        author_name = Some(jobName),
        title = Some("Job successful"),
        title_link = trackingUrl,
        text = text,
        color = Some("#2EC886"),
        fields = {
          val envField = environment.map(env ⇒ Field(title = Some("Environment"), value = Some(env), short = true)).toVector
          envField ++ Vector(durationField(startDateTime, endDateTime))
        },
        ts = Some(endDateTime.getMillis / 1000),
        fallback = Some(fallback(jobName, startDateTime, text))
      ))
    )

    notify(msg)
  }

  def notifyJobEndedWithError(e: Throwable): Any = {
    val text = blockCode(s"${e.getMessage}$break${e.getStackTrace.mkString(break)}")
    notifyJobEndedWithError(text)
  }

  def notifyJobEndedWithError(text: String): Any = {
    val endDateTime = DateTime.now()

    val msg = Message(
      attachments = Vector(Attachment(
        author_name = Some(jobName),
        title = Some("Job failed"),
        title_link = trackingUrl,
        text = Some(text),
        color = Some("#FF0000"),
        fields = {
          val envField = environment.map(environmentField).toVector
          envField ++ Vector(durationField(startDateTime, endDateTime))
        },
        ts = Some(endDateTime.getMillis / 1000),
        fallback = Some(fallback(jobName, startDateTime, Some(text)))
      ))
    )

    notify(msg)
  }

  private def fallback(jobName: String, dateTime: DateTime, text: Option[String]): String = {
    val envStr = environment.map(env ⇒ s"in environment $env").getOrElse("")
    val textStr = s"saying '$text'"
    s"Job $jobName started $envStr at ${dateTime.toString} $textStr".trim.replaceAll(" +", " ")
  }

}

object SlackJobNotifier {

  private val durationFormatter = new PeriodFormatterBuilder()
    .printZeroRarelyLast()
    .appendHours()
    .appendSeparatorIfFieldsBefore("h")
    .appendMinutes()
    .appendSeparatorIfFieldsBefore("min")
    .appendSeconds()
    .appendSeparatorIfFieldsBefore("s")
    .toFormatter

  private def getDurationString(start: DateTime, end: DateTime): String = {
    val period = new Duration(start, end).toPeriod()
    durationFormatter.print(period)
  }

  def durationField(start: DateTime, end: DateTime) = Field(
    title = Some("Duration"),
    value = Some(getDurationString(start, end)),
    short = true
  )

  def environmentField(name: String) = Field(
    title = Some("Environment"),
    value = Some(name),
    short = true
  )

}