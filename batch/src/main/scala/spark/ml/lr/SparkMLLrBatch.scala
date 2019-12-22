package spark.ml.lr

import com.twitter.app.App
import com.twitter.logging.Logging
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{
  OneHotEncoderEstimator,
  StringIndexer,
  VectorAssembler
}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import spark.ml.config

object SparkMLLrBatch extends App with Logging {
  def main(): Unit = {
    val spark = SparkSession
      .builder()
      .appName("SparkMLLrBatch")
      .getOrCreate()

    log.info("Batch Started")

    val trainSchema = StructType(
      Array(
        StructField("label", DoubleType, false),
        StructField("uid", StringType, false),
        StructField("hour", IntegerType, false),
        StructField("advertiserId", IntegerType, false),
        StructField("campaignId", IntegerType, false),
        StructField("adId", IntegerType, false),
        StructField("siteId", IntegerType, false),
        StructField("c1", IntegerType, false),
        StructField("c2", IntegerType, false),
        StructField("n1", DoubleType, false),
        StructField("n2", DoubleType, false)
      )
    )

    val catFeatures = Array(
      "uid",
      "hour",
      "advertiserId",
      "campaignId",
      "adId",
      "siteId",
      "c1",
      "c2"
    )

    val trainDF = spark.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .schema(trainSchema)
      .load(s"s3a://${config.s3.bucketName}/${config.models.v1.trainDataPath}")

    val indexers =
      catFeatures.map { name =>
        new StringIndexer()
          .setInputCol(name)
          .setOutputCol(s"${name}_indexed")
          .setHandleInvalid("keep")
      }

    val encoder = new OneHotEncoderEstimator()
      .setInputCols(indexers.map(_.getOutputCol))
      .setOutputCols(catFeatures.map(name => s"${name}_processed"))

    val assembler = new VectorAssembler()
      .setInputCols(encoder.getOutputCols)
      .setOutputCol("features")

    val lr = new LogisticRegression()
      .setMaxIter(50)
      .setRegParam(0.001)
      .setStandardization(false)

    val pipeline = new Pipeline()
      .setStages(indexers ++ Array(encoder, assembler, lr))

    val model = pipeline.fit(trainDF)
    model.write
      .overwrite()
      .save(
        s"s3a://${config.s3.bucketName}/${config.models.v1.modelPath}"
      )

    log.info("Batch Completed")

    spark.stop()
  }
}
