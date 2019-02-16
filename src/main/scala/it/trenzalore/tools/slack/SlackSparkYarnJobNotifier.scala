package it.trenzalore.tools.slack

import org.apache.hadoop.yarn.api.records.ApplicationId
import org.apache.hadoop.yarn.client.api.YarnClient
import org.apache.spark.sql.SparkSession

import scala.util.{Failure, Success, Try}

class SlackSparkYarnJobNotifier(
  jobName:      String,
  webHookUrl:   String,
  shouldNotify: Boolean        = true,
  environment:  Option[String] = None
)(implicit spark: SparkSession) extends SlackJobNotifier(jobName, webHookUrl, shouldNotify, environment) {

  lazy val yarnClientOpt: Option[YarnClient] = {
    val _yarnClient = YarnClient.createYarnClient()
    Try(_yarnClient.init(spark.sparkContext.hadoopConfiguration)) match {
      case Success(_) => Some(_yarnClient)
      case Failure(e) =>
        logger.error("Failed instantiating Yarn client", e)
        None
    }
  }

  override def trackingUrl: Option[String] = {
    for {
      sparkAppId <- Option(spark.sparkContext.applicationId)
      applicationId <- Try(ApplicationId.fromString(sparkAppId)).toOption
      yarnClient <- yarnClientOpt
    } yield {
      yarnClient.getApplicationReport(applicationId).getTrackingUrl
    }
  }

}
