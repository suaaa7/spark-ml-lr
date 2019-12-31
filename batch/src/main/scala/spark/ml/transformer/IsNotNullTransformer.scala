package spark.ml.transformer

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.param.shared.{HasInputCols, HasOutputCols}
import org.apache.spark.ml.util.{
  DefaultParamsReadable,
  DefaultParamsWritable,
  Identifiable
}
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.functions.{col, lit, when}
import org.apache.spark.sql.types._

class IsNotNullTransformer(override val uid: String)
    extends Transformer
    with HasInputCols
    with HasOutputCols
    with DefaultParamsWritable {

  def this() = this(Identifiable.randomUID("IsNotNullTransformer"))
  def setInputCols(value: Array[String]): this.type  = set(inputCols, value)
  def setOutputCols(value: Array[String]): this.type = set(outputCols, value)

  override def copy(extra: ParamMap): IsNotNullTransformer =
    defaultCopy(extra)

  override def transform(dataset: Dataset[_]): DataFrame =
    $(inputCols).zip($(outputCols)).toSeq.foldLeft(dataset.toDF()) {
      (df, cols) =>
        df.withColumn(
          cols._2,
          when(col(cols._1).isNotNull, lit(1.0))
            .otherwise(lit(0.0))
        )
    }

  override def transformSchema(schema: StructType): StructType = {
    val outputFields = $(inputCols).zip($(outputCols)).map {
      case (inputCol, outputCol) =>
        StructField(outputCol, DoubleType, schema(inputCol).nullable)
    }
    StructType(schema ++ outputFields)
  }
}

object IsNotNullTransformer
    extends DefaultParamsReadable[IsNotNullTransformer] {
  override def load(path: String): IsNotNullTransformer = super.load(path)
}
