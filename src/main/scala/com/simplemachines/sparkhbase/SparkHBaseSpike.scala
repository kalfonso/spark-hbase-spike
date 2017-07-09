package com.simplemachines.sparkhbase

import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}


case class HBaseRecord(
                        col0: String,
                        col1: Boolean,
                        col2: Double,
                        col3: Float,
                        col4: Int,
                        col5: Long,
                        col6: Short,
                        col7: String,
                        col8: Byte)

object HBaseRecord {
  def apply(i: Int): HBaseRecord = {
    val s = s"""row${"%03d".format(i)}"""
    HBaseRecord(s,
      i % 2 == 0,
      i.toDouble,
      i.toFloat,
      i,
      i.toLong,
      i.toShort,
      s"String$i extra",
      i.toByte)
  }
}

object HBaseSource {
  val cat = s"""{
               |"table":{"namespace":"default", "name":"shcExampleTable", "tableCoder":"PrimitiveType"},
               |"rowkey":"key",
               |"columns":{
               |"col0":{"cf":"rowkey", "col":"key", "type":"string"},
               |"col1":{"cf":"cf1", "col":"col1", "type":"boolean"},
               |"col2":{"cf":"cf2", "col":"col2", "type":"double"},
               |"col3":{"cf":"cf3", "col":"col3", "type":"float"},
               |"col4":{"cf":"cf4", "col":"col4", "type":"int"},
               |"col5":{"cf":"cf5", "col":"col5", "type":"bigint"},
               |"col6":{"cf":"cf6", "col":"col6", "type":"smallint"},
               |"col7":{"cf":"cf7", "col":"col7", "type":"string"},
               |"col8":{"cf":"cf8", "col":"col8", "type":"tinyint"}
               |}
               |}""".stripMargin

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark HBase Spike")

    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    def withCatalog(cat: String): DataFrame = {
      sqlContext
        .read
        .options(Map(HBaseTableCatalog.tableCatalog->cat))
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .load()
    }

    val data = (0 to 255).map { i =>
      HBaseRecord(i)
    }

    sc.parallelize(data).toDF.write.options(
      Map(HBaseTableCatalog.tableCatalog -> cat, HBaseTableCatalog.newTable -> "5"))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save()

    val df = withCatalog(cat)
    df.show
    df.filter($"col0" <= "row005")
      .select($"col0", $"col1").show
    df.filter($"col0" === "row005" || $"col0" <= "row005")
      .select($"col0", $"col1").show
    df.filter($"col0" > "row250")
      .select($"col0", $"col1").show
    df.registerTempTable("table1")
    val c = sqlContext.sql("select count(col1) from table1 where col0 < 'row050'")
    c.show()

    sc.stop()
  }
}
