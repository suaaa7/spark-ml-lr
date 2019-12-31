package spark.ml.entity

import org.apache.spark.sql.types._

final case class TrainData(
    label: Double,
    uid: String,
    hour: Int,
    advertiserId: Int,
    campaignId: Int,
    adId: Int,
    siteId: Int,
    c1: Int,
    c2: Int,
    n1: Double,
    n2: Double,
    c3: Int
)

object TrainData {
  val schema: StructType =
    StructType(
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
        StructField("n2", DoubleType, false),
        StructField("c3", IntegerType, false)
      )
    )
}
