package spark.ml.lr

import com.twitter.app.App
import com.twitter.logging.Logging
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{
  Imputer,
  OneHotEncoderEstimator,
  StringIndexer,
  VectorAssembler
}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import spark.ml.config
import spark.ml.entity.{Features, TrainData}
import spark.ml.transformer.{
  ConcatTransformer,
  IsNotNullTransformer,
  LogarithmicTransformer
}

object SparkMLLrBatch extends App with Logging {
  def main(): Unit = {
    val spark = SparkSession
      .builder()
      .appName("SparkMLLrBatch")
      .getOrCreate()

    log.info("Batch Started")

    val trainDF = spark.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .schema(TrainData.schema)
      .load(s"s3a://${config.s3.bucketName}/${config.models.v1.trainDataPath}")

    val concaters = Features.concatFeatures.map { feature =>
      new ConcatTransformer()
        .setInputCols(feature._1)
        .setOutputCol(feature._2)
    }

    val indexers =
      (Features.catFeatures ++ (Features.concatFeatures.map(_._2))).map {
        name =>
          new StringIndexer()
            .setInputCol(name)
            .setOutputCol(s"${name}_indexed")
            .setHandleInvalid("keep")
      }

    val encoder = new OneHotEncoderEstimator()
      .setInputCols(indexers.map(_.getOutputCol))
      .setOutputCols(
        (Features.catFeatures ++ (Features.concatFeatures.map(_._2)))
          .map(name => s"${name}_processed")
      )

    val isNotNuller = new IsNotNullTransformer()
      .setInputCols(Features.isNotNullFeatures)
      .setOutputCols(
        Features.isNotNullFeatures.map(name => "${name}_processed")
      )

    val logger = new LogarithmicTransformer()
      .setInputCols(Features.logFeatures)
      .setOutputCols(Features.logFeatures.map(name => s"${name}_log"))

    val imputer = new Imputer()
      .setInputCols(Features.quaFeatures ++ logger.getOutputCols)
      .setOutputCols(
        (Features.quaFeatures ++ Features.logFeatures)
          .map(name => s"${name}_processed")
      )

    val assembler = new VectorAssembler()
      .setInputCols(
        encoder.getOutputCols ++ isNotNuller.getOutputCols ++ imputer.getOutputCols
      )
      .setOutputCol("features")

    val lr = new LogisticRegression()
      .setMaxIter(100)
      .setRegParam(0.001)
      .setStandardization(false)

    val stages = concaters ++ indexers ++ Array(
      encoder,
      isNotNuller,
      logger,
      imputer,
      assembler,
      lr
    )

    val pipeline = new Pipeline()
      .setStages(stages)

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
