package spark.ml

import spark.ml.entity.Environment
import spark.ml.Config.{ModelGroupsConfig, S3Config}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.ProductHint

final case class Config(
    environment: Environment,
    models: ModelGroupsConfig,
    s3: S3Config
)

object Config {
  implicit val environmentConvert: ConfigConvert[Environment] =
    ConfigConvert.viaNonEmptyStringTry[Environment](
      s => Environment.fromString(s).asScala,
      e => e.toString
    )

  def load: Config = {
    implicit def hint[T]: ProductHint[T] =
      ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

    ConfigSource.default.loadOrThrow[Config]
  }

  final case class ModelGroupsConfig(
      v1: LrModelConfig
  )

  final case class LrModelConfig(
      modelPath: String,
      trainDataPath: String,
      modelName: String
  )

  final case class S3Config(
      bucketName: String
  )
}
