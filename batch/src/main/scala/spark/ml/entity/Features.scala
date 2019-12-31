package spark.ml.entity

import org.apache.spark.ml.linalg.Vector

object Features {
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

  val concatFeatures = Array(
    Array("campaignId", "adId") -> "ca",
    Array("c1", "c2")           -> "cc"
  )

  val isNotNullFeatures = Array(
    "c3"
  )

  val quaFeatures = Array(
    "n1"
  )

  val logFeatures = Array(
    "n2"
  )
}
