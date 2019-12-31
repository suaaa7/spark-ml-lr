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
import org.apache.spark.sql.functions.{col, log => sparkLog}
import org.apache.spark.sql.types._

class LogarithmicTransformer(override val uid: String)
    extends Transformer
    with HasInputCols
    with HasOutputCols
    with DefaultParamsWritable {

  def this() = this(Identifiable.randomUID("LogarithmicTransformer"))
  def setInputCols(value: Array[String]): this.type  = set(inputCols, value)
  def setOutputCols(value: Array[String]): this.type = set(outputCols, value)

  override def copy(extra: ParamMap): LogarithmicTransformer =
    defaultCopy(extra)

  override def transform(dataset: Dataset[_]): DataFrame =
    $(inputCols).zip($(outputCols)).toSeq.foldLeft(dataset.toDF()) {
      (df, cols) =>
        df.withColumn(cols._2, sparkLog(col(cols._1) + 1))
    }

  override def transformSchema(schema: StructType): StructType = {
    val fields = schema($(inputCols).toSet)
    fields.foreach { fieldSchema =>
      require(
        fieldSchema.dataType.isInstanceOf[NumericType],
        s"${fieldSchema.name} does not match numeric type"
      )
    }

    val outputFields = $(inputCols).zip($(outputCols)).map {
      case (inputCol, outputCol) =>
        StructField(outputCol, DoubleType, schema(inputCol).nullable)
    }
    StructType(schema ++ outputFields)
  }
}

object LogarithmicTransformer
    extends DefaultParamsReadable[LogarithmicTransformer] {
  override def load(path: String): LogarithmicTransformer = super.load(path)
}
