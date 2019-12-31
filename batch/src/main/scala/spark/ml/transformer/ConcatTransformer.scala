package spark.ml.transformer

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.param.shared.{HasInputCols, HasOutputCol}
import org.apache.spark.ml.util.{
  DefaultParamsReadable,
  DefaultParamsWritable,
  Identifiable
}
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.functions.{col, concat, lit, when}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

class ConcatTransformer(override val uid: String)
    extends Transformer
    with HasInputCols
    with HasOutputCol
    with DefaultParamsWritable {

  def this() = this(Identifiable.randomUID("ConcatTransformer"))
  def setInputCols(value: Array[String]): this.type = set(inputCols, value)
  def setOutputCol(value: String): this.type        = set(outputCol, value)

  override def copy(extra: ParamMap): ConcatTransformer =
    defaultCopy(extra)

  override def transform(dataset: Dataset[_]): DataFrame = {
    val inCols = $(inputCols)
    dataset.withColumn(
      $(outputCol),
      concat(
        when(col(inCols.head).isNotNull, col(inCols.head))
          .otherwise(lit("null")),
        when(col(inCols.last).isNotNull, col(inCols.last))
          .otherwise(lit("null"))
      )
    )
  }

  override def transformSchema(schema: StructType): StructType = {
    require($(inputCols).length == 2, "InputCols must have 2 cols")
    schema.add(StructField($(outputCol), StringType, false))
  }
}

object ConcatTransformer extends DefaultParamsReadable[ConcatTransformer] {
  override def load(path: String): ConcatTransformer = super.load(path)
}
