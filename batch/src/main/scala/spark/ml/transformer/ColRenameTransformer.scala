package spark.ml.transformer

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.ml.util.{
  DefaultParamsReadable,
  DefaultParamsWritable,
  Identifiable
}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Dataset}

class ColRenameTransformer(override val uid: String)
    extends Transformer
    with DefaultParamsWritable {

  def this() = this(Identifiable.randomUID("ColRenameTransformer"))
  def setInputCol(value: String): this.type  = set(inputCol, value)
  def setOutputCol(value: String): this.type = set(outputCol, value)
  def getOutputCol: String                   = getOrDefault((outputCol))

  val inputCol  = new Param[String](this, "inputCol", "input column")
  val outputCol = new Param[String](this, "outputCol", "output column")

  override def transform(dataset: Dataset[_]): DataFrame = {
    val inCol  = extractParamMap.getOrElse(inputCol, "input")
    val outCol = extractParamMap.getOrElse(outputCol, "output")

    dataset.drop(outCol).withColumnRenamed(inCol, outCol)
  }

  override def copy(extra: ParamMap): ColRenameTransformer     = defaultCopy(extra)
  override def transformSchema(schema: StructType): StructType = schema
}

object ColRenameTransformer
    extends DefaultParamsReadable[ColRenameTransformer] {
  override def load(path: String): ColRenameTransformer = super.load(path)
}
