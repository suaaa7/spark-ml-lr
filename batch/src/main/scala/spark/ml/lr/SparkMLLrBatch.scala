package spark.ml.lr

import com.twitter.app.App
import com.twitter.logging.Logging
import org.apache.spark.sql.SparkSession

object SparkMLLrBatch extends App with Logging {
  def main(): Unit = {
    val spark = SparkSession
      .builder()
      .appName("SparkMLLrBatch")
      .getOrCreate()

    log.info("Batch Started")

    log.info("Batch Completed")

    spark.stop()
  }
}
